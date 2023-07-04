const settings = {
    authority: "https://idms.gkpixel.com/application/o/skill-editor/",
    client_id: "KpB2ih5fw3SRCh5ad7fCEE2iqkbrpjJ4oVEwvpBf",
    redirect_uri: window.location.origin + "/idms_callback",
    post_logout_redirect_uri: window.location.origin,
    response_type: "code",
    scope: "openid profile download:cdn read:cdn.class.* update:cdn.class.* read.all:cdn.class.* read:cdn.skill.* update:cdn.skill.* read.all:cdn.skill.*",
    response_mode: "fragment",
    filterProtocolClaims: true,
    extraHeaders: {
        "Origin": window.location.origin
    }
};

oidc.Log.setLogger(console);
const userManager = new oidc.UserManager(settings);

function login() {
    userManager.signinRedirect(settings).then().catch(err => {
        console.log(err);
    });
}

userManager.getUser().then(user => {
    console.log("User: ", user);
});

function processLoginResponse() {
    userManager.signinRedirectCallback().then(() => {
        window.location.href = window.location.origin;
    }).catch(function (err) {
        console.error(err);
    });
}

function logout() {
    userManager.signoutRedirect(settings).then().catch(err => {
        console.log(err);
    });
}

if (window.location.href.includes("code=")) {
    processLoginResponse();
}
