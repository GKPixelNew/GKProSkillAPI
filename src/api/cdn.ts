import axios, {AxiosError, type AxiosInstance} from "axios";
import {userManager} from "$api/oauth";
import {loadRaw} from "../data/store";
import {notifyFailure, notifySuccess} from "$api/notify";
import {classes} from "../data/class-store";
import {get} from "svelte/store";
import { skills } from '../data/skill-store';

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
        baseURL: 'https://cdn.gkpixel.com/v1/',
        headers: {
            'Authorization': 'Bearer ' + user.access_token,
        }
    });
    console.log('Axios refreshed')
}

export const importClass = async (classId: string) => {
    CONFIGURED_AXIOS.get('class/' + classId).then(response => {
        if (response.data.success) {
            CONFIGURED_AXIOS.get('download/' + response.data.class.fileId).then(response => {
                loadRaw(response.data, false)
            })
            notifySuccess('匯入成功');
        } else {
            notifyFailure('匯入失敗');
        }
    }).catch(error => {
        if (error.response.status === 404) {
            notifyFailure('匯入失敗，找不到信仰');
        } else {
            notifyFailure('匯入失敗，錯誤 ' + error.response.status);
        }
    })
}

export const importSkill = async (skillId: string) => {
    CONFIGURED_AXIOS.get('skill/' + skillId).then(response => {
        if (response.data.success) {
            CONFIGURED_AXIOS.get('download/' + response.data.skill.fileId).then(response => {
                loadRaw(response.data, false)
            })
            notifySuccess('匯入成功');
        } else {
            notifyFailure('匯入失敗');
        }
    }).catch(error => {
        if (error.response.status === 404) {
            notifyFailure('匯入失敗，找不到技能');
        } else {
            notifyFailure('匯入失敗，錯誤 ' + error.response.status);
        }
    })
}

export const reloadAllClasses = async () => {
    for (const c of get(classes)) {
        loading.push(c.name);
        importClass(c.name).then(() => {
            loading.splice(loading.indexOf(c.name), 1);
        });
    }
}

export const reloadAllSkills = async () => {
    for (const c of get(skills)) {
        loading.push(c.name);
        importSkill(c.name).then(() => {
            loading.splice(loading.indexOf(c.name), 1);
        });
    }
}

export const getAllClasses = async () => {
    try {
        const response = await CONFIGURED_AXIOS.get("class");
        if (response.data.success) {
            return response.data.classes.map((s: { classId: unknown; }) => {
                return s.classId
            }).sort();
        } else {
            notifyFailure('讀取信仰列表失敗')
        }
    } catch (error) {
        if (error instanceof AxiosError){
            notifyFailure('讀取信仰列表失敗，錯誤 ' + error.response?.status)
        }
    }
    return []
}

export const getAllSkills = async () => {
    try {
        const response = await CONFIGURED_AXIOS.get("skill");
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