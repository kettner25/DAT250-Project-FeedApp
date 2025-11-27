// src/lib/store.js
import { get, writable, derived } from "svelte/store";
import { isAuthenticated, profile } from './auth.js';
import { apiFetch } from "./api.js";

// --- Routing ---
const DEFAULT_ROUTE = "all-polls";

function getRouteFromHash() {
    const hash = window.location.hash.slice(1);
    return hash || DEFAULT_ROUTE;
}

export const route = writable(getRouteFromHash());

function setRoute() {
    route.set(getRouteFromHash());
}

window.addEventListener("hashchange", setRoute);

export function navigate(to) {
    if (!to.startsWith("#")) {
        window.location.hash = `#${to}`;
    } else {
        window.location.hash = to;
    }
}

// --- User from our DB ---
export const me = writable(null);

// --- Error ---
export const errorStore = writable(null);

// --- Polls ---
export const allPolls = writable([]);

export const myPolls = derived(
    [allPolls, me, isAuthenticated],
    ([$allPolls, $me, $isAuthenticated]) => {
        if (!$isAuthenticated || !$me || !$me.polls || !Array.isArray($me.polls)) return [];
        return $allPolls.filter(pa => $me.polls.some(pb => pa.id === pb.id));
    }
);

export const newPollTemplate = writable({
    id: null,
    question: "",
    publishedAt: "",
    validUntil: "",
    options: [],
    user: null,
});

export const pollToEdit = writable(null);

// --- Helpers ---
function sortPollOptions(poll) {
    if (!poll || !Array.isArray(poll.options)) return poll;
    return { ...poll, options: [...poll.options].sort((a, b) => a.order - b.order) };
}

export function updatePollInStore(updatedPoll) {
    allPolls.update(list =>
        list.map(p => p.id === updatedPoll.id ? updatedPoll : p)
    );
}

// --- API calls ---

export async function loadBootstrap() {
    const [_me, _all] = await Promise.all([
        get(isAuthenticated) ? apiFetch("/me") : null,
        apiFetch("/polls"),
    ]);

    if (_me) me.set(_me);
    if (_all) allPolls.set(_all.map(sortPollOptions));
}

export async function createPoll(data) {
    const created = await apiFetch(`/polls`, {
        method: "POST",
        body: JSON.stringify(data)
    });

    if (created) {
        const sorted = sortPollOptions(created);
        allPolls.update(list => [sorted, ...list]);

        if (get(isAuthenticated)) {
            me.update(cur => {
                if (!cur) return cur;
                return { ...cur, polls: [...(cur.polls || []), sorted] };
            });
        }

        return sorted;
    } else {
        errorStore.set("Poll Creation Failed");
    }
}

export async function updatePoll(pid, patch) {
    const updated = await apiFetch(`/polls/${pid}`, {
        method: "PUT",
        body: JSON.stringify(patch)
    });

    if (updated) {
        const sorted = sortPollOptions(updated);
        allPolls.update(list => list.map(p => (p.id === pid ? sorted : p)));
        return sorted;
    } else {
        errorStore.set("Poll Update Failed");
    }
}

export async function getPoll(pid) {
    const poll = await apiFetch(`/polls/${pid}`, {
        method: "GET",
    });

    return sortPollOptions(poll);
}

export async function deletePoll(pid) {
    const deleted = await apiFetch(`/polls/${pid}`, {
        method: "DELETE"
    });

    if (deleted) {
        allPolls.update(list => list.filter(p => p.id !== pid));
        return deleted;
    } else {
        errorStore.set("Poll Deletion Failed");
        return false;
    }
}

export async function castVote(pid, vote) {
    return await apiFetch(`/polls/${pid}/votes`, {
        method: "POST",
        body: JSON.stringify(vote)
    });
}

export async function remVote(pid, vid) {
    return await apiFetch(`/polls/${pid}/votes/${vid}`, {
        method: "DELETE"
    });
}

export async function getPollStats(pid) {
    return await apiFetch(`/polls/${pid}/count`, {
        method: "GET"
    });
}
