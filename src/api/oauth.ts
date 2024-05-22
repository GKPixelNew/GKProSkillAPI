import {Log, UserManager, type UserManagerSettings} from "oidc-client-ts";
import {refreshAxios} from "$api/cdn";

export let userManager: UserManager;
let settings: UserManagerSettings;
export let loggedIn = false;

export const initOAuth = () => {
    Log.setLogger(console);
    settings = {
        authority: "https://idms.gkpixel.com/application/o/skill-editor/",
        client_id: "KpB2ih5fw3SRCh5ad7fCEE2iqkbrpjJ4oVEwvpBf",
        redirect_uri: window.location.origin + "/oauth/callback",
        post_logout_redirect_uri: window.location.origin,
        response_type: "code",
        scope: "openid profile offline_access download:cdn read:cdn.class.* update:cdn.class.* read.all:cdn.class.* read:cdn.skill.* update:cdn.skill.* read.all:cdn.skill.*",
        response_mode: "fragment",
        filterProtocolClaims: true,
        extraHeaders: {
            "Origin": window.location.origin
        },
        silent_redirect_uri: window.location.origin + "/oauth/silent",
        automaticSilentRenew: true,
    } as UserManagerSettings;
    userManager = new UserManager(settings);
    userManager.events.addUserLoaded(() => {
        loggedIn = true;
        refreshAxios()
    })
}

export const login = () => {
    userManager.signinRedirect(settings).then().catch(err => {
        console.log(err);
    });
}

export const processLoginResponse = (silent: boolean = false) => {
    userManager.signinCallback().then(() => {
        if (silent) return;
        window.location.href = window.location.origin;
    }).catch(function (err) {
        console.error(err);
    });
}

export const logout = () => {
    userManager.signoutRedirect(settings).then(() => {
        loggedIn = false;
        refreshAxios()
    }).catch(err => {
        console.log(err);
    });
}
