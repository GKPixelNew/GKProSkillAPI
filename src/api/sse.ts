import { getTargetGame } from '../version/data';
import { writable, get } from 'svelte/store';

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
 * Connect to SSE endpoint for real-time updates
 */
export const connectSSE = async (game?: string) => {
    const targetGame = game || getTargetGame();
    
    // Don't reconnect if already connected to same game
    if (eventSource && currentGame === targetGame) {
        return;
    }
    
    // Disconnect existing connection
    disconnectSSE();
    
    currentGame = targetGame;
    
    // SSE endpoint is public (scoped by game)
    const url = `https://cdn.gkpixel.com/v2/events/subscribe/${targetGame}`;
    
    console.log(`SSE: Connecting to ${targetGame}...`);
    
    eventSource = new EventSource(url);
    
    eventSource.onopen = () => {
        console.log(`SSE: Connected to ${targetGame}`);
        sseConnected.set(true);
        
        // Clear any pending reconnect
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
            reconnectTimeout = null;
        }
    };
    
    eventSource.addEventListener('connected', (e) => {
        console.log('SSE: Connection confirmed', e.data);
    });
    
    eventSource.addEventListener('update', (e) => {
        try {
            const event: UpdateEvent = JSON.parse(e.data);
            console.log('SSE: Received update', event);
            
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
            console.error('SSE: Failed to parse event', err);
        }
    });
    
    eventSource.onerror = (e) => {
        console.error('SSE: Connection error', e);
        sseConnected.set(false);
        
        // Auto-reconnect after 5 seconds
        if (!reconnectTimeout) {
            reconnectTimeout = setTimeout(() => {
                reconnectTimeout = null;
                console.log('SSE: Attempting to reconnect...');
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
