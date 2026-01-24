# Fragments: why they exist, and when to avoid them

See conceptual example: [`FragmentLifecycleExample.kt`](examples/FragmentLifecycleExample.kt)

Fragments are one of Android’s most controversial components because they were introduced to solve **real platform problems**, then widely abused.

Understanding *why* they exist is the difference between using them correctly and hating Android forever.

---

## Why Fragments exist (the real reason)

Fragments were created to solve **three concrete problems**:

1. **Reusable UI composition**
2. **Dynamic UI inside a single Activity**
3. **Decoupling UI pieces from the Activity lifecycle**

They were *not* created to:
- Replace Activities
- Act as mini-Activities
- Host business logic

---

## The historical context (important)

Before Fragments:
- One screen = one Activity
- Tablets required multiple Activities side-by-side
- Navigation logic exploded

Fragments allowed:
- Multiple UI regions in one window
- Phone vs tablet layouts without duplicating logic
- Dynamic UI changes without task stack abuse

Fragments are a **UI composition tool**, not a navigation primitive.

---

## What a Fragment really is

A `Fragment` is:
- A **reusable UI controller**
- Scoped to an Activity
- With its **own view lifecycle**

It is *not*:
- An application entry point
- A lifecycle owner independent of Activity
- A place for long-lived state

Fragments always live *inside* an Activity.

---

## Fragment lifecycle (what actually matters)

Fragments have **two lifecycles**:

1. Fragment lifecycle
2. View lifecycle (`viewLifecycleOwner`)

This distinction exists because:
- Fragment instance can survive
- View can be destroyed and recreated

Ignoring this causes most fragment bugs.

---

### `onCreate`

- Non-UI initialization
- Argument parsing

Never touch views here.

---

### `onCreateView` / `onViewCreated`

- Inflate UI
- Bind views
- Start observing UI state

Always use `viewLifecycleOwner`.

---

### `onDestroyView`

- View is gone
- Fragment may still exist

All view references **must be cleared here**.

---

## Why fragments feel “buggy”

Because developers:
- Treat them like Activities
- Store view references too long
- Ignore view lifecycle
- Mix navigation and logic

Fragments punish sloppy lifecycle thinking.

---

## Fragment + ViewModel contract

Correct pattern:
- Fragment handles UI logic
- ViewModel holds state
- Fragment observes state

Incorrect pattern:
- ViewModel manipulating views
- Fragment storing domain data

Fragments should remain **stateless UI controllers**.

## 8. Fragment a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result API

The Fragment a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result API provides a type-safe and lifecycle-aware way for Fragments to communicate results back to a target Fragment (or the hosting Activity). This replaces older, often error-error-prone methods like `onActivityResult` or direct interface callbacks.

- `setFragmentResult(requestKey, bundle)`: Sends a result.
- `setFragmentResultListener(requestKey, owner, listener)`: Listens for a result.

```kotlin
// In Fragment A (listener)
setFragmentResultListener("requestKey") { requestKey, bundle ->
    val result = bundle.getString("resultKey")
    // Do something with the result
}

// In Fragment B (sender)
setFragmentResult("requestKey", bundleOf("resultKey" to "Hello from B!"))
```

This ensures proper decoupling and lifecycle handling for inter-Fragment communication.

---

## 9. When Fragments are the right tool

Use Fragments when:
- You need multiple UI regions in one Activity
- You want reusable UI blocks
- You use Jetpack Navigation (fragment-based)
- You target tablets / foldables

Fragments shine at **composition**.

---

## 10. When to avoid Fragments

Avoid Fragments when:
- One screen per Activity is enough
- Using pure Compose navigation
- UI is simple and static
- You don’t need reuse or dynamic composition

Fragments add lifecycle complexity.
Don’t pay that cost without reason.

---

## 11. Fragments vs Compose

Compose changes **how UI is written**, not why Fragments exist.

Today:
- Activity + Compose + Navigation → often no Fragments
- Hybrid apps → Fragments still useful

Compose reduces the need for Fragments, but doesn’t invalidate them.

---

## 12. Common real-world mistakes

- Holding view references in fields
- Observing LiveData with Fragment lifecycle
- Launching coroutines tied to Fragment lifecycle incorrectly
- Treating Fragments as navigation state

Most “fragment bugs” are lifecycle bugs.

---

## 13. Senior-level mental model

Fragments exist to **compose UI**, not manage apps.

Rules:
- UI logic only
- Respect view lifecycle
- Delegate state upward

If your Fragment feels complicated, it’s doing too much.

Fragments are not bad.
Misuse is.