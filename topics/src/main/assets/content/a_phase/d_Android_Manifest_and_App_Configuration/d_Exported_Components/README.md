# Exported Components

`android:exported` is **the single most critical security declaration** in modern Android. Misconfiguration is the fastest path to an app exploit or Play Store rejection.

This document explains exactly what exported components are, how to reason about them, and common mistakes.

---

## What an exported component is

A component (Activity, Service, BroadcastReceiver, ContentProvider) is **exported** if it can be invoked by other apps.

- `android:exported="true"` → system allows external apps to call it
- `android:exported="false"` → internal only

Default behavior changed in Android 12:
- Components with intent filters **must explicitly declare exported**
- Omitting the flag now throws a build error

---

## Why exported components matter

Exported components define **attack surfaces**:
- Other apps can send intents, bind services, or read content
- If your component trusts input, it can be exploited
- Every exported component is a potential vector for **data leaks or privilege escalation**

Misconfigured exported components are one of the **most common security vulnerabilities** in Android apps.

---

## Rules by component type

### Activities
- Exported = anyone can start via Intent
- Critical for deep links and app links
- Validate all input in `onCreate` / `onNewIntent`

### Services
- Exported = anyone can bind or start
- Must enforce permission checks or reject untrusted callers
- Do **not** assume internal context

### BroadcastReceivers
- Exported = can receive broadcasts from any app
- Must validate intent data carefully
- Avoid logic-heavy work in receivers

### ContentProviders
- Exported = any app can query/insert/delete
- Must enforce permissions and sanitize URIs
- Often safest to leave non-exported unless explicitly sharing data

---

## Common misconfigurations

1. **Accidental exports**
- Declaring an intent filter but leaving `exported` unspecified
- Older Android versions defaulted differently, newer versions require explicit

2. **Over-trusting external input**
- Treating external Intents as trusted
- Accepting data without validation

3. **Exporting for convenience**
- “Just to make it work” → security nightmare

4. **Combining with dangerous permissions improperly**
- External apps can bypass internal logic if permissions aren’t enforced

---

## Best practices

- Default to `exported=false` unless explicitly needed
- If exported, **validate everything**
- Combine with explicit permission checks when necessary
- Treat every external entry point as hostile
- Use `Intent.setPackage()` when invoking other apps to reduce exposure

---

## Senior-level mental model

Exported components are **your app’s gates**.

- `true` → external callers allowed → security boundary
- `false` → internal only → safest default

Every exported component should be defensively coded:
- Assume malicious input
- Sanitize and validate
- Enforce runtime permissions

If you cannot defend it, it should not be exported.

Think: **“If another app calls this, can it break or steal?”**
If yes, fix it.

