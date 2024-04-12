<script lang='ts'>
	import {goto} from '$app/navigation';
	import {base} from '$app/paths';
	import {login, logout, userManager} from "$api/oauth";
	import {User} from "oidc-client-ts";

	let user: User | null = null;
    $: user = null;
    userManager.getUser().then(u => {
        user = u
        console.log(u)
    });
</script>

<div id='header'>
    <div class='home'
         tabindex='0'
         role='button'
         on:click={() => goto(`${base}/`)}
         on:keypress={(e) => e.key === "Enter" && goto(`${base}/`)}
    >
        <h1 class='accent'>GKPixel</h1>
        <h2>Dynamic Skill Editor</h2>
        <div class='copy'>&copy; ProMCTeam and GKPixelTeam {new Date().getFullYear()}</div>
    </div>

    <div class='socials'>
        {#if user == null}
            <div class='chip'
                 tabindex='0'
                 role='button'
                 style="background-color: #0079d9"
                 on:click|stopPropagation={login}
                 on:keypress={(e) => {
					 if (e.key === 'Enter') {
						 e.stopPropagation();
						 login();
					 }
				 }}
                 title='登入 GKIDMS'>
                登入
            </div>
        {/if}
        {#if user != null}
			<img style="margin: auto;margin-right: 10px;height: 35px" src={user.profile.avatar} alt="Avatar"/>
			<div style="margin: auto;margin-right: 20px">{user.profile.given_name}</div>
			<div class='chip'
				 tabindex='0'
				 role='button'
				 style="background-color: #d92800"
				 on:click|stopPropagation={logout}
				 on:keypress={(e) => {
					 if (e.key === 'Enter') {
						 e.stopPropagation();
						 logout();
					 }
				 }}
				 title='登出 GKIDMS'>
				登出
			</div>
        {/if}
    </div>
</div>

<style>
    #header {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0.5rem;
    }

    div > * {
        text-align: center;
    }

    .home {
        display: flex;
        align-items: center;
    }

    h1, h2 {
        display: inline;
        font-family: Mojave-Web, sans-serif;
        margin: 0;
        padding: 0 0 0.5rem;
    }

    h1 {
        font-size: 2rem;
    }

    h2 {
        margin-left: 0.5rem;
        font-size: 1.25rem;
    }

    .socials {
        display: flex;
        justify-content: flex-end;
    }

    .copy {
        align-self: flex-end;
        font-size: 0.7rem;
        padding-left: 0.5rem;
    }

    @media screen and (min-width: 500px) {
        #header {
            flex-direction: row;
            justify-content: space-between;
        }

        h1, h2 {
            padding: unset;
        }
    }
</style>