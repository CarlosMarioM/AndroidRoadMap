# Runtime permissions flow

Runtime permissions are not an API detail — they are a **user-driven control system**. If your flow assumes cooperation, your app will fail in the real world.

This document explains the **only correct mental model** and the **non-negotiable flow**.

---

## Core truth

- Declaring a permission ≠ having it
- Requesting a permission ≠ getting it
- Getting a permission ≠ keeping it

Design accordingly.

---

## When runtime permissions apply

Only **dangerous permissions** require runtime handling.

Normal permissions:
- Are granted automatically
- Never enter this flow

If you’re checking `INTERNET` at runtime, you’re already wrong.

---

## The mandatory flow (no shortcuts)

### 1. Check before every use

Before accessing a protected API:
- Check permission
- Assume it may be revoked

Never cache permission state.

---

### 2. Decide if a request is appropriate

Ask yourself:
- Is the feature being used *right now*?
- Does the user understand the benefit?

If the answer is no, **do not request yet**.

---

### 3. Show rationale (when required)

Rationale is needed when:
- User previously denied
- Permission is still requestable

Purpose:
- Explain value
- Explain consequence
- No pressure

This is UX, not legal text.

---

### 4. Request the permission

The system dialog:
- Cannot be customized
- Cannot be bypassed
- Will be ignored by many users

Treat denial as the default outcome.

---

### 5. Handle the result explicitly

Outcomes:
- Granted
- Denied (can ask again)
- Denied permanently ("Don’t ask again")

Each requires **different behavior**.

---

## Permanent denial (critical)

When the user selects:
> Don’t ask again

The system will:
- Stop showing dialogs
- Always return DENIED

Your only option:
- Explain limitation
- Link to system settings

Never loop requests.

---

## Revocation after grant

Permissions can be revoked:
- From system settings
- Automatically by Android
- After app updates

a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result:
- Your app may crash if you assume access

Always re-check **right before use**.

---

## Timing rules (most apps get this wrong)

### Never request on app launch

At startup:
- User has no context
- Denial rates are highest

Requesting here is lazy design.

---

### Request at point of value

Correct timing:
- User taps “Scan QR” → request Camera
- User taps “Share location” → request Location

This aligns intent with permission.

---

## Multiple permissions

Rules:
- Request the minimum set
- Avoid batch requests
- Sequence if necessary

One dialog ≠ better UX.

---

## UX failure modes

Common mistakes:
- Blocking the app on denial
- Re-requesting immediately
- Showing no explanation
- Treating permission as mandatory

These lead to:
- One-star reviews
- App uninstalls
- Play policy warnings

---

## Architectural rule

Permission handling belongs to:
- UI layer (Activity / Fragment)

Not in:
- ViewModels
- Repositories
- Domain logic

Permissions are **platform concerns**.

---

## Testing reality

You must test:
- First install
- Denial
- Permanent denial
- Revocation while app is backgrounded

Most bugs only appear here.

---

## Senior-level mental model

Runtime permissions are a **negotiation**, not a request.

The user owes you nothing.

Good apps:
- Ask late
- Ask clearly
- Respect refusal

Bad apps:
- Beg early
- Loop dialogs
- Break on denial

Design like the answer is “no” — and earn the “yes”.

