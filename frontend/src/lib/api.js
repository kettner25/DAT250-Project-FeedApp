// src/lib/api.js
import { getToken } from './auth.js';

const API_BASE = "http://localhost:8080/api";

export async function apiFetch(path, options = {}) {
    const token = getToken();

    const baseHeaders = {
        "Content-Type": "application/json",
    };

    if (token) {
        baseHeaders["Authorization"] = `Bearer ${token}`;
    }

    const res = await fetch(`${API_BASE}${path}`, {
        headers: {
            ...baseHeaders,
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

    if (res.status === 204 || res.status === 205) {
        return undefined;
    }

    const text = await res.text();

    try {
        return JSON.parse(text) ?? undefined;
    } catch (_) {
        return text ?? undefined;
    }
}
