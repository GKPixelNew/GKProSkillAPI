import { writable, get } from 'svelte/store';
import { browser } from '$app/environment';

export interface VersionInfo {
    uploadedAt: string;
    uploadedBy: string;
}

const STORAGE_KEY = 'cdn-version-tracker';

// Load from localStorage on init
const loadFromStorage = (): Map<string, VersionInfo> => {
    if (!browser) return new Map();
    try {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored) {
            const parsed = JSON.parse(stored);
            return new Map(Object.entries(parsed));
        }
    } catch (e) {
        console.error('Failed to load version tracker from storage', e);
    }
    return new Map();
};

// Save to localStorage
const saveToStorage = (versions: Map<string, VersionInfo>) => {
    if (!browser) return;
    try {
        const obj = Object.fromEntries(versions);
        localStorage.setItem(STORAGE_KEY, JSON.stringify(obj));
    } catch (e) {
        console.error('Failed to save version tracker to storage', e);
    }
};

// Store version info for each resource: "type:id" -> VersionInfo
const versionStore = writable<Map<string, VersionInfo>>(loadFromStorage());

/**
 * Track the version of a resource when it's loaded/uploaded
 */
export const trackVersion = (type: string, resourceId: string, uploadedAt: string, uploadedBy: string) => {
    const key = `${type}:${resourceId}`;
    const versions = get(versionStore);
    versions.set(key, { uploadedAt, uploadedBy });
    versionStore.set(new Map(versions));
    saveToStorage(versions);
};

/**
 * Get the tracked version of a resource
 */
export const getTrackedVersion = (type: string, resourceId: string): VersionInfo | undefined => {
    const key = `${type}:${resourceId}`;
    return get(versionStore).get(key);
};

/**
 * Clear tracked version for a resource
 */
export const clearTrackedVersion = (type: string, resourceId: string) => {
    const key = `${type}:${resourceId}`;
    const versions = get(versionStore);
    versions.delete(key);
    versionStore.set(new Map(versions));
    saveToStorage(versions);
};

/**
 * Get all tracked versions
 */
export const getAllTrackedVersions = (): Map<string, VersionInfo> => {
    return get(versionStore);
};

/**
 * Check if a resource has been modified on server since we loaded it
 * @returns Object with isModified flag and details if modified
 */
export const checkVersionConflict = (
    type: string, 
    resourceId: string, 
    serverUploadedAt: string, 
    serverUploadedBy: string
): { isModified: boolean; localVersion?: VersionInfo; serverVersion?: VersionInfo } => {
    const localVersion = getTrackedVersion(type, resourceId);
    
    if (!localVersion) {
        // No tracked version, consider it as new
        return { isModified: false };
    }
    
    const localDate = new Date(localVersion.uploadedAt).getTime();
    const serverDate = new Date(serverUploadedAt).getTime();
    
    if (serverDate > localDate) {
        return {
            isModified: true,
            localVersion,
            serverVersion: { uploadedAt: serverUploadedAt, uploadedBy: serverUploadedBy }
        };
    }
    
    return { isModified: false };
};
