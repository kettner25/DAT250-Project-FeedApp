<script>
    // @ts-nocheck

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

    $: votes = option?.votes?.length ?? 0;

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

    button.option-row {
        all: unset;
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        cursor: pointer;
    }

    .caption {
        min-width: 10rem;
    }

    .voteCnt {
        min-width: 5rem;
    }
</style>

<div class="option-row">
    {#if viewOnly}
        <button class="option-row" type="button" title="Vote" on:click={vote}>
            <span class="caption">{option.caption}</span>
            <span class="voteCnt">{votes} {votes === 1 ? 'vote' : 'votes'}</span>
        </button>
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
