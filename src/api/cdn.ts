import axios, {type AxiosInstance} from "axios";
import {userManager} from "$api/oauth";

export let CONFIGURED_AXIOS:AxiosInstance = axios;

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