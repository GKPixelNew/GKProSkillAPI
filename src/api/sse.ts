import { getTargetGame } from '../version/data';
import { writable, get } from 'svelte/store';
import { userManager } from '$api/oauth';

export interface UpdateEvent {
    type: 'skill' | 'class' | 'attribute';
    game: string;
    resourceId: string;
    uploadedBy: string;
    uploadedAt: string;
    action: 'update' | 'delete';
}

// Store for pending updates (items that have newer versions on server)
export const pendingUpdates = writable<Map<string, UpdateEvent>>(new Map());

// Store for SSE connection status
export const sseConnected = writable<boolean>(false);

let eventSource: EventSource | null = null;
let reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
let currentGame: string | null = null;

// Callbacks for update events
type UpdateCallback = (event: UpdateEvent) => void;
const updateCallbacks: UpdateCallback[] = [];

export const onUpdate = (callback: UpdateCallback) => {
    updateCallbacks.push(callback);
    return () => {
        const index = updateCallbacks.indexOf(callback);
        if (index > -1) updateCallbacks.splice(index, 1);
    };
};

/**
 * Get current user's username for filtering own events
 */
const getCurrentUsername = async (): Promise<string | null> => {
    try {
        const user = await userManager?.getUser();
        return user?.profile?.preferred_username || user?.profile?.name || null;
    } catch {
        return null;
    }
};

/**
 * Connect to SSE endpoint for real-time updates
 */
export const connectSSE = async (game?: string) => {
    const targetGame = game || getTargetGame();
    
    console.log(`[SSE] connectSSE called with game: ${game}, targetGame: ${targetGame}`);
    console.log(`[SSE] Current state - eventSource: ${eventSource ? 'exists' : 'null'}, currentGame: ${currentGame}`);
    
    // Don't reconnect if already connected to same game
    if (eventSource && currentGame === targetGame) {
        console.log(`[SSE] Already connected to ${targetGame}, skipping`);
        return;
    }
    
    // Disconnect existing connection
    disconnectSSE();
    
    currentGame = targetGame;
    
    // SSE endpoint is public (scoped by game)
    const url = `https://cdn.gkpixel.com/v2/events/subscribe/${targetGame}`;
    
    console.log(`[SSE] Creating EventSource for URL: ${url}`);
    
    // First, test if the endpoint is reachable with a quick fetch
    try {
        console.log(`[SSE] Testing endpoint with fetch first...`);
        const testResponse = await fetch(url, { 
            method: 'GET',
            headers: { 'Accept': 'text/event-stream' }
        });
        console.log(`[SSE] Fetch test response:`, {
            status: testResponse.status,
            statusText: testResponse.statusText,
            headers: Object.fromEntries(testResponse.headers.entries())
        });
        // Abort the fetch since we just wanted to test
        testResponse.body?.cancel();
    } catch (fetchErr) {
        console.error(`[SSE] Fetch test failed:`, fetchErr);
    }
    
    try {
        eventSource = new EventSource(url);
        console.log(`[SSE] EventSource created, readyState: ${eventSource.readyState} (0=CONNECTING, 1=OPEN, 2=CLOSED)`);
        
        // Log readyState changes periodically
        const checkInterval = setInterval(() => {
            if (eventSource) {
                console.log(`[SSE] Checking readyState: ${eventSource.readyState}`);
                if (eventSource.readyState === 1) {
                    console.log(`[SSE] Connection is OPEN!`);
                    sseConnected.set(true);
                    clearInterval(checkInterval);
                } else if (eventSource.readyState === 2) {
                    console.log(`[SSE] Connection is CLOSED`);
                    clearInterval(checkInterval);
                }
            } else {
                clearInterval(checkInterval);
            }
        }, 1000);
        
        // Clear interval after 30 seconds regardless
        setTimeout(() => clearInterval(checkInterval), 30000);
        
    } catch (err) {
        console.error(`[SSE] Failed to create EventSource:`, err);
        return;
    }
    
    eventSource.onopen = (e) => {
        console.log(`[SSE] onopen fired! readyState: ${eventSource?.readyState}`, e);
        sseConnected.set(true);
        
        // Clear any pending reconnect
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
            reconnectTimeout = null;
        }
    };
    
    eventSource.addEventListener('connected', (e) => {
        console.log('[SSE] "connected" event received:', e.data);
        sseConnected.set(true);
    });
    
    eventSource.addEventListener('update', async (e) => {
        console.log('[SSE] "update" event received:', e.data);
        try {
            const event: UpdateEvent = JSON.parse(e.data);
            console.log('[SSE] Parsed update event:', event);
            
            // Ignore events from current user (own uploads)
            const currentUser = await getCurrentUsername();
            if (currentUser && event.uploadedBy === currentUser) {
                console.log('[SSE] Ignoring own update event from:', currentUser);
                return;
            }
            
            // Store the update
            const key = `${event.type}:${event.resourceId}`;
            const updates = get(pendingUpdates);
            
            if (event.action === 'delete') {
                // Remove from pending if deleted
                updates.delete(key);
            } else {
                updates.set(key, event);
            }
            pendingUpdates.set(new Map(updates));
            
            // Notify callbacks
            updateCallbacks.forEach(cb => cb(event));
        } catch (err) {
            console.error('[SSE] Failed to parse event:', err);
        }
    });
    
    // Also listen for generic message events
    eventSource.onmessage = (e) => {
        console.log('[SSE] onmessage (generic) received:', e.data);
    };
    
    eventSource.onerror = (e) => {
        console.error('[SSE] onerror fired! readyState:', eventSource?.readyState, 'event:', e);
        
        // Log more details about the error
        if (eventSource) {
            console.error('[SSE] EventSource state:', {
                readyState: eventSource.readyState,
                url: eventSource.url,
                withCredentials: eventSource.withCredentials
            });
        }
        
        sseConnected.set(false);
        
        // Auto-reconnect after 5 seconds
        if (!reconnectTimeout) {
            console.log('[SSE] Scheduling reconnect in 5 seconds...');
            reconnectTimeout = setTimeout(() => {
                reconnectTimeout = null;
                console.log('[SSE] Attempting to reconnect...');
                connectSSE(currentGame || undefined);
            }, 5000);
        }
    };
};

/**
 * Disconnect from SSE
 */
export const disconnectSSE = () => {
    if (eventSource) {
        eventSource.close();
        eventSource = null;
        currentGame = null;
        sseConnected.set(false);
        console.log('SSE: Disconnected');
    }
    
    if (reconnectTimeout) {
        clearTimeout(reconnectTimeout);
        reconnectTimeout = null;
    }
};

/**
 * Check if a resource has pending updates
 */
export const hasPendingUpdate = (type: string, resourceId: string): boolean => {
    const key = `${type}:${resourceId}`;
    return get(pendingUpdates).has(key);
};

/**
 * Get pending update for a resource
 */
export const getPendingUpdate = (type: string, resourceId: string): UpdateEvent | undefined => {
    const key = `${type}:${resourceId}`;
    return get(pendingUpdates).get(key);
};

/**
 * Clear pending update for a resource (call after user reloads)
 */
export const clearPendingUpdate = (type: string, resourceId: string) => {
    const key = `${type}:${resourceId}`;
    const updates = get(pendingUpdates);
    updates.delete(key);
    pendingUpdates.set(new Map(updates));
};

/**
 * Clear all pending updates
 */
export const clearAllPendingUpdates = () => {
    pendingUpdates.set(new Map());
};
