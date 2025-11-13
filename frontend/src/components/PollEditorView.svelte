<script>
    import { onMount, tick, onDestroy } from "svelte";
    import {
        newPollTemplate,
        me,
        user_createPoll,
        user_updatePoll,
        user_getPoll,
    } from '../lib/store.js';

    import OptionRow from "./OptionRow.svelte";

    export let pollId = null;
    export let title = "Edit Poll"

    let poll = $newPollTemplate;
    let questionElemRef = null;
    let newOptionCaptionElemRef = null;
    let newOption = "";

    function handleKey(e) {
        if (e.key === 'Enter' && e.shiftKey) {
            e.preventDefault();
            savePoll();
        }
    }

    async function load() {
        try {
            if (pollId === null) {
                const template = $newPollTemplate;
                poll = structuredClone ? structuredClone(template) : JSON.parse(JSON.stringify(template));
            } else {
                const existing = await user_getPoll(pollId);
                if (!existing) poll = null;
                else poll = structuredClone ? structuredClone(existing) : JSON.parse(JSON.stringify(existing));
            }
        } catch (e) {
            console.error(e);
        }
    }

    onMount(() => {
        load();
        questionElemRef?.focus();  // autofocus on question input
        window.addEventListener("keydown", handleKey);  // add save keyboard shortcut
    });
    onDestroy(() => {
        window.removeEventListener("keydown", handleKey);  // remove added save keyboard shortcut
    });

    function moveUp(option) {
        const index = option.order;
        if (index > 0) {
            // Get the option above
            const above = poll.options[index - 1];

            // Swap their order values
            above.order++;
            option.order--;

            // Swap positions in the array
            [poll.options[index - 1], poll.options[index]] = [poll.options[index], poll.options[index - 1]];
        }
    }

    function moveDown(option) {
        const index = option.order;
        if (index < poll.options.length - 1) {
            // Get the option below
            const below = poll.options[index + 1];

            // Swap their order values
            below.order--;
            option.order++;

            // Swap positions in the array
            [poll.options[index], poll.options[index + 1]] = [poll.options[index + 1], poll.options[index]];
        }
    }

    async function remove(option) {
        poll = {
            ...poll,
            options: poll.options
                .filter(o => o.id !== option.id)    // Remove matching option
                .map((o, i) => ({...o, order: i}))  // Re-index orders cleanly
        };
        await tick();  // Needed for focus after button press
        newOptionCaptionElemRef?.focus();

        console.log(poll.options);
    }

    async function addOption(text) {
        if (!text.trim()) return;

        if (!unique) return;

        const option = {
            caption: text,
            order: poll.options.length,
        };

        // Push to end using Svelte reactivity
        poll = {...poll, options: [...poll.options, option]};

        newOption = "";
        await tick();  // Needed for focus after button press
        newOptionCaptionElemRef?.focus();

        console.log(poll.options);
    }

    $: isPollValid = poll.question.trim() !== "" && poll.options.length >= 2;
    $: unique = !poll.options.some(o => o.caption.trim().toLowerCase() === newOption.trim().toLowerCase());

    async function savePoll() {
        if (!isPollValid)
            return;

        try {
            if (pollId === null) {
                poll = {...poll, publishedAt: new Date().toISOString()};
                poll = {...poll, validUntil: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString()};  // 1 month
                poll = {...poll, user: $me}
                await user_createPoll(poll);
            } else {
                poll = {...poll, validUntil: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString()};  // 1 month
                poll = {...poll, user: $me}
                await user_updatePoll(pollId, poll);
            }
        } catch (e) {
            console.error(e);
        }

        // Route to my polls
        poll = $newPollTemplate;
        location.hash = "my-polls";
    }
</script>

<style>
    :global(body) {
        margin: 0;
    }

    form {
        text-align: left;
    }

    .add-row {
        display: flex;
        gap: 1rem;
    }

    .row {
        margin-bottom: 0.5rem;
    }

    button {
        padding: 0.5rem 1.5rem;
    }

    button[type="submit"] {
        margin-top: 0.5rem;
    }

    label {
        display: block;
        font-weight: bold;
        margin-top: 1rem;
        margin-bottom: 0.5rem;
    }
</style>

<h2>{title}</h2>
<form on:submit|preventDefault={savePoll}>
    <label for="q">Question</label>
    <input
            id="q"
            type="text"
            placeholder="Type poll question"
            bind:value={poll.question}
            bind:this={questionElemRef}
    />

    <label for="opt">Options</label>
    <div id="opt">
        {#each poll.options as opt (opt.caption)}
            <div class="row">
                <OptionRow
                        option={opt} viewOnly={false}
                        onMoveUp={moveUp} onMoveDown={moveDown} onRemove={remove}
                        canMoveUp={opt.order > 0} canMoveDown={opt.order < poll.options.length - 1}
                />
            </div>
        {/each}

        {#if poll.options.length === 0}
            <div class="row"><span>No options yet</span></div>
        {/if}

        <div class="add-row">
            <input
                    type="text"
                    placeholder="Type option text"
                    bind:value={newOption}
                    bind:this={newOptionCaptionElemRef}
                    on:keydown={(e) => {
                        if (e.key === 'Enter' && newOption.trim() !== "") {
                            e.preventDefault();
                            addOption(newOption);
                        }
                    }}
            />
            <button type="button" on:click={() => addOption(newOption)} disabled={newOption.trim() === "" || !unique}>
                Add new
            </button>
        </div>
    </div>

    <button type="submit" disabled={!isPollValid}>Save Poll</button>
</form>
