import Keycloak from 'keycloak-js';
import { writable } from 'svelte/store';

export const isAuthenticated = writable(false);
export const profile = writable(null);

export const keycloak = new Keycloak({
    url: 'http://localhost:8081',
    realm: 'feedapp',
    clientId: 'feedapp-frontend'
});

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

export function login(opts = {}) {
    return keycloak.login(opts);
}

export function logout() {
    return keycloak.logout({ redirectUri: window.location.origin });
}

export function getToken() {
    return keycloak.token || '';
}
