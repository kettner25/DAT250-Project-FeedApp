<script>
    // @ts-nocheck
    
    import {
        isAuthenticated,
        profile,
        login,
        logout
    } from '../lib/auth.js';
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
        <button type="button" title="Logout" on:click={logout}>Logout</button>
    {:else}
        <span>Anonymous</span>
        <span>User</span>
        <button type="button" title="Login" on:click={() => login()}>Login</button>
    {/if}
</header>
