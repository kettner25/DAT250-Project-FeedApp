import { writable, derived } from "svelte/store";
import { apiFetch } from "./api.js"

// --- Routing ---
export const route = writable(location.hash.replace("#", "") || "home");
function setRoute() { route.set(location.hash.replace("#", "") || "home") }
window.addEventListener("hashchange", setRoute);

// --- User from our DB ---
export const me = writable(null);
export const currentUser = writable(null);
export const role = writable(null);

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
    const [all, my] = await Promise.all([
        apiFetch("/polls/"),
        currentUser?.id ? apiFetch(`/users/${currentUser.id}/polls/`) : []
    ]);

    if (all) allPolls.set(all.map(sortPollOptions));
    if (my) myPolls.set(my.map(sortPollOptions));
}

export async function refresh() {
    await loadBootstrap();
}

export async function createPoll(uid, data) {
    const created = await apiFetch(`/users/${uid}/polls/`, {
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

export async function updatePoll(uid, pid, patch) {
    const updated = await apiFetch(`/users/${uid}/polls/${pid}`, {
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

export async function getPoll(uid, pid) {
    const poll = await apiFetch(`/users/${uid}/polls/${pid}`, {
        method: "GET",
    });
    return sortPollOptions(poll)
}

export async function deletePoll(uid, pid) {
    const deleted = await apiFetch(`/users/${uid}/polls/${pid}`, {
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

export async function castVote(user, vote) {
    // todo anonymous votes
    const castedVote = await apiFetch(`/polls/${user.pid}/votes/`, {
        method: "POST",
        body: JSON.stringify(vote)
    });
    return castedVote;
}

export async function remVote(pid, vid) {
    const remVote = await apiFetch(`/polls/${pid}/votes/${vid}`, {
        method: "DELETE"
    });
    return remVote;
}

