"use strict";



(function (window) {
  const API_BASE = '/api';

  function beginLogin() {
    window.location.href = '/oauth2/authorization/cognito';
  }

  function logout() {
    window.location.href = '/logout';
  }

  async function getCurrentUser() {
    try {
      const res = await fetch(API_BASE + '/auth/me', {
        credentials: 'include' 
      });
      if (res.ok) {
        const data = await res.json();
        if (data.authenticated) {
          return { username: data.username, email: data.email };
        }
      }
    } catch (e) {
      console.warn('No se pudo obtener usuario actual', e);
    }
    return null;
  }

  window.Auth = {
    beginLogin,
    logout,
    getCurrentUser
  };
})(window);
