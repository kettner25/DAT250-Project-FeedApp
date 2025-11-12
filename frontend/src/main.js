import { mount } from 'svelte';
import { initAuth } from './lib/auth';
import './app.css';
import App from './App.svelte';

(async () => {
    await initAuth()
    mount(App, {
        target: document.getElementById('app'),
        props: {},
    })
})()
