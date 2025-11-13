<script>
    import {
        user_deletePoll,
        user_castVote,
        user_getPollVotes,
        user_getVotes,
        user_remVote,
        me,
        pollToEdit,
    } from '../lib/store.js';
    import OptionRow from "./OptionRow.svelte";

    // todo finish anonym voting

    export let poll;
    export let editable = false;

    let selectedOption = null;

    async function voteAnonym(option) {

    }

    async function vote(option) {
        if (selectedOption === null) {
            let vote = {
                publishedAt: new Date().toISOString(),
                option: option,
                user: $me,
            }

            vote = await user_castVote(poll.id, vote);
            if (vote)
                selectedOption = option;
        }
        else if (selectedOption === option) {
            const userVotes = await user_getVotes();                                    // todo improve
            const optionVotes = await user_getPollVotes(poll.id, option.id);    // todo improve
            const vote = optionVotes.find(optionVote => userVotes.some(userVote => userVote.id === optionVote.id));
            console.log(userVotes);
            console.log(optionVotes);
            console.log(vote);

            if (vote) {
                let res = await user_remVote(poll.id, vote.id);
                if (res)
                    selectedOption = null;
            }
        }
    }

    function edit() {
        pollToEdit.set(poll);
        location.hash = "edit"
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
