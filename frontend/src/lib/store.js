import { writable, derived } from "svelte/store";

// --- Routing ---
export const route = writable(location.hash.replace("#", "") || "home");
function setRoute() { route.set(location.hash.replace("#", "") || "home") }
window.addEventListener("hashchange", setRoute);

// --- Config ---
const API_BASE = "http://localhost:8080";

// --- Auth / user ---
export const currentUser = writable(null);
export const role = "USER"

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

// ------- API calls -------

function sortPollOptions(poll) {
    if (!poll || !Array.isArray(poll.options)) return poll;
    return { ...poll, options: [...poll.options].sort((a, b) => a.order - b.order) };
}

async function request(path, options = {}) {
    const res = await fetch(`${API_BASE}${path}`, {
        headers: { "Content-Type": "application/json", ...(options.headers || {}) },
        credentials: "include",
        ...options
    });

    if (!res.ok) {
        const text = await res.text().catch(() => "");
        console.error(`HTTP ${res.status}: ${text}`);
        return null;
    }

    return res.status === 204 ? undefined : res.json();
}

export async function loadBootstrap() {
    const [all, my] = await Promise.all([
        request("/polls/"),
        currentUser?.id ? request(`/users/${currentUser.id}/polls/`) : []
    ]);

    allPolls.set(all.map(sortPollOptions));
    myPolls.set(my.map(sortPollOptions));
}

export async function refresh() {
    await loadBootstrap();
}

export async function createPoll(uid, data) {
    const created = await request(`/users/${uid}/polls/`, {
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
    const updated = await request(`/users/${uid}/polls/${pid}`, {
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
    const poll = await request(`/users/${uid}/polls/${pid}`, {
        method: "GET",
    });
    return sortPollOptions(poll)
}

export async function deletePoll(uid, pid) {
    const deleted = await request(`/users/${uid}/polls/${pid}`, {
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

export async function castVote(pid, vote) {
    const castedVote = await request(`/polls/${pid}/votes/`, {
        method: "POST",
        body: JSON.stringify(vote)
    });
    return castedVote;
}

export async function remVote(pid, vid) {
    const remVote = await request(`/polls/${pid}/votes/${vid}`, {
        method: "DELETE"
    });
    return remVote;
}

