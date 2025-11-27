// src/lib/auth.js
// --- Imports ---
import { writable } from 'svelte/store';

// --- Stores ---
export const isAuthenticated = writable(false);
export const profile = writable(null); // { id, username, email }

const TOKEN_KEY = "feedapp_token";
const PROFILE_KEY = "feedapp_profile";

// interner Token-Cache für getToken()
let currentToken = localStorage.getItem(TOKEN_KEY) || "";

const tokenStore = writable(currentToken);
tokenStore.subscribe((t) => {
    currentToken = t || "";
});

// --- Init (beim App-Start aufrufen) ---
export function initAuth() {
    const savedToken = localStorage.getItem(TOKEN_KEY);
    const savedProfile = localStorage.getItem(PROFILE_KEY);

    if (savedToken && savedProfile) {
        try {
            const parsed = JSON.parse(savedProfile);
            tokenStore.set(savedToken);
            profile.set(parsed);
            isAuthenticated.set(true);
        } catch (e) {
            console.error("Failed to parse saved profile", e);
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(PROFILE_KEY);
            tokenStore.set("");
            profile.set(null);
            isAuthenticated.set(false);
        }
    } else {
        tokenStore.set("");
        profile.set(null);
        isAuthenticated.set(false);
    }
}

// --- Token Zugriff für api.js ---
export function getToken() {
    return currentToken;
}

// --- Login ---
// Kann wie bisher einfach `login()` aufrufen (dann kommen Prompts),
// oder programmatisch mit { username, password } aufgerufen werden.
export async function login(opts = {}) {
    let { username, password } = opts;

    if (!username) {
        username = window.prompt("Username:");
    }
    if (!password) {
        password = window.prompt("Password:");
    }
    if (!username || !password) {
        return false;
    }

    try {
        const res = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) {
            const text = await res.text().catch(() => "");
            console.error("Login failed:", res.status, text);
            alert("Login failed: Invalid credentials");
            return false;
        }

        const data = await res.json();
        // Erwartet: { token, username, email, id }

        localStorage.setItem(TOKEN_KEY, data.token);
        localStorage.setItem(PROFILE_KEY, JSON.stringify({
            id: data.id,
            username: data.username,
            email: data.email
        }));

        tokenStore.set(data.token);
        profile.set({
            id: data.id,
            username: data.username,
            email: data.email
        });
        isAuthenticated.set(true);

        // neue anonyme ID für Cookies
        createNewAnonId();

        return true;
    } catch (e) {
        console.error("Login error:", e);
        alert("Login error – see console");
        return false;
    }
}

// --- Registration (optional nutzbar) ---
export async function register({ username, email, password }) {
    const res = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password })
    });

    if (!res.ok) {
        const txt = await res.text().catch(() => "");
        console.error("Register failed:", res.status, txt);
        return false;
    }
    return true;
}

// --- Logout ---
export function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(PROFILE_KEY);

    tokenStore.set("");
    profile.set(null);
    isAuthenticated.set(false);

    createNewAnonId();
}

// --- Anonyme Cookies (wie bisher) ---
export function createNewAnonId() {
    const anonId = crypto.randomUUID();
    document.cookie = `anonId=${anonId}; path=/; max-age=${60 * 60 * 24 * 30}`;
    return anonId;
}

export function getOrCreateAnonId() {
    const existing = document.cookie
        .split("; ")
        .find(row => row.startsWith("anonId="));

    if (existing)
        return existing.split("=")[1];

    const anonId = crypto.randomUUID();
    document.cookie = `anonId=${anonId}; path=/; max-age=${60 * 60 * 24 * 30}`;

    return anonId;
}
