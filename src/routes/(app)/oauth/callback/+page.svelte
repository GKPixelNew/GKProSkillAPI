<svelte:head>
    <title>GKPixel Skill Editor</title>
</svelte:head>

<script lang="ts">
    import {processLoginResponse} from "$api/oauth.js";

    processLoginResponse()
</script>

<div>Please wait...</div>