<script>
    // export
    // todo


    export let user = null;
    export let cookie = "123456abc"
    
    function logOut() {
        // todo
    }
</script>

<style>
    header {
        display: flex;
        gap: 1rem;
        padding: 1rem;
        text-align: left;
        border-bottom: 1px solid #dddddd;
    }
</style>

<header>
    <span>Feed App</span>
    {#if (user !== null)}
        <span>{user.username}</span>
        <span>{user.email}</span>
        <button type="button">Log out</button>
    {:else}
        <span>Anonymous user</span>
        <span>{cookie}</span>
        <button type="button" on:click={logOut}>Log in / Register</button>
    {/if}
</header>
