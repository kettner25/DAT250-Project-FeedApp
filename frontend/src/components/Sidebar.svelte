<script>
    import { isAuthenticated, profile } from '../lib/auth';

    export let view = "all-polls";

    // todo solve router
    function nav(to) {
        location.hash = to;
    }
</script>

<style>
    nav {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 1rem;
        text-align: left;
        border-right: 1px solid #dddddd;
        min-width: 170px;
    }
    button {
        padding: 1rem;
    }
</style>

<nav>
    <span>Feed App</span>
    <button class:selected={view === "all-polls"} on:click={() => nav("all-polls")}>All Polls</button>
    {#if $isAuthenticated}
        <button class:selected={view === "my-polls"} on:click={() => nav("my-polls")}>My Polls</button>
        <button class:selected={view === "create"} on:click={() => nav("create")}>Create New Poll</button>
    {/if}
    {#if $isAuthenticated && $profile?.roles?.includes("ADMIN")}
        <button class:selected={view === "admin"} on:click={() => nav("admin")}>Admin</button>
    {/if}
</nav>
