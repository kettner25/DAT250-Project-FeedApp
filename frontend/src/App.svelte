<script>
    // @ts-nocheck

    import {
        route,
        myPolls,
        allPolls,
        pollToEdit,
        errorStore,
        loadBootstrap,
        refresh
    } from './lib/store.js';
    import { onMount , onDestroy } from 'svelte';
    import { isAuthenticated, profile, getOrCreateAnonId } from './lib/auth';

    import PollListView from "./components/PollListView.svelte";
    import Header from "./components/Header.svelte";
    import Sidebar from "./components/Sidebar.svelte";
    import AdminView from "./components/AdminView.svelte";
    import PollEditorView from "./components/PollEditorView.svelte";

    $: currentView = $route;

    let refreshInterval;
    let unsubscribe;

    onMount(async () => {
        getOrCreateAnonId();
        refreshInterval = setInterval(() => { refresh(); }, 60000);
        unsubscribe = errorStore.subscribe((v) => { if (v) setTimeout(() => errorStore.set(null), 4000); });
        await loadBootstrap();
    });

    onDestroy(async () => {
        if (refreshInterval) clearInterval(refreshInterval);
        if (unsubscribe) unsubscribe();
    })
</script>

<style>
    .layout {
        display: grid;
        grid-template-columns: 200px 1fr;
    }

    .content {
        padding-left: 2rem;
        padding-top: 0.5rem;
    }

    .toast {
        background: #f44336;
        color: white;
        padding: 1rem;
        border-radius: 4px;
        position: fixed;
        top: 1rem;
        right: 1rem;
    }
</style>

{#if $errorStore}
    <div class="toast">
        {$errorStore}
    </div>
{/if}

<h1>The FeedApp</h1>

<Header />

<div class="layout">
        <Sidebar view={currentView} />
    <div class="content">
        {#if currentView === "create" && $isAuthenticated}
            <PollEditorView pollId={null} title="Create a New Poll" />
        {:else if currentView === "edit" && $pollToEdit !== null && $isAuthenticated}
            <PollEditorView pollId={$pollToEdit.id} title="Edit this Poll" />
        {:else if currentView === "my-polls" && $isAuthenticated}
            <PollListView polls={$myPolls} title="My Polls" editable={true} />
        {:else if currentView === "all-polls"}
            <PollListView polls={$allPolls} title="All Polls" />
        {:else if currentView === "admin" && $isAuthenticated && $profile?.roles?.includes("ADMIN")}
            <AdminView />
        {:else}
            <PollListView polls={$allPolls} title="All Polls" />
        {/if}
    </div>
</div>
