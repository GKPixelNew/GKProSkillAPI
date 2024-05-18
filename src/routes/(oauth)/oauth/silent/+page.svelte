<svelte:head>
    <title>GKPixel Skill Editor</title>
</svelte:head>

<script lang="ts">
    import {initOAuth, processLoginResponse} from "$api/oauth.js";
    import {onMount} from "svelte";

    onMount(() => {
        initOAuth();
        processLoginResponse();
    });
</script>