import { writable, get } from "svelte/store";
import { isAuthenticated, profile } from './auth.js';
import { apiFetch } from "./api.js"

// --- Routing ---
// todo fix routing
export const route = writable(location.hash.replace("#", "") || "home");
function setRoute() { route.set(location.hash.replace("#", "") || "home") }
window.addEventListener("hashchange", setRoute);

// --- User from our DB ---
export const me = writable(null);

// --- Error ---
export const errorStore = writable(null);

// --- Polls ---
export const allPolls = writable([]);
export const myPolls = writable([]);
export const newPollTemplate = writable({
    question: "",
    publishedAt: "",
    validUntil: "",
    options: [],
    user: null,
});
export const pollToEdit = writable(null);

function sortPollOptions(poll) {
    if (!poll || !Array.isArray(poll.options)) return poll;
    return { ...poll, options: [...poll.options].sort((a, b) => a.order - b.order) };
}

// ------- API calls -------

export async function loadBootstrap() {
    console.log(get(isAuthenticated))
    const [meres, all, my] = await Promise.all([
        get(isAuthenticated) ? apiFetch("/me") : null,
        apiFetch("/polls"),
        get(isAuthenticated) ? apiFetch("/me/polls") : []
    ]);

    if (meres) me.set(meres)
    if (all) allPolls.set(all.map(sortPollOptions));
    if (my) myPolls.set(my.map(sortPollOptions));
}

export async function refresh() {
    await loadBootstrap();
}

export async function user_createPoll(data) {
    const created = await apiFetch(`/polls`, {
        method: "POST",
        body: JSON.stringify(data)
    });

    if (created) {
        const sorted = sortPollOptions(created);
        allPolls.update(list => [sorted, ...list]);
        myPolls.update(list => [sorted, ...list]);
        return sorted;
    } else {
        errorStore.set("Poll Creation Failed");
    }
}

export async function user_updatePoll(pid, patch) {
    const updated = await apiFetch(`/polls/${pid}`, {
        method: "PUT",
        body: JSON.stringify(patch)
    });

    console.log(patch);
    console.log(updated);

    if (updated) {
        const sorted = sortPollOptions(updated);
        allPolls.update(list => list.map(p => (p.id === pid ? sorted : p)));
        myPolls.update(list => list.map(p => (p.id === pid ? sorted : p)));
        return sorted;
    } else {
        errorStore.set("Poll Update Failed");
    }
}

export async function user_getPoll(pid) {
    const poll = await apiFetch(`/polls/${pid}`, {
        method: "GET",
    });

    return sortPollOptions(poll)
}

export async function user_deletePoll(pid) {
    const deleted = await apiFetch(`/polls/${pid}`, {
        method: "DELETE"
    });

    if (deleted) {
        allPolls.update(list => list.filter(p => p.id !== pid));
        myPolls.update(list => list.filter(p => p.id !== pid));
        return deleted
    } else {
        errorStore.set("Poll Deletion Failed");
        return false;
    }
}

export async function user_castVote(pid, vote) {
    const castedVote = await apiFetch(`/polls/${pid}/votes`, {
        method: "POST",
        body: JSON.stringify(vote)
    });

    return castedVote;
}

export async function user_remVote(pid, vid) {
    const remVote = await apiFetch(`/polls/${pid}/votes/${vid}`, {
        method: "DELETE"
    });

    return remVote;
}

export async function anonym_castVote(pid, vote) {
    const castedVote = await apiFetch(`/polls/${pid}/votes`, {
        method: "POST",
        body: JSON.stringify(vote)
    });

    return castedVote;
}

export async function anonym_remVote(pid, vid) {
    const remVote = await apiFetch(`/polls/${pid}/votes/${vid}`, {
        method: "DELETE"
    });

    return remVote;
}
