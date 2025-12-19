import axios, {AxiosError, type AxiosInstance} from "axios";
import {userManager} from "$api/oauth";
import { active, loadRaw } from '../data/store';
import {notifyFailure, notifySuccess} from "$api/notify";
import {get} from "svelte/store";
import FabledAttribute from '$api/fabled-attribute.svelte';
import YAML from 'yaml';
import FabledClass, { classStore } from '../data/class-store.svelte';
import { skillStore } from '../data/skill-store.svelte';
import { FabledFolder } from '../data/folder-store.svelte';
import { classChinese, getTargetGame } from '../version/data';

let CONFIGURED_AXIOS: AxiosInstance = axios;
export const loading: string[] = [];

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
            CONFIGURED_AXIOS.get('download/' + response.data.class.fileId).then(response => {
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
            try {
                const file = await CONFIGURED_AXIOS.get('download/' + response.data.skill.fileId);
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

export const upload = async () => {
    const formData = new FormData();
    const act = get(active);
    if (!act) return;
    if (act instanceof FabledAttribute) {
        notifyFailure("無法上傳屬性")
        return;
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
            notifySuccess('上傳成功')
        } else {
            notifyFailure('上傳失敗')
        }
    }).catch(function (error) {
        notifyFailure('上傳失敗，錯誤 ' + error.response.status)
    });
}