<script>
    // @ts-nocheck
    import { isAuthenticated, profile } from '../lib/auth';
    import { navigate } from '../lib/store';

    export let view = "all-polls";
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

    <button
        type="button"
        title="View All Polls"
        class:selected={view === "all-polls"}
        on:click={() => navigate("all-polls")}
    >
        All Polls
    </button>

    {#if $isAuthenticated}
        <button
            type="button"
            title="View Only My Polls"
            class:selected={view === "my-polls"}
            on:click={() => navigate("my-polls")}
        >
            My Polls
        </button>

        <button
            type="button"
            title="Create A New Poll"
            class:selected={view === "create"}
            on:click={() => navigate("create")}
        >
            Create New Poll
        </button>
    {/if}
</nav>
