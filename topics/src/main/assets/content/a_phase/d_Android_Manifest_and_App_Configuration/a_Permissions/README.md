# Permissions: normal vs dangerous

Permissions are not a formality. They are **Android’s security and trust model**.

Misunderstanding them leads to:
- crashes,
- rejected Play Store submissions,
- broken UX,
- and user distrust.

---

## What a permission really is

A permission is:
- A **capability grant** by the system
- Scoped to your app
- Revocable at any time

It is *not*:
- A guarantee
- A permanent approval
- A user promise

Your app must behave correctly **with and without** permissions.

---

## Permission categories

Android groups permissions by **risk level**, not by feature.

The two categories that matter in practice:
- Normal permissions
- Dangerous permissions

Everything else builds on this distinction.

---

## Normal permissions

### What they are

Normal permissions:
- Pose minimal risk to the user
- Do not expose private data
- Do not affect other apps

They are **automatically granted** at install time.

---

### Examples

Common normal permissions:
- `INTERNET`
- `ACCESS_NETWORK_STATE`
- `WAKE_LOCK`

If your app breaks because one of these is missing, that’s a developer error.

---

### Key characteristics

- No runtime request
- No user dialog
- No revocation UI

You declare them and move on.

---

## Dangerous permissions

### What makes a permission dangerous

A permission is dangerous if it:
- Exposes personal data
- Allows tracking or recording
- Affects user privacy or safety

These permissions **require explicit user consent**.

---

### Permission groups

Dangerous permissions are grouped by data domain:
- Location
- Camera
- Microphone
- Contacts
- Storage (scoped)

Granting one permission often grants the whole group.

---

### Examples

- `ACCESS_FINE_LOCATION`
- `CAMERA`
- `RECORD_AUDIO`
- `READ_CONTACTS`

These are **never guaranteed**.

---

## Manifest vs runtime reality

Declaring a dangerous permission in the Manifest:
- Does **not** grant it
- Only makes it requestable

The actual grant happens at runtime — or not at all.

This distinction is non-negotiable.

---

## Runtime permission model

Dangerous permissions:
- Must be checked at runtime
- Must be requested explicitly
- Can be denied permanently
- Can be revoked in system settings

Your app must handle **all outcomes**.

---

## User behavior (harsh reality)

Users:
- Don’t read dialogs
- Don’t care about your feature
- Will deny permissions reflexively

If your app collapses without a permission, that’s bad design.

---

## Proper design principle

Rule:
> A permission enables a feature, it must not block the app.

Good apps:
- Degrade gracefully
- Explain value *before* requesting
- Respect denial

Bad apps:
- Gate the entire UI
- Spam permission dialogs
- Punish the user

---

## Revocation and edge cases

Dangerous permissions can be:
- Revoked while app is backgrounded
- Revoked after updates
- Auto-reset by the system

Always re-check before use.

Assume nothing.

---

## Common mistakes

- Treating permissions as install-time decisions
- Requesting multiple dangerous permissions at startup
- Blocking app usage on denial
- Forgetting revocation handling

These mistakes surface as:
- crashes,
- one-star reviews,
- Play policy violations.

---

## Senior-level mental model

Permissions are **dynamic contracts**, not static flags.

Normal permissions:
- Declare and forget

Dangerous permissions:
- Design around absence
- Request with intent
- Handle denial permanently

If permissions are central to your app’s value,
**your UX must earn them**.

