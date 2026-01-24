# Focus Handling — Jetpack Compose

## What focus actually is

Focus is **not visual selection**.

Focus is:
- Which element receives keyboard input
- Which element TalkBack interacts with
- Which element reacts to non-touch navigation

If focus handling is wrong, your UI:
- Breaks for accessibility
- Breaks for keyboards, D‑pads, TV, ChromeOS

This is correctness, not polish.

Official reference:
- https://developer.android.com/jetpack/compose/focus

---

## How focus works in Compose

Compose has a **dedicated focus system**, separate from layout and drawing.

Key components:
- Focus nodes
- Focus traversal order
- Focus events

Focus is driven by:
- Semantics
- Modifier order
- Explicit requests

There is no implicit magic.

---

## Focusable elements

### `Modifier.focusable()`

Marks a composable as able to receive focus.

Used when:
- The component is interactive
- It is not already focusable by default

Many Material components already apply this automatically.

Anti-pattern:
- Making non-interactive UI focusable

Focus noise is just as bad as missing focus.

---

## FocusRequester

### What it does

`FocusRequester` allows **explicit focus control**.

Used for:
- Initial focus
- Restoring focus
- Programmatic navigation

Focus requests are **side effects**, not UI description.

---

### Correct usage

Rules:
- Must be remembered
- Must be used from a side-effect (`LaunchedEffect`, event handler)

Requesting focus during composition is invalid.

---

## Focus traversal

### Default behavior

Compose determines traversal based on:
- Layout order
- Semantics tree

This works for most linear layouts.

---

### Custom traversal

Use when:
- Layout is non-linear
- TV / D‑pad navigation
- Custom grids

Tools:
- `Modifier.focusOrder`
- Directional focus properties

Never assume visual order equals focus order.

---

## Focus groups

### `Modifier.focusGroup()`

Used to group elements so focus:
- Enters the group
- Navigates within it
- Exits predictably

Critical for:
- Dialogs
- Menus
- Complex containers

Without grouping, focus jumps unpredictably.

---

## Focus and accessibility

TalkBack focus and keyboard focus share infrastructure.

If focus order is wrong:
- TalkBack reads nonsense
- Keyboard navigation breaks

Accessibility issues here are usually **logic bugs**, not missing labels.

---

## Focus events

Compose exposes focus state via:
- `onFocusChanged`

Use cases:
- Visual focus indicators
- Lazy loading content
- Analytics (carefully)

Do NOT:
- Trigger navigation directly on focus change

Focus is intent-neutral.

---

## Text fields and IME

Text fields:
- Automatically request focus
- Interact with IME

You must:
- Avoid stealing focus unexpectedly
- Handle focus loss correctly

Unexpected focus changes are one of the fastest ways to annoy users.

---

## Common focus bugs

Very common failures:
- Requesting focus during composition
- Losing focus on recomposition
- Broken traversal in custom layouts
- Focus trapped inside containers
- Clickable but unfocusable elements

Most of these only show up on TV or with TalkBack.

---

## Testing focus

Manual testing:
- Keyboard navigation
- D‑pad / emulator TV
- TalkBack linear navigation

Automated testing:
- Compose UI tests with focus assertions

If you don’t test non-touch input, focus bugs ship.

---

## Performance considerations

Focus system is cheap.

Problems come from:
- Excessive focus nodes
- Over-grouping
- Recreating FocusRequesters

Correctness first, micro-optimization later.

---

## Mental model

Focus in Compose is:
- Explicit
- Predictable
- Shared between accessibility and input

If focus feels unstable, the architecture is wrong.

---

## Official documentation

- Focus overview: https://developer.android.com/jetpack/compose/focus
- FocusRequester: https://developer.android.com/jetpack/compose/focus#focusrequester
- Focus traversal: https://developer.android.com/jetpack/compose/focus#custom-focus-traversal

---

## Senior-level takeaway

If you:
- Treat focus as part of UI correctness
- Control traversal intentionally
- Test beyond touch input

You avoid an entire class of production bugs that most teams never notice.

