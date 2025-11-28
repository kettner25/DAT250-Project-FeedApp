// --- Imports ---
import { getToken } from './auth.js';

// --- Config ---
const API_BASE = "http://localhost:8080/api";

// --- Fetch helper ---
export async function apiFetch(path, options = {}) {
    const token = getToken();

    const res = await fetch(`${API_BASE}${path}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": token ? `Bearer ${token}` : "None",
            ...(options.headers || {})
        },
        credentials: "include",
        ...options
    });

    if (!res.ok) {
        const text = await res.text().catch(() => "");
        console.error(`HTTP ${res.status}: ${text}`);
        return null;
    }

    if (res.status === 200 || res.status === 201) {
        const text = await res.text();

        try {
            return JSON.parse(text) ?? undefined;
        } catch (_) {
            return text ?? undefined;
        }
    }

    return undefined;
}
