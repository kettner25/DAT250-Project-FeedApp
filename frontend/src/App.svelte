<script>
    import { onMount } from 'svelte';
    import {
        route,
        currentUser,
        myPolls,
        allPolls,
        pollToEdit,
        loadBootstrap,
        errorStore,
        role,
    } from './lib/store.js';

    import PollListView from "./components/PollListView.svelte";
    import Header from "./components/Header.svelte";
    import Sidebar from "./components/Sidebar.svelte";
    import AdminView from "./components/AdminView.svelte";
    import PollEditorView from "./components/PollEditorView.svelte";

    $: currentView = $route;

    onMount(async () => {
        location.hash = "all-polls"

        try {
            await loadBootstrap();
        } catch (e) {
            console.error(e);
        }
    });

    $: if ($errorStore) setTimeout(() => errorStore.set(null), 4000);
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
        padding: 1em;
        border-radius: 4px;
        position: fixed;
        top: 1em;
        right: 1em;
    }
</style>

{#if $errorStore}
    <div class="toast">
        {$errorStore}
    </div>
{/if}

<h1>The FeedApp</h1>

<Header user={$currentUser} />

<div class="layout">
        <Sidebar view={currentView} />
    <div class="content">
        {#if currentView === "create"}
            <PollEditorView pollId={null} title="Create a New Poll" />
        {:else if currentView === "edit" && $pollToEdit !== null}
            <PollEditorView pollId={$pollToEdit.id} title="Edit this Poll" />
        {:else if currentView === "my-polls"}
            <PollListView polls={$myPolls} title="My Polls" editable={true} />
        {:else if currentView === "all-polls"}
            <PollListView polls={$allPolls} title="All Polls" />
        {:else if currentView === "admin" && role === "ADMIN"}
            <AdminView />
        {:else}
            <span>Error</span>
        {/if}
    </div>
</div>
