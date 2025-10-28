"use strict";

const API_BASE = '/api';

const qs = (id) => document.getElementById(id);

document.addEventListener('DOMContentLoaded', () => {

  const appView = qs('appView');
  const currentUserDisplay = qs('currentUserDisplay');
  const logoutBtn = qs('logoutBtn');

  if (logoutBtn) logoutBtn.addEventListener('click', () => window.Auth.logout());

  // Main app controls
  const createStreamBtn = qs('createStreamBtn');
  const postBtn = qs('postBtn');
  const refreshBtn = qs('refreshBtn');
  const charsLeft = qs('charsLeft');

  if (charsLeft) charsLeft.innerText = 140;

  let cachedUser = null;

  if (postBtn) postBtn.addEventListener('click', async () => {
    const user = cachedUser || await window.Auth.getCurrentUser();
    if (!user) { window.Auth.beginLogin(); return; }
    const streamId = qs('streamSelect').value;
    const contenido = qs('postContent').value.trim();
    if (!streamId) { alert('Seleccione un stream'); return; }
    if (!contenido) { alert('El contenido no puede estar vacío'); return; }
    if (contenido.length > 140) { alert('El contenido excede 140 caracteres'); return; }
    try {
      const res = await fetch(API_BASE + '/posts', { 
        method: 'POST', 
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' }, 
        body: JSON.stringify({ streamId: parseInt(streamId), contenido }) 
      });
      if (!res.ok) { const err = await res.json().catch(() => null); alert('Error publicando: ' + (err?.message || res.status)); return; }
      qs('postContent').value = '';
      qs('charsLeft').innerText = 140;
      await loadPosts(currentStreamId);
    } catch (e) { alert('Error de red al publicar'); }
  });

  if (createStreamBtn) createStreamBtn.addEventListener('click', async () => {
    const nombre = qs('streamNombre').value.trim();
    const descripcion = qs('streamDescripcion').value.trim();
    if (!nombre) { alert('El nombre del stream es requerido'); return; }
    try {
      const res = await fetch(API_BASE + '/streams', { 
        method: 'POST', 
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' }, 
        body: JSON.stringify({ nombre, descripcion }) 
      });
      if (!res.ok) { const err = await res.json().catch(() => null); alert('Error creando stream: ' + (err?.message || res.status)); return; }
      qs('streamNombre').value = ''; qs('streamDescripcion').value = '';
      await loadStreams();
    } catch (e) { alert('Error de red al crear stream'); }
  });

  if (refreshBtn) refreshBtn.addEventListener('click', () => loadPosts(currentStreamId));

  if (qs('postContent')) qs('postContent').addEventListener('input', (e) => {
    const left = 140 - e.target.value.length;
    if (qs('charsLeft')) qs('charsLeft').innerText = left;
  });

  // Data & UI helpers
  async function fetchStreams() { const res = await fetch(API_BASE + '/streams', {credentials: 'include'}); return res.ok ? res.json() : []; }
  async function fetchPosts() { const res = await fetch(API_BASE + '/posts', {credentials: 'include'}); return res.ok ? res.json() : []; }
  async function fetchPostsByStream(streamId) { const res = await fetch(API_BASE + '/streams/' + streamId + '/posts', {credentials: 'include'}); return res.ok ? res.json() : []; }

  function escapeHtml(s) { if (!s) return ''; return s.replace(/[&<>\"'`]/g, (m) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '\"': '&quot;', "'": "&#39;", "`": "&#96;" })[m]); }

  function renderStreamsSelect(streams) {
    const sel = qs('streamSelect'); if (!sel) return; sel.innerHTML = '';
    streams.forEach(s => { const opt = document.createElement('option'); opt.value = s.id; opt.textContent = s.nombre + (s.descripcion ? ' — ' + s.descripcion : ''); sel.appendChild(opt); });
  }

  function renderCategoryButtons(streams) {
    const container = qs('categories'); if (!container) return;
    container.querySelectorAll('button[data-stream]').forEach(b => { if (b.dataset.stream !== 'all') b.remove(); });
    streams.forEach(s => {
      const btn = document.createElement('button'); btn.className = 'btn-secondary small'; btn.dataset.stream = s.id; btn.textContent = s.nombre;
      btn.addEventListener('click', () => { onCategorySelect(s.id); });
      container.appendChild(btn);
    });
    const globalBtn = container.querySelector('button[data-stream="all"]');
    if (globalBtn && !globalBtn.dataset.listenerAttached) {
      globalBtn.addEventListener('click', () => { onCategorySelect('all'); });
      globalBtn.dataset.listenerAttached = '1';
    }
  }

  function renderPosts(posts) {
    const c = qs('postsContainer'); if (!c) return; c.innerHTML = '';
    if (!posts || posts.length === 0) { c.innerHTML = '<div class="small">No hay posts todavía.</div>'; return; }
    posts.forEach(p => {
      const d = document.createElement('div'); d.className = 'post';
      const when = p.createdAt ? new Date(p.createdAt).toLocaleString() : '';
      d.innerHTML = `<div><strong>${escapeHtml(p.username || 'anon')}</strong> <span class="meta">en <em>${escapeHtml(p.streamNombre || 'general')}</em></span></div>
                     <div style="margin:6px 0">${escapeHtml(p.contenido)}</div>
                     <div class="meta">${when}</div>`;
      c.appendChild(d);
    });
  }

  let currentStreamId = 'all';

  async function loadStreams() { const s = await fetchStreams(); renderStreamsSelect(s); renderCategoryButtons(s); }

  async function loadPosts(streamId) {
    if (!streamId || streamId === 'all') {
      const p = await fetchPosts(); renderPosts(p);
      currentStreamId = 'all';
    } else {
      const p = await fetchPostsByStream(streamId); renderPosts(p); currentStreamId = streamId;
    }
  }

  function onCategorySelect(streamId) { loadPosts(streamId); }

  function showApp() {
    if (appView) appView.style.display = 'block';
  }

  function onLogin(user) {
    cachedUser = user;
    if (currentUserDisplay) currentUserDisplay.innerText = (user?.username || '') + (user?.email ? ' (' + user.email + ')' : '');
    showApp();
    // load streams and posts
    loadStreams();
    loadPosts('all');
  }

  // Si volvemos del OAuth callback, verificamos sesión en backend
  window.Auth.getCurrentUser().then(user => {
    if (user) { onLogin(user); } else { window.Auth.beginLogin(); }
  });

  window.MiniTwitter = { loadStreams, loadPosts, onCategorySelect };

});
