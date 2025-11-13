<script>
    import { tick } from "svelte";

    export let option;
    export let onMoveUp = null;
    export let onMoveDown = null;
    export let onRemove = null;
    export let onVote = null;

    export let viewOnly = true;
    export let canMoveUp = true;
    export let canMoveDown = true;

    let inputElemRef = null;

    $: votes = 0; // todo vote count

    async function moveUp() {
        onMoveUp?.(option);
        await tick();  // needs for focus
        inputElemRef?.focus();
    }

    async function moveDown() {
        onMoveDown?.(option);
        await tick();  // needs for focus
        inputElemRef?.focus();
    }

    function remove() {
        onRemove?.(option);
    }

    function vote() {
        onVote?.(option);
    }
</script>

<style>
    :global(body) {
        margin: 0;
    }

    .option-row {
        display: flex;
        gap: 1rem;
    }

    .buttons {
        display: flex;
        gap: 0.5rem;
    }

    .caption {
        width: 100%;
        min-width: 10rem;
    }

    .voteCnt {
        width: 2rem;
    }
</style>

<div class="option-row">
    {#if viewOnly}
        <span class="caption">{option.caption}</span>
        <div class="buttons">
            <span class="voteCnt">{votes}</span>
            <button type="button" title="Vote" on:click={vote}>Vote</button>
        </div>
    {:else}
        <input
                type="text"
                placeholder="Option"
                bind:value={option.caption}
                on:keydown={(e) => {
                    if (e.shiftKey && e.key === 'ArrowUp') {
                        e.preventDefault();
                        moveUp()
                    }
                    if (e.shiftKey && e.key === 'ArrowDown') {
                        e.preventDefault();
                        moveDown()
                    }
                    if (e.key === 'Delete') {
                        e.preventDefault();
                        remove()
                    }
                }}
                bind:this={inputElemRef}
        />
        <div class="buttons">
            <button type="button" tabindex="-1" title="Move up" on:click={moveUp} disabled={!canMoveUp}>↑</button>
            <button type="button" tabindex="-1" title="Move down" on:click={moveDown} disabled={!canMoveDown}>↓</button>
            <button type="button" tabindex="-1" title="Delete" on:click={remove}>✕</button>
        </div>
    {/if}
</div>
