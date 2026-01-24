# Compose Interop — Compose ↔ View system

## Why interop exists

Jetpack Compose did **not** replace the View system overnight. Real apps:
- Migrate gradually
- Reuse existing Views
- Embed Compose into legacy screens

Interop is a **transition layer**, not a permanent architecture goal.

Official references:
- https://developer.android.com/jetpack/compose/interop

---

## Direction 1: Compose in View-based apps

### `ComposeView`

`ComposeView` is a regular Android `View` that hosts a Compose composition.

Used when:
- Migrating screen by screen
- Adding Compose components to existing layouts
- Keeping Fragment / Activity structure

Key rules:
- The composition is tied to the View lifecycle
- Disposal happens automatically when the View is detached

---

### Setting content

Compose content must be set exactly once per `ComposeView` instance.

Good:
- Set content in `onCreateView` or `onViewCreated`

Bad:
- Resetting content repeatedly
- Creating new `ComposeView` instances unnecessarily

Compose inside Views should behave like a **leaf UI element**, not a container for logic.

---

### Lifecycle and state

ComposeView integrates with:
- LifecycleOwner
- ViewModelStoreOwner
- SavedStateRegistryOwner

This allows:
- `viewModel()` usage
- State restoration
- Proper cleanup

If these owners are missing, things break quietly.

---

### Performance considerations

Avoid:
- Large Compose trees inside RecyclerView items
- Frequent creation/destruction of ComposeView
- Mixing heavy recomposition with View invalidations

Best practice:
> Use ComposeView at screen or section boundaries, not per pixel.

---

## Direction 2: Views inside Compose

### `AndroidView`

`AndroidView` allows embedding classic Views inside Compose.

Used when:
- A View-based component has no Compose equivalent
- Rewriting is too expensive or risky
- Platform APIs still require Views

Examples:
- MapView
- WebView
- Camera previews

---

### Factory vs update

`AndroidView` has two distinct phases:

- `factory` → called once, create the View
- `update` → called on recomposition

Critical rule:
> Never recreate the View in `update`

All state updates must be **idempotent**.

---

### Lifecycle ownership

Views inside Compose:
- Do NOT automatically get a lifecycle
- Must be manually wired if needed

Compose does not magically solve View lifecycle problems.

---

## Measurement and layout differences

Compose:
- Constraint-based measurement
- Single-pass layout per node

Views:
- Measure specs
- Multiple passes

Interop cost:
- Extra measurement overhead
- Layout bridging

This is fine for coarse-grained usage, not for dense UI.

---

## Input, focus, and accessibility

Interop supports:
- Touch input
- Focus
- Accessibility

But edge cases exist:
- Nested scrolling conflicts
- Focus traversal issues
- IME handling quirks

Test thoroughly when mixing systems.

---

## State synchronization pitfalls

Common mistakes:
- Duplicating state in Compose and View layers
- Imperative View mutations driven by Compose state
- Bidirectional updates without a single source of truth

Rule:
> One state owner. Everything else observes.

---

## When NOT to use interop

Avoid interop when:
- Starting a new feature
- Building reusable UI libraries
- Performance is critical

Interop should shrink over time, not grow.

---

## Migration strategy

Recommended approach:
1. Start with ComposeView at screen boundaries
2. Extract reusable UI into pure Compose
3. Replace View-based components incrementally
4. Remove interop once the feature is fully migrated

Do not mix paradigms permanently.

---

## Mental model

Interop is:
- A bridge
- A compromise
- A migration tool

It is not an excuse to avoid architectural decisions.

---

## Official documentation

- Interop overview: https://developer.android.com/jetpack/compose/interop
- Compose in Views: https://developer.android.com/jetpack/compose/interop/interop-apis#compose-in-views
- Views in Compose: https://developer.android.com/jetpack/compose/interop/interop-apis#views-in-compose

---

## Senior-level takeaway

If you:
- Use ComposeView intentionally
- Keep interop boundaries coarse
- Maintain a single state source
- Plan to delete interop code

You’re doing Compose migration **correctly**, not just making it compile.

