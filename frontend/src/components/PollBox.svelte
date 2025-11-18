<script>
    // @ts-nocheck

    import {
        me,
        pollToEdit,
        castVote,
        deletePoll,
        remVote,
        navigate,
        loadBootstrap,
        getPollStats
    } from '../lib/store.js';
    import { getOrCreateAnonId, isAuthenticated } from '../lib/auth.js';

    import OptionRow from "./OptionRow.svelte";

    export let poll;
    export let editable = false;

    let selectedOption = null;

    // todo
    $: if (poll && poll.options) {
        const anonId = getOrCreateAnonId();

        if ($isAuthenticated) {
            selectedOption =
                poll.options.find(opt =>
                    (opt.votes ?? []).some(v => v.userId === $me.id)
                ) ?? null;
        } else {
            selectedOption =
                poll.options.find(opt =>
                    (opt.votes ?? []).some(v => v.anonId === anonId)
                ) ?? null;
        }
    } else {
        selectedOption = null;
    }

    async function vote(option) {
        const anonId = $isAuthenticated ? null : getOrCreateAnonId();
        const currentSelection = selectedOption;

        if (!currentSelection) {
            // 1) No vote yet → create a vote

            let vote = {
                publishedAt: new Date().toISOString(),
                option: option,
                user: $isAuthenticated ? $me : null,
                userId: $isAuthenticated ? $me.id : null,
                anonId: $isAuthenticated ? null : anonId,
            };

            vote = await castVote(poll.id, vote);
            if (vote) {
                const updatedOption = { ...option, votes: [...(option.votes ?? []), vote] };
                poll = { ...poll, options: poll.options.map(o => o.id === option.id ? updatedOption : o) };
            }
        }
        else if (currentSelection.id === option.id) {
            // 2) Click same option → remove vote (unvote)

            const voteToRemove = $isAuthenticated
                ? option.votes?.find(v => v.userId === $me.id)
                : option.votes?.find(v => v.anonId === anonId);

            if (!voteToRemove) return;

            const res = await remVote(poll.id, voteToRemove.id);
            if (res) {
                const updatedOption = { ...option, votes: (option.votes ?? []).filter(v => v.id !== voteToRemove.id) };
                poll = { ...poll, options: poll.options.map(o => o.id === option.id ? updatedOption : o) };
            }
        }
        else {
            // 3) Click different option → move vote from old to new

            const oldOption = currentSelection;
            const oldVote = $isAuthenticated
                ? oldOption.votes?.find(v => v.userId === $me.id)
                : oldOption.votes?.find(v => v.anonId === anonId);
            if (!oldVote) return;

            const removed = await remVote(poll.id, oldVote.id);
            if (!removed) return;

            const updatedOldOption = { ...oldOption, votes: (oldOption.votes ?? []).filter(v => v.id !== oldVote.id) };

            let newVote = {
                publishedAt: new Date().toISOString(),
                option: option,
                user: $isAuthenticated ? $me : null,
                userId: $isAuthenticated ? $me.id : null,
                anonId: $isAuthenticated ? null : anonId,
            };

            newVote = await castVote(poll.id, newVote);
            if (!newVote) return;

            const updatedNewOption = { ...option, votes: [...(option.votes ?? []), newVote] };

            poll = { ...poll, options: poll.options.map(o => {
                        if (o.id === updatedOldOption.id) return updatedOldOption;
                        if (o.id === updatedNewOption.id) return updatedNewOption;
                        return o;
            })};
        }

        // todo fix this approach
        await loadBootstrap();
    }

    function edit() {
        pollToEdit.set(poll);
        navigate("edit");
    }

    async function print() {
        // redis
        const result = await getPollStats(poll.id);
        console.log(result);
        alert(JSON.stringify(result, null, 2));
    }
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
        border-radius: 0.5rem;
        padding: 0.5rem;
    }

    .row:hover {
        background-color: rgba(0, 0, 0, 0.1);
    }

    .selected-row {
        background: linear-gradient(135deg, #1e78ff, #1f3b73);
        color: white;
        box-shadow: 0 2px 6px rgba(0,0,0,0.3);
    }

    .box {
        max-width: 22rem;
        background: #e8e8e8;
        padding: 1.2rem;
        border-radius: 1.2rem;
        box-shadow: 0 4px 10px rgba(0,0,0,0.5);
    }

    .header, .footer {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
    }

    button {
        padding: 0.4rem 0.6rem;
        margin-left: 1rem;
        border: none;
        border-radius: 0.5rem;
    }

    button:hover {
        background-color: rgba(0, 0, 0, 0.1);
    }
</style>

<div class="box">
    <div class="header">
        <h3>{poll.question}</h3>
        {#if (editable)}
            <div class="header">
                <button type="button" title="Delete" on:click={() => deletePoll(poll.id)}>Delete</button>
                <button type="button" title="Edit" on:click={edit}>Edit</button>
            </div>
        {/if}
    </div>
    <div class="poll">
        {#each poll.options as opt (opt.caption)}
            <div class="row" class:selected-row={selectedOption && selectedOption.id === opt.id}>
                <OptionRow option={opt} viewOnly={true} onVote={vote}/>
            </div>
        {/each}
    </div>
    {#if ($isAuthenticated)}
    <div class="footer">
        <span></span>
        <button type="button" title="Print" on:click={print}>Print</button>
    </div>
    {/if}
</div>
