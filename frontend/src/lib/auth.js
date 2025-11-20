// --- Imports ---
import { writable } from 'svelte/store';
import Keycloak from 'keycloak-js';

// --- Config ---
export const isAuthenticated = writable(false);
export const profile = writable(null);

export const keycloak = new Keycloak({
    url: 'http://localhost:8081',
    realm: 'feedapp',
    clientId: 'feedapp-frontend'
});

// --- Auth ---

export async function initAuth() {
    const authenticated = await keycloak.init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
    });

    isAuthenticated.set(authenticated);

    if (authenticated && keycloak.idTokenParsed) {
        profile.set({
            username: keycloak.idTokenParsed.preferred_username,
            email: keycloak.idTokenParsed.email,
            keycloak_id: keycloak.idTokenParsed.sub,
            roles: keycloak.realmAccess.roles,
        });
    } else {
        profile.set(null);
    }

    setInterval(async () => {
        if (!keycloak) return;
        try { await keycloak.updateToken(30); } catch (e) { /* ignore */ }
    }, 10000);

    return authenticated;
}

// --- Logins ---

export function login(opts = {}) {
    createNewAnonId();
    return keycloak.login(opts);
}

export function logout() {
    createNewAnonId();
    return keycloak.logout({ redirectUri: window.location.origin });
}

export function getToken() {
    return keycloak.token || '';
}

// --- Cookies ---
export function createNewAnonId() {
    const anonId = crypto.randomUUID();
    document.cookie = `anonId=${anonId}; path=/; max-age=${60 * 60 * 24 * 30}`;
    return anonId;
}

export function getOrCreateAnonId() {
    const existing = document.cookie
        .split("; ")
        .find(row => row.startsWith("anonId="));

    if (existing) return existing.split("=")[1];
    else return createNewAnonId();
}
