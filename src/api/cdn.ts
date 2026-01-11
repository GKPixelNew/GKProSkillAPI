import axios, {AxiosError, type AxiosInstance} from "axios";
import {userManager} from "$api/oauth";
import { active, getAttributeYaml, loadRaw } from '../data/store';
import {notifyFailure, notifySuccess} from "$api/notify";
import { writable, get } from "svelte/store";
import FabledAttribute from '$api/fabled-attribute.svelte';
import YAML from 'yaml';
import FabledClass, { classStore } from '../data/class-store.svelte';
import { skillStore } from '../data/skill-store.svelte';
import { FabledFolder } from '../data/folder-store.svelte';
import { classChinese, getTargetGame } from '../version/data';
import { attributeStore } from '../data/attribute-store';
import { trackVersion, getTrackedVersion, clearTrackedVersion, getAllTrackedVersions } from '$api/version-tracker';
import { clearPendingUpdate, pendingUpdates, type UpdateEvent } from '$api/sse';

let CONFIGURED_AXIOS: AxiosInstance = axios;
export const loading: string[] = [];

// Store for showing conflict confirmation dialog
export interface ConflictInfo {
    type: 'skill' | 'class' | 'attribute';
    resourceId: string;
    serverUploadedBy: string;
    serverUploadedAt: string;
    onConfirm: () => void;
    onCancel: () => void;
}
export const conflictDialog = writable<ConflictInfo | null>(null);

export const refreshAxios = async () => {
    const user = await userManager.getUser()
    if (user == null) {
        console.error("User is null")
        return
    }
    CONFIGURED_AXIOS = axios.create({
        // baseURL: 'http://localhost:81/v1/',
        baseURL: 'https://cdn.gkpixel.com/v2/',
        headers: {
            'Authorization': 'Bearer ' + user.access_token,
        }
    });
    console.log('Axios refreshed')
}

export const importClass = async (classId: string) => {
    CONFIGURED_AXIOS.get(`class/${getTargetGame()}/${classId}`).then(response => {
        if (response.data.success) {
            const classData = response.data.class;
            // Track version for conflict detection
            trackVersion('class', classId, classData.uploadedAt, classData.uploadedBy);
            // Clear any pending update badge
            clearPendingUpdate('class', classId);
            
            CONFIGURED_AXIOS.get('download/' + classData.fileId).then(response => {
                loadRaw(response.data, false)
            })
            notifySuccess(`成功匯入: ${classId}`);
        } else {
            notifyFailure(`匯入失敗: ${classId}`)
        }
    }).catch(error => {
        if (error.response.status === 404) {
            notifyFailure(`匯入失敗，找不到${classChinese()}: ${classId}`);
        } else {
            notifyFailure(`匯入${classId}失敗，錯誤 ` + error.response.status);
        }
    })
}

export const importSkill = async (skillId: string) => {
    try {
        const response = await CONFIGURED_AXIOS.get(`skill/${getTargetGame()}/${skillId}`);
        if (response.data.success) {
            const skillData = response.data.skill;
            // Track version for conflict detection
            trackVersion('skill', skillId, skillData.uploadedAt, skillData.uploadedBy);
            // Clear any pending update badge
            clearPendingUpdate('skill', skillId);
            
            try {
                const file = await CONFIGURED_AXIOS.get('download/' + skillData.fileId);
                await loadRaw(file.data, false);
                notifySuccess(`成功匯入: ${skillId}`);
            } catch (error) {
                notifyFailure(`匯入${skillId}失敗: ` + (error as AxiosError).status);
            }
        } else {
            notifyFailure(`匯入失敗: ${skillId}`);
        }
    } catch (error) {
        const status = (error as AxiosError).response?.status;
        if (status === 404) {
            notifyFailure(`匯入失敗，找不到技能: ${skillId}`);
        } else {
            notifyFailure(`匯入${skillId}失敗，錯誤 ` + status);
        }
    }
}

export const reloadAllClasses = async () => {
    for (const c of get(classStore.classes)) {
        loading.push(c.name);
        importClass(c.name).then(() => {
            loading.splice(loading.indexOf(c.name), 1);
        });
    }
}

