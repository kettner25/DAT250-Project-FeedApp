import Keycloak from 'keycloak-js';
import { writable } from 'svelte/store';

// in-memory auth state for your UI
export const isAuthenticated = writable(false);
export const profile = writable(null);

export const keycloak = new Keycloak({
    url: 'http://localhost:8081',   // Keycloak base URL
    realm: 'feedapp',
    clientId: 'feedapp-frontend'
});

export async function initAuth() {
    const authenticated = await keycloak.init({
        onLoad: 'check-sso',  // or 'login-required' to force login immediately
        pkceMethod: 'S256',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
    });

    isAuthenticated.set(authenticated);

    if (authenticated && keycloak.tokenParsed) {
        profile.set({
            username: keycloak.tokenParsed.preferred_username,
            email: keycloak.tokenParsed.email
        });
    } else {
        profile.set(null);
    }

    // keep token fresh (refresh if <30s remaining)
    setInterval(async () => {
        if (!keycloak) return;
        try { await keycloak.updateToken(30); } catch (e) { /* ignore */ }
    }, 10000);

    return authenticated;
}

export function login(opts = {}) {
    return keycloak.login(opts);
}

export function logout() {
    return keycloak.logout({ redirectUri: window.location.origin });
}

export function getToken() {
    return keycloak.token || '';
}
