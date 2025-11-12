import { getToken } from './auth';

// --- Config ---
const API_BASE = "http://localhost:8080";

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

    return res.status === 204 ? undefined : res.json();
}
