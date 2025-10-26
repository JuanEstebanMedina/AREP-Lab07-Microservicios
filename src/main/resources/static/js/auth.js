"use strict";

// Simple auth module. No backend auth endpoint available in the project,
// so login is implemented by fetching users and matching username+password.
// This is acceptable for a lab/demo only.

(function (window) {
  const API_BASE = '/api';

  async function fetchUsers() {
    const res = await fetch(API_BASE + '/users');
    return res.ok ? res.json() : [];
  }

  async function register({ username, email, password }) {
    const res = await fetch(API_BASE + '/users', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    });
    if (!res.ok) throw await res.json();
    return res.json();
  }

  async function login({ username, password }) {
    const basic = 'Basic ' + btoa(`${username}:${password}`);

    // Probamos acceder a un endpoint protegido
    const res = await fetch(API_BASE + '/users', { // o cualquier endpoint protegido
      headers: { Authorization: basic }
    });

    if (res.ok) {
      const user = await res.json().catch(() => ({ username }));
      sessionStorage.setItem('AUTH', basic);
      localStorage.setItem('mini_twitter_user', JSON.stringify(user));
      return user;
    } else {
      throw new Error('Credenciales inválidas');
    }
  }

  function logout() {
    sessionStorage.removeItem('AUTH');
    localStorage.removeItem('mini_twitter_user');
  }

  function getCurrentUser() {
    const s = localStorage.getItem('mini_twitter_user');
    return s ? JSON.parse(s) : null;
  }

  // Expose API
  window.Auth = { fetchUsers, register, login, logout, getCurrentUser };

})(window);
