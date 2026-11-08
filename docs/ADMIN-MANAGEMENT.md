# Member 5 — Admin management

Open `/login`, sign in as an administrator, then open `/admin/dashboard`.
The existing development seed account is `admin@wedding.com` / `admin123`.

## Features

- Dashboard totals for users, vendors, pending approvals, bookings and events.
- Search couple/admin accounts by name, email or role; edit profile/password and delete accounts.
- Search vendors by business/email and filter approval status; inspect contact, service, rate and description.
- Approve or reject vendors. Rejection requires a reason; approval clears the previous reason.
- Edit vendor profiles and delete vendor accounts plus their packages.
- Recent platform activity shows the latest 20 registrations, event updates and review submissions from current records.
- Persistent admin history records actor, time, action, account ID and reason/details. Newest first, 50 entries per page.
- Matching rose/cream palette, Playfair Display/Poppins fonts, rounded cards and responsive sidebar/tables.

## Structure and integration

`admin/AdminController` owns the admin routes formerly in `contoller/DashboardController`.
`admin/AdminService` handles transactional actions and audit writes. A failed action rolls back its audit record too.
`admin/AdminSchema` creates missing vendor/package/audit tables and adds only missing approval columns.
It works with the project's existing MySQL setup with Flyway disabled. Existing vendors default to PENDING and must be reviewed.
No existing records are deleted by schema setup. Do not enable the legacy Flyway scripts as part of this module.

Vendor models/mappers now expose approval status and reason. Vendor login requires APPROVED status.
`VendorApprovalGuard` checks status on every protected vendor request, including old sessions.
`UserAccountGuard` prevents deleted users from continuing to access customer/admin pages.
Admin writes use POST forms with CSRF tokens. Existing Spring Security limits `/admin/**` to ROLE_ADMIN.
Self-deletion and deletion of the last admin are blocked. An admin cannot change their own login email during a session.
Password changes are optional; passwords and hashes are never included in the audit history.

The new templates are `admin-dashboard.html` and `admin-vendor-edit.html`; the existing `edit-user.html` uses the shared `/css/admin.css` styling.

## Team boundaries

- Member 2: new vendor registrations wait for admin approval before accessing the vendor workboard/profile/packages. Registration itself stays public.
- Member 3: booking vendor choices are still a hard-coded catalog, separate from registered vendor accounts. Connect that catalog to APPROVED database vendors when integrating bookings. Admin approval does not change those demo choices.
- Member 6: review moderation remains in the review module; its existing demo admin endpoints are outside this implementation. The new admin activity view reads review submission records only.
- Platform activity is a current-record summary, not a complete historical audit of all six modules. The admin audit is durable, including after account deletion; it starts when this module is installed.

## Verification

Build/rebuild using IntelliJ, then run `WeddingMangementSystemApplication` with MySQL running.
The HTTP smoke test uses only Python's standard library:

```sh
ADMIN_EMAIL=admin@wedding.com ADMIN_PASSWORD=admin123 python3 scripts/admin-smoke-test.py
```

Run against a development database. The test creates uniquely named vendor/couple accounts and deletes them on success; their audit entries remain intentionally. If interrupted, remove accounts whose names start with `admin-test-` using the admin dashboard.
It covers anonymous/customer restrictions, CSRF rejection, self-deletion prevention, pending/approved/rejected vendor login, existing-session rejection, profile edits, invalid email/rejection reason, account deletion, audit rendering, and deleted-user session blocking.

Validated with an IntelliJ rebuild and real HTTP flows against local MySQL, including a server restart. No browser screenshot/visual rendering test was performed.