export const reloadAllSkills = async () => {
    for (const c of get(skillStore.skills)) {
        loading.push(c.name);
        importSkill(c.name).then(() => {
            loading.splice(loading.indexOf(c.name), 1);
        });
    }
}

function getFolderName(skillId: string): string {
    if (skillId.toLowerCase().includes('test')) return 'Test';
    if (getTargetGame() === 'gkpm')
        return skillId.split('_')[0].replace(/[0-9]+/, '').toLowerCase().replace(/^[a-z]/, (c) => c.toUpperCase());
    else if (getTargetGame() === 'gkpl')
        return skillId.split('_')[0].toLowerCase();
    else return 'undefined game';
}

export const importAllClasses = async () => {
    const tasks = [];
    for (const clazz of await getAllClasses()) {
        tasks.push(async function () {
            await importClass(clazz);
        }());
    }
    await Promise.all(tasks);
    notifySuccess('成功匯入所有' + classChinese());
};

export const importAllSkills = async () => {
    const tasks = [];
    for (const skill of await getAllSkills()) {
        tasks.push(async function () {
            await importSkill(skill);
            const folderName = getFolderName(skill);
            let folder = get(skillStore.skillFolders).filter(f => f.name === folderName)[0];
            const realSkill = skillStore.getSkill(skill)!;
            if (folder) {
                folder.add(realSkill);
            } else {
                folder = new FabledFolder();
                folder.name = folderName;
                skillStore.addSkillFolder(folder);
                folder?.add(realSkill);
            }
        }());
    }
    await Promise.all(tasks);
    notifySuccess('成功匯入所有技能');
};

export const getAllClasses = async () => {
    try {
        const response = await CONFIGURED_AXIOS.get(`class/${getTargetGame()}`);
        if (response.data.success) {
            return response.data.classes.map((s: { classId: unknown; }) => {
                return s.classId
            }).sort();
        } else {
            notifyFailure(`讀取${classChinese()}列表失敗`)
        }
    } catch (error) {
        if (error instanceof AxiosError){
            notifyFailure(`讀取${classChinese()}列表失敗，錯誤 ` + error.response?.status)
        }
    }
    return []
}

export const getAllSkills = async () => {
    try {
        const response = await CONFIGURED_AXIOS.get(`skill/${getTargetGame()}`);
        if (response.data.success) {
            return response.data.skills.map((s: { skillId: unknown; }) => {
                return s.skillId
            }).sort();
        } else {
            notifyFailure('讀取技能列表失敗')
        }
    } catch (error) {
        if (error instanceof AxiosError){
            notifyFailure('讀取技能列表失敗，錯誤 ' + error.response?.status)
        }
    }
    return []
}

/**
 * Check all tracked resources against server versions and mark outdated ones
 * Call this on editor load to show which files have newer versions on server
 */
