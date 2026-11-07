// Front end <-> back end. Every page calls the API of the Spring Boot project (which saves in MySQL).
// Needs bootstrap.bundle.min.js loaded first. Start the Spring Boot project before opening the pages.
(function () {
  'use strict';

  // ===================== Settings: change these two lines when needed =====================
  const API_BASE = '/api';   // where the Spring Boot project runs
  const CURRENT_CUSTOMER_ID = 12;                 // later this comes from the login (User part)

  const page = document.body.dataset.page;        // 'package', 'add', 'edit', 'mine' or 'admin'
  const query = new URLSearchParams(window.location.search);
  let reloadPage = function () {};                // each page sets this, so actions can refresh the list
  let pendingDelete = null;                       // { kind: 'mine' | 'admin', id }

  // ===================== Small helpers =====================
  function esc(value) {                           // stops a comment from running as HTML
    return String(value == null ? '' : value)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
  }

  function starsHtml(n) {
    let out = '<span class="stars" aria-label="' + n + ' out of 5 stars">';
    for (let i = 1; i <= 5; i++) {
      out += i <= n ? '<span class="on">&#9733;</span>' : '<span>&#9733;</span>';
    }
    return out + '</span>';
  }

  function badgeHtml(status) {
    return '<span class="badge rounded-pill status-' + esc(status.toLowerCase()) + '">' + esc(status) + '</span>';
  }

  // The label comes from the Java classes: PublicReview and VerifiedReview each write their own text
  function labelHtml(r) {
    return '<span class="badge rounded-pill text-bg-light border">' + esc(r.label) + '</span>';
  }

  function replyHtml(text, label) {
    if (!text) { return ''; }
    return '<div class="reply-note mt-3"><strong class="d-block small">' + label + '</strong><span>' + esc(text) + '</span></div>';
  }

  function say(message) {                         // small message at the bottom right
    const el = document.getElementById('demoToast');
    if (!el || !window.bootstrap) { return; }
    document.getElementById('demoToastBody').textContent = message;
    bootstrap.Toast.getOrCreateInstance(el, { delay: 2600 }).show();
  }

  function showAlert(message) {
    const box = document.getElementById('pageAlert');
    if (box) { box.innerHTML = '<div class="alert alert-danger" role="alert">' + esc(message) + '</div>'; }
  }

  function clearAlert() {
    const box = document.getElementById('pageAlert');
    if (box) { box.innerHTML = ''; }
  }

  function el(id) { return document.getElementById(id); }

  // ===================== The one function that talks to the back end =====================
  // method: 'GET' | 'POST' | 'PUT' | 'DELETE'      path: '/reviews?packageId=1'
  // fields: an object such as { rating: 5, comment: 'Lovely' }, sent as JSON (POST and PUT)
  async function api(method, path, fields) {
    const options = { method: method };
    if (fields) {
      const values = {};
      Object.keys(fields).forEach(function (key) { values[key] = String(fields[key]); });   // the server reads text and makes the numbers
      options.headers = { 'Content-Type': 'application/json' };
      options.body = JSON.stringify(values);
    }
    let response;
    try {
      response = await fetch(API_BASE + path, options);
    } catch (e) {
      throw new Error('Cannot reach the server. Please start the Spring Boot project (it should run at ' + API_BASE + ').');
    }
    let data = {};
    try { data = await response.json(); } catch (e) { /* the answer had no JSON body */ }
    if (!response.ok || data.success === false) {
      throw new Error(data.message || 'The request failed (' + response.status + ').');
    }
    return data;
  }

  // ===================== Page: reviews of one package (public) =====================
  async function initPackagePage() {
    const packageId = query.get('packageId') || '1';
    el('pageTitle').textContent = 'Package #' + packageId;
    el('writeLink').href = '/review/write?packageId=' + encodeURIComponent(packageId);

    reloadPage = async function () {
      const data = await api('GET', '/reviews?packageId=' + encodeURIComponent(packageId));   // GET /api/reviews?packageId=1
      renderSummary(data);
      renderPackageReviews(data.reviews);
    };
    await reloadPage();
  }

  function renderSummary(data) {
    const box = el('summary');
    if (data.count === 0) {
      box.innerHTML = '<div class="card border-0 mb-4"><div class="card-body text-center p-5">'
        + '<h2>No reviews yet</h2><p class="text-secondary">Be the first to share your experience with this package.</p></div></div>';
      return;
    }
    const counts = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
    data.reviews.forEach(function (r) { counts[r.rating]++; });
    let bars = '';
    for (let s = 5; s >= 1; s--) {
      const pct = Math.floor(counts[s] * 100 / data.count);
      bars += '<div class="d-flex align-items-center gap-3 my-2">'
        + '<span class="small text-secondary" style="width: 56px;">' + s + ' stars</span>'
        + '<div class="progress flex-grow-1" role="progressbar" aria-label="' + s + ' star reviews" aria-valuenow="' + pct + '" aria-valuemin="0" aria-valuemax="100">'
        + '<div class="progress-bar" style="width: ' + pct + '%"></div></div>'
        + '<span class="small text-secondary text-end" style="width: 24px;">' + counts[s] + '</span></div>';
    }
    box.innerHTML = '<section class="card border-0 mb-4" aria-label="Rating summary"><div class="card-body p-4"><div class="row align-items-center g-4">'
      + '<div class="col-md-4 text-center avg-col"><div class="avg-number">' + data.averageRating.toFixed(1) + '</div>'
      + starsHtml(Math.round(data.averageRating))
      + '<p class="text-secondary mb-0 mt-1">' + data.count + (data.count === 1 ? ' review' : ' reviews') + '</p></div>'
      + '<div class="col-md-8 ps-md-4">' + bars + '</div></div></div></section>';
  }

  function renderPackageReviews(reviews) {
    el('reviewList').innerHTML = reviews.map(function (r) {
      return '<article class="card border-0 mb-3 review-card"><div class="card-body d-flex gap-3 p-4">'
        + '<span class="avatar" aria-hidden="true">C' + r.customerId + '</span>'
        + '<div class="flex-grow-1"><div class="d-flex flex-wrap align-items-center gap-2 gap-md-3 mb-1">'
        + starsHtml(r.rating)
        + '<span class="small text-secondary">Customer #' + r.customerId + '</span>'
        + labelHtml(r)
        + '<span class="small text-secondary">' + esc(r.date) + '</span></div>'
        + '<p class="mb-0">' + esc(r.comment) + '</p>'
        + replyHtml(r.reply, 'Reply from our team')
        + '</div></div></article>';
    }).join('');
  }

  // ===================== Page: write a review =====================
  async function initAddPage() {
    const form = el('reviewForm');
    form.elements.packageId.value = query.get('packageId') || '1';
    form.elements.bookingId.value = query.get('bookingId') || '';

    form.addEventListener('submit', async function (e) {
      e.preventDefault();
      clearAlert();
      try {
        const data = await api('POST', '/reviews', {                       // POST /api/reviews
          customerId: CURRENT_CUSTOMER_ID,
          packageId: form.elements.packageId.value,
          bookingId: form.elements.bookingId.value,
          bookingComplete: 'true',                                          // the Booking part will tell us this later
          rating: form.elements.rating.value,
          comment: form.elements.comment.value
        });
        say(data.message);
        setTimeout(function () { window.location.href = '/review/mine'; }, 1500);
      } catch (err) {
        showAlert(err.message);
      }
    });
  }

  // ===================== Page: edit my review =====================
  async function initEditPage() {
    const form = el('reviewForm');
    const id = query.get('id');
    const data = await api('GET', '/reviews/' + encodeURIComponent(id));    // GET /api/reviews/5
    const review = data.review;
    if (review.customerId !== CURRENT_CUSTOMER_ID) {
      throw new Error('You can edit only your own reviews.');
    }
    const radio = form.querySelector('input[name=rating][value="' + review.rating + '"]');
    if (radio) { radio.checked = true; }
    form.elements.comment.value = review.comment;
    form.elements.comment.dispatchEvent(new Event('input'));                         // updates the character counter

    form.addEventListener('submit', async function (e) {
      e.preventDefault();
      clearAlert();
      try {
        const result = await api('PUT', '/reviews/' + encodeURIComponent(id), {   // PUT /api/reviews/5
          customerId: CURRENT_CUSTOMER_ID,
          rating: form.elements.rating.value,
          comment: form.elements.comment.value
        });
        say(result.message);
        setTimeout(function () { window.location.href = '/review/mine'; }, 1500);
      } catch (err) {
        showAlert(err.message);
      }
    });
  }

  // ===================== Page: my reviews =====================
  async function initMinePage() {
    reloadPage = async function () {
      const data = await api('GET', '/reviews?customerId=' + CURRENT_CUSTOMER_ID);   // GET /api/reviews?customerId=12
      const box = el('myReviews');
      if (data.count === 0) {
        box.innerHTML = '<div class="card border-0"><div class="card-body text-center p-5"><h2>You have not written a review yet</h2>'
          + '<p class="text-secondary">Once a booking is complete, you can review the package.</p>'
          + '<a class="btn btn-primary" href="/review/write">Write a review</a></div></div>';
        return;
      }
      box.innerHTML = data.reviews.map(function (r) {
        return '<article class="card border-0 mb-3 review-card"><div class="card-body d-flex gap-3 p-4">'
          + '<span class="avatar" aria-hidden="true">C' + r.customerId + '</span>'
          + '<div class="flex-grow-1"><div class="d-flex flex-wrap align-items-center gap-2 gap-md-3 mb-1">'
          + starsHtml(r.rating) + badgeHtml(r.status) + labelHtml(r)
          + '<span class="small text-secondary">Package #' + r.packageId + '</span>'
          + '<span class="small text-secondary">' + esc(r.date) + '</span></div>'
          + '<p class="mb-0">' + esc(r.comment) + '</p>'
          + replyHtml(r.reply, 'Reply from our team')
          + '<div class="d-flex gap-2 mt-3">'
          + '<a class="btn btn-outline-primary btn-sm" href="/review/edit?id=' + r.id + '">Edit</a>'
          + '<button type="button" class="btn btn-outline-danger btn-sm js-delete" data-kind="mine" data-id="' + r.id + '" data-bs-toggle="modal" data-bs-target="#confirmDelete">Delete</button>'
          + '</div></div></div></article>';
      }).join('');
    };
    await reloadPage();
  }

  // ===================== Page: admin panel =====================
  async function initAdminPage() {
    const filters = el('filters');

    reloadPage = async function () {
      const params = new URLSearchParams({
        status: filters.elements.status.value,
        rating: filters.elements.rating.value,
        packageId: filters.elements.packageId.value
      });
      const data = await api('GET', '/admin/reviews?' + params.toString());      // GET /api/admin/reviews?status=&rating=&packageId=
      el('countText').textContent = data.count + (data.count === 1 ? ' review shown.' : ' reviews shown.');
      el('reviewRows').innerHTML = data.reviews.length === 0
        ? '<tr><td colspan="7" class="text-center text-secondary p-4">No reviews match these filters.</td></tr>'
        : data.reviews.map(adminRowHtml).join('');
    };

    filters.addEventListener('submit', function (e) {
      e.preventDefault();
      reloadPage().catch(function (err) { showAlert(err.message); });
    });
    el('clearFilters').addEventListener('click', function () {
      filters.reset();
      reloadPage().catch(function (err) { showAlert(err.message); });
    });
    await reloadPage();
  }

  function adminRowHtml(r) {
    return '<tr>'
      + '<td class="text-nowrap">' + esc(r.date) + '</td>'
      + '<td>#' + r.customerId + '</td>'
      + '<td>#' + r.packageId + '</td>'
      + '<td>' + starsHtml(r.rating) + '</td>'
      + '<td class="comment-cell"><div class="small text-secondary mb-1">' + esc(r.label) + '</div>' + esc(r.comment)
      + replyHtml(r.reply, 'Your reply')
      + '<a class="d-inline-block mt-2 small fw-semibold" data-bs-toggle="collapse" href="#reply-' + r.id + '" role="button" aria-expanded="false" aria-controls="reply-' + r.id + '">' + (r.reply ? 'Edit reply' : 'Reply') + '</a>'
      + '<div class="collapse mt-2" id="reply-' + r.id + '">'
      + '<form data-action="reply" data-id="' + r.id + '">'
      + '<textarea class="form-control form-control-sm mb-2" name="reply" rows="3" required maxlength="500" aria-label="Reply">' + esc(r.reply) + '</textarea>'
      + '<button type="submit" class="btn btn-primary btn-sm">Send reply</button></form></div></td>'
      + '<td>' + badgeHtml(r.status) + '</td>'
      + '<td><div class="d-flex flex-wrap gap-1">'
      + '<form data-action="approve" data-id="' + r.id + '"><button type="submit" class="btn btn-success btn-sm">Approve</button></form>'
      + '<form data-action="hide" data-id="' + r.id + '"><button type="submit" class="btn btn-outline-secondary btn-sm">Hide</button></form>'
      + '<button type="button" class="btn btn-outline-danger btn-sm js-delete" data-kind="admin" data-id="' + r.id + '" data-bs-toggle="modal" data-bs-target="#confirmDelete">Delete</button>'
      + '</div></td></tr>';
  }

  // Approve, hide and reply buttons in the admin table
  document.addEventListener('submit', async function (e) {
    const form = e.target;
    if (!form.dataset.action) { return; }                                    // not one of the admin action forms
    e.preventDefault();
    clearAlert();
    try {
      const fields = form.dataset.action === 'reply' ? { reply: form.elements.reply.value } : undefined;
      // POST /api/admin/reviews/5/approve   (or /hide, or /reply)
      const data = await api('POST', '/admin/reviews/' + form.dataset.id + '/' + form.dataset.action, fields || {});
      say(data.message);
      await reloadPage();
    } catch (err) {
      showAlert(err.message);
    }
  });

  // Delete buttons open the confirm window first, then this runs
  document.addEventListener('click', function (e) {
    const btn = e.target.closest('.js-delete');
    if (btn) { pendingDelete = { kind: btn.dataset.kind, id: btn.dataset.id }; }
  });

  const confirmBtn = el('confirmDeleteBtn');
  if (confirmBtn) {
    confirmBtn.addEventListener('click', async function () {
      bootstrap.Modal.getInstance(el('confirmDelete')).hide();
      if (!pendingDelete) { return; }
      clearAlert();
      try {
        const data = pendingDelete.kind === 'admin'
          ? await api('DELETE', '/admin/reviews/' + pendingDelete.id)                                        // DELETE /api/admin/reviews/5
          : await api('DELETE', '/reviews/' + pendingDelete.id + '?customerId=' + CURRENT_CUSTOMER_ID);      // DELETE /api/reviews/5?customerId=12
        say(data.message);
        await reloadPage();
      } catch (err) {
        showAlert(err.message);
      }
      pendingDelete = null;
    });
  }

  // ===================== Start =====================
  document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('textarea[data-counter]').forEach(function (area) {   // live character counter
      const out = el(area.dataset.counter);
      if (!out) { return; }
      function update() { out.textContent = area.value.length + ' / ' + area.maxLength; }
      area.addEventListener('input', update);
      update();
    });

    const start = { package: initPackagePage, add: initAddPage, edit: initEditPage, mine: initMinePage, admin: initAdminPage }[page];
    if (start) {
      start().catch(function (err) { showAlert(err.message); });
    }
  });
})();
