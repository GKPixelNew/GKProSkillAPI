<!--This to disable the default layouts for OAuth redirect pages to prevent waste of bandwidth-->
<div id="body-container">
    <div id="body">
        <div style="justify-content: center;display: table-cell; vertical-align: middle;height: 100%;margin-top: 50vh;color: #cccccc;font-family: sans-serif">Please wait...</div>
        <slot/>
    </div>
</div>

<style>
    @media screen and (min-width: 500px) {
        #body-container {
            max-height: 100%;
            overflow: visible;
        }
    }

    #body-container {
        max-width: 100dvw;
        height: 10%;
        flex-grow: 1;
        display: flex;
        flex-direction: row;
    }

    #body {
        display: flex;
        flex-direction: column;
        align-items: center;
        /*padding-bottom: 1rem;*/
        width: 10%;
        flex: 1;
        overflow: auto;
        min-height: 100vh;
        background-color: #0a0a0a;
    }

    :global(body) {
        min-height: 100vh;
        margin: 0;
    }
</style>