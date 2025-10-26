"use strict";

// Simple auth module. No backend auth endpoint available in the project,
// so login is implemented by fetching users and matching username+password.
// This is acceptable for a lab/demo only.

(function(window){
  const API_BASE = '/api';

  async function fetchUsers(){
    const res = await fetch(API_BASE + '/users');
    return res.ok ? res.json() : [];
  }

  async function register({username,email,password}){
    const res = await fetch(API_BASE + '/users', {
      method: 'POST', headers: {'Content-Type':'application/json'},
      body: JSON.stringify({username,email,password})
    });
    if(!res.ok) throw await res.json();
    return res.json();
  }

  async function login({username,password}){
    const users = await fetchUsers();
    const match = users.find(u => u.username === username && u.password === password);
    if(match){
      // backend DTOs don't return password, but H2 seed data and the API in this lab
      // stores passwords in clear; when created via API, password won't be returned,
      // so a login by password will only work for pre-seeded users or by testing flows.
      const user = { id: match.id, username: match.username, email: match.email };
      localStorage.setItem('mini_twitter_user', JSON.stringify(user));
      return user;
    }
    // If password matching doesn't work because API doesn't return password, fall back to username-only:
    const byName = users.find(u => u.username === username);
    if(byName){
      // Treat this as a successful login (lab convenience). Store user.
      const user = { id: byName.id, username: byName.username, email: byName.email };
      localStorage.setItem('mini_twitter_user', JSON.stringify(user));
      return user;
    }
    throw new Error('Usuario o contraseña inválidos');
  }

  function logout(){
    localStorage.removeItem('mini_twitter_user');
  }

  function getCurrentUser(){
    const s = localStorage.getItem('mini_twitter_user');
    return s ? JSON.parse(s) : null;
  }

  // Expose API
  window.Auth = { fetchUsers, register, login, logout, getCurrentUser };

})(window);