export const checkAllVersions = async () => {
    const trackedVersions = getAllTrackedVersions();
    if (trackedVersions.size === 0) return;
    
    const updates = get(pendingUpdates);
    let hasChanges = false;
    
    try {
        // Check skills
        const skillsResponse = await CONFIGURED_AXIOS.get(`skill/${getTargetGame()}`);
        if (skillsResponse.data.success) {
            for (const skill of skillsResponse.data.skills) {
                const localVersion = getTrackedVersion('skill', skill.skillId);
                if (localVersion && skill.uploadedAt) {
                    const localDate = new Date(localVersion.uploadedAt).getTime();
                    const serverDate = new Date(skill.uploadedAt).getTime();
                    
                    if (serverDate > localDate) {
                        const key = `skill:${skill.skillId}`;
                        updates.set(key, {
                            type: 'skill',
                            game: getTargetGame(),
                            resourceId: skill.skillId,
                            uploadedBy: skill.uploadedBy,
                            uploadedAt: skill.uploadedAt,
                            action: 'update'
                        });
                        hasChanges = true;
                    }
                }
            }
        }
        
        // Check classes
        const classesResponse = await CONFIGURED_AXIOS.get(`class/${getTargetGame()}`);
        if (classesResponse.data.success) {
            for (const clazz of classesResponse.data.classes) {
                const localVersion = getTrackedVersion('class', clazz.classId);
                if (localVersion && clazz.uploadedAt) {
                    const localDate = new Date(localVersion.uploadedAt).getTime();
                    const serverDate = new Date(clazz.uploadedAt).getTime();
                    
                    if (serverDate > localDate) {
                        const key = `class:${clazz.classId}`;
                        updates.set(key, {
                            type: 'class',
                            game: getTargetGame(),
                            resourceId: clazz.classId,
                            uploadedBy: clazz.uploadedBy,
                            uploadedAt: clazz.uploadedAt,
                            action: 'update'
                        });
                        hasChanges = true;
                    }
                }
            }
        }
        
        // Check attributes
        try {
            const attrResponse = await CONFIGURED_AXIOS.get(`attribute/${getTargetGame()}`);
            if (attrResponse.data.success) {
                const attrData = attrResponse.data.attributeCollection;
                const localVersion = getTrackedVersion('attribute', 'all');
                if (localVersion && attrData.uploadedAt) {
                    const localDate = new Date(localVersion.uploadedAt).getTime();
                    const serverDate = new Date(attrData.uploadedAt).getTime();
                    
                    if (serverDate > localDate) {
                        const key = 'attribute:all';
                        updates.set(key, {
                            type: 'attribute',
                            game: getTargetGame(),
                            resourceId: 'all',
                            uploadedBy: attrData.uploadedBy,
                            uploadedAt: attrData.uploadedAt,
                            action: 'update'
                        });
                        hasChanges = true;
                    }
                }
            }
        } catch (e) {
            // 404 is fine - no attributes uploaded yet
        }
        
        if (hasChanges) {
            pendingUpdates.set(new Map(updates));
        }
        
    } catch (error) {
        console.error('Failed to check versions:', error);
    }
}

export const upload = async (forceOverwrite: boolean = false) => {
    const formData = new FormData();
    const act = get(active);
    if (!act) return;
    if (act instanceof FabledAttribute) {
        // For attributes, upload the entire combined attributes file
        await uploadAttributes(forceOverwrite);
        return;
    }
    
    const type = act instanceof FabledClass ? 'class' : 'skill';
    const resourceId = act.name;
    
    // Check for version conflict if not force overwriting
    if (!forceOverwrite) {
        try {
            const response = await CONFIGURED_AXIOS.get(`${type}/${getTargetGame()}/${resourceId}`);
            if (response.data.success) {
                const serverData = type === 'class' ? response.data.class : response.data.skill;
                const localVersion = getTrackedVersion(type, resourceId);
                
                if (localVersion && serverData.uploadedAt) {
                    const localDate = new Date(localVersion.uploadedAt).getTime();
                    const serverDate = new Date(serverData.uploadedAt).getTime();
                    
                    if (serverDate > localDate) {
                        // Conflict detected - show confirmation dialog
                        return new Promise<void>((resolve) => {
                            conflictDialog.set({
                                type: type as 'skill' | 'class',
                                resourceId,
                                serverUploadedBy: serverData.uploadedBy,
                                serverUploadedAt: serverData.uploadedAt,
                                onConfirm: async () => {
                                    conflictDialog.set(null);
                                    await upload(true); // Force overwrite
                                    resolve();
                                },
                                onCancel: () => {
                                    conflictDialog.set(null);
                                    resolve();
                                }
                            });
                        });
                    }
                }
            }
        } catch (error) {
            // 404 means new file, proceed with upload
            if ((error as AxiosError).response?.status !== 404) {
                console.error('Failed to check version:', error);
            }
        }
    }
    
    if (act instanceof FabledClass) {
        formData.append('details', new Blob([JSON.stringify({classId: act.name})], {
            type: 'application/json'
        }));
    } else {
        formData.append('details', new Blob([JSON.stringify({ skillId: act.name })], {
            type: 'application/json'
        }));
    }
    formData.append('file', new File([new Blob([YAML.stringify({ [act.name]: act.serializeYaml() })], {
        type: 'application/x-yaml'
    })], act.name + ".yml"));
    CONFIGURED_AXIOS.post((act instanceof FabledClass ? 'class' : 'skill') + `/${getTargetGame()}`, formData).then(function (response) {
        if (response.data.success) {
            // Update tracked version after successful upload
            const uploadedData = type === 'class' ? response.data.class : response.data.skill;
            trackVersion(type, resourceId, uploadedData.uploadedAt, uploadedData.uploadedBy);
            notifySuccess('上傳成功')
        } else {
            notifyFailure('上傳失敗')
        }
    }).catch(function (error) {
        notifyFailure('上傳失敗，錯誤 ' + error.response.status)
    });
}

