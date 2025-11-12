<script>
    import { isAuthenticated, profile, login, logout } from '../lib/auth';

    // todo
    export let cookie = "123456abc"
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
    {#if $isAuthenticated}
        <span>{$profile?.username}</span>
        <span>{$profile?.email}</span>
<!--        <button on:click={loadMe}>Who am I?</button>-->
<!--        <p>{me}</p>-->
        <button on:click={logout}>Logout</button>
    {:else}
        <span>Anonymous user</span>
        <span>{cookie}</span>
        <button on:click={() => login()}>Login</button>
    {/if}
</header>
