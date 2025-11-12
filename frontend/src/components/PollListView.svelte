<script>
    import PollBox from './PollBox.svelte';

    export let polls = [];
    export let title = "Polls";
    export let editable = false;

    let search = "";
    $: filtered = search.trim().toLowerCase()
        ? polls.filter(p => (p.question || '').toLowerCase().includes(search.trim().toLowerCase()))
        : polls;
</script>

<style>
    :global(body) {
        margin: 0;
    }

    input[type="search"] {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #dddddd;
        border-radius: 0.5rem;
        margin-bottom: 2rem;
        font-size: 0.8rem;
    }

    .container {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(18rem, 1fr));
        gap: 1rem;
    }
</style>

<h2>{title}</h2>
<input type="search" placeholder="Search polls..." bind:value={search} />
<div class="container">
    {#if polls.length > 0}
    {#each filtered as poll (poll.id)}
        <div>
            <PollBox poll={poll} editable={editable} />
        </div>
    {/each}
    {:else}
        <span>No Polls Yet...</span>
    {/if}
</div>