/**
 * Upload all attributes as a combined file to CDN
 */
export const uploadAttributes = async (forceOverwrite: boolean = false) => {
    // Check for version conflict if not force overwriting
    if (!forceOverwrite) {
        try {
            const response = await CONFIGURED_AXIOS.get(`attribute/${getTargetGame()}`);
            if (response.data.success) {
                const serverData = response.data.attributeCollection;
                const localVersion = getTrackedVersion('attribute', 'all');
                
                if (localVersion && serverData.uploadedAt) {
                    const localDate = new Date(localVersion.uploadedAt).getTime();
                    const serverDate = new Date(serverData.uploadedAt).getTime();
                    
                    if (serverDate > localDate) {
                        // Conflict detected - show confirmation dialog
                        return new Promise<void>((resolve) => {
                            conflictDialog.set({
                                type: 'attribute',
                                resourceId: 'all',
                                serverUploadedBy: serverData.uploadedBy,
                                serverUploadedAt: serverData.uploadedAt,
                                onConfirm: async () => {
                                    conflictDialog.set(null);
                                    await uploadAttributes(true); // Force overwrite
                                    resolve();
                                },
                                onCancel: () => {
                                    conflictDialog.set(null);
                                    resolve();
                                }
                            });
                        });
                    }
                }
            }
        } catch (error) {
            // 404 means new file, proceed with upload
            if ((error as AxiosError).response?.status !== 404) {
                console.error('Failed to check version:', error);
            }
        }
    }
    
    const formData = new FormData();
    const yamlContent = await getAttributeYaml();
    
    formData.append('file', new File([new Blob([yamlContent], {
        type: 'application/x-yaml'
    })], 'attributes.yml'));
    
    try {
        const response = await CONFIGURED_AXIOS.post(`attribute/${getTargetGame()}`, formData);
        if (response.data.success) {
            // Update tracked version after successful upload
            const uploadedData = response.data.attributeCollection;
            trackVersion('attribute', 'all', uploadedData.uploadedAt, uploadedData.uploadedBy);
            notifySuccess('屬性上傳成功');
        } else {
            notifyFailure('屬性上傳失敗');
        }
    } catch (error) {
        notifyFailure('屬性上傳失敗，錯誤 ' + (error as AxiosError).response?.status);
    }
}

/**
 * Import all attributes from CDN (combined file)
 */
export const importAttributes = async () => {
    try {
        const response = await CONFIGURED_AXIOS.get(`attribute/${getTargetGame()}`);
        if (response.data.success) {
            const attrData = response.data.attributeCollection;
            // Track version for conflict detection
            trackVersion('attribute', 'all', attrData.uploadedAt, attrData.uploadedBy);
            // Clear any pending update badge
            clearPendingUpdate('attribute', 'all');
            
            const file = await CONFIGURED_AXIOS.get('download/' + attrData.fileId);
            attributeStore.loadAttributesText(file.data, 'local');
            notifySuccess('成功匯入所有屬性');
        } else {
            notifyFailure('匯入屬性失敗');
        }
    } catch (error) {
        const status = (error as AxiosError).response?.status;
        if (status === 404) {
            notifyFailure('匯入失敗，找不到屬性資料');
        } else {
            notifyFailure('匯入屬性失敗，錯誤 ' + status);
        }
    }
}

/**
 * Reload all attributes from CDN (same as import, refreshes local data)
 */
export const reloadAllAttributes = async () => {
    await importAttributes();
}