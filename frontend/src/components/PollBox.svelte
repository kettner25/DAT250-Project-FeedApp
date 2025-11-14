<script>
    // @ts-nocheck

    import {
        me,
        pollToEdit,
        user_castVote,
        user_deletePoll,
        user_remVote
    } from '../lib/store.js';
    import { getOrCreateAnonId, isAuthenticated } from '../lib/auth.js';
    import { onMount} from "svelte";

    import OptionRow from "./OptionRow.svelte";

    export let poll;
    export let editable = false;

    let selectedOption = null;

    async function vote(option) {
        if (selectedOption === null) {
            // vote

            let vote = {
                publishedAt: new Date().toISOString(),
                option: option,
                user: isAuthenticated ? $me : null,
                userId: null,
                anonId: null,
            }

            vote = await user_castVote(poll.id, vote);
            if (vote) {
                const updatedOption = { ...option, votes: [...option.votes, vote] };
                poll = { ...poll, options: poll.options.map(o => o.id === option.id ? updatedOption : o) };
                selectedOption = updatedOption;
            }
        }
        else if (selectedOption === option) {
            // unvote

            const vote = $isAuthenticated
                ? option.votes.find(v => v.userId === $me.id)
                : option.votes.find(v => v.anonId === getOrCreateAnonId())
            ;

            if (vote) {
                const res = await user_remVote(poll.id, vote.id);
                if (res) {
                    const updatedOption = { ...option, votes: option.votes.filter(v => v.id !== vote.id) };
                    poll = { ...poll, options: poll.options.map(o => o.id === option.id ? updatedOption : o) };
                    selectedOption = null;
                }
            }
        }
    }

    async function loadSelection() {
        const option = $isAuthenticated
            ? poll.options.find(opt => opt.votes.some(v => v.userId === $me.id))
            : poll.options.find(opt => opt.votes.some(v => v.anonId === getOrCreateAnonId()))
        ;
        selectedOption = option ?? null;
        return selectedOption;
    }

    function edit() {
        pollToEdit.set(poll);
        location.hash = "edit"
    }

    onMount(async () => {
        await loadSelection();
    });
</script>

<style>
    :global(body) {
        margin: 0;
    }

    div {
        text-align: left;
    }

    .row {
        margin-bottom: 0.5rem;
    }

    .selected-row {
        background-color: #aa0000;
    }

    .box {
        max-width: 20rem;
        background: #aaaaaa;
        padding: 1rem;
        border-radius: 1rem;
    }

    .header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
    }

    button {
        margin-left: 1rem;
    }
</style>

<div class="box">
    <div class="header">
        <h3>{poll.question}</h3>
        {#if (editable)}
            <div class="header">
                <button type="button" title="Delete" on:click={() => user_deletePoll(poll.id)}>Delete</button>
                <button type="button" title="Edit" on:click={edit}>Edit</button>
            </div>
        {/if}
    </div>
    <div>
        {#each poll.options as opt (opt.caption)}
            <div class="row" class:selected-row={selectedOption && selectedOption.id === opt.id}>
                <OptionRow option={opt} viewOnly={true} onVote={vote}/>
            </div>
        {/each}
    </div>
</div>
