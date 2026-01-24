# Accessibility & TalkBack — Jetpack Compose

## Why accessibility matters (for real)

Accessibility is **not optional polish**. It is:
- A functional requirement
- A legal requirement in many regions
- A correctness requirement

If TalkBack cannot understand your UI, your UI is broken.

Compose gives you powerful tools, but it will **not save you automatically**.

Official reference:
- https://developer.android.com/jetpack/compose/accessibility

---

## How TalkBack sees Compose

TalkBack does **not** understand composables.

It understands:
- Semantics nodes
- Accessibility properties
- Focus order

Compose builds a **semantics tree** parallel to the UI tree.

Key rule:
> What TalkBack reads comes from semantics, not visuals.

---

## Semantics basics

### `Modifier.semantics`

The core API for accessibility metadata.

Used to define:
- Content descriptions
- Roles
- States
- Actions

Example use cases:
- Custom components
- Complex layouts
- Non-standard interactions

---

### Common semantics properties

Important properties:
- `contentDescription`
- `role`
- `stateDescription`
- `disabled`
- `selected`
- `checked`

If these are missing, TalkBack guesses — badly.

---

## Clickable and focusable elements

Compose automatically adds semantics for:
- `Button`
- `Checkbox`
- `Switch`
- `TextField`

But **only if you use them correctly**.

Anti-pattern:
- `Box` + `clickable` pretending to be a button

If you build custom controls, you must:
- Assign a role
- Expose actions
- Expose state

---

## Merging and clearing semantics

### `mergeDescendants`

Used to:
- Combine multiple nodes into one accessible element
- Avoid verbose TalkBack output

Example:
- Card with icon + text

---

### `clearAndSetSemantics`

Used to:
- Replace all child semantics
- Fully control TalkBack output

Danger:
> This erases child accessibility entirely

Only use when you know exactly what you’re doing.

---

## Focus order and traversal

Compose determines focus order based on:
- Layout order
- Semantics tree structure

You can override using:
- `Modifier.focusOrder`
- `Modifier.focusRequester`

Use this for:
- Custom layouts
- Non-linear navigation

Never rely on visual order alone.

---

## Dynamic state announcements

### `stateDescription`

Used for dynamic UI state:
- Loading
- Error
- Expanded / collapsed

Example:
> "Expanded"

Without this, TalkBack users miss critical context.

---

### Live region behavior

Compose supports live updates via semantics.

Use sparingly:
- Important changes only
- Avoid noisy updates

Overuse makes apps unusable.

---

## Images and icons

Rules:
- Decorative images → no contentDescription
- Informative images → meaningful description

Anti-pattern:
- Repeating visible text as contentDescription

Screen readers read everything — redundancy is bad UX.

---

## Text and reading behavior

Compose Text:
- Supports TalkBack automatically
- Respects font scaling

You must:
- Avoid hardcoded text sizes
- Respect `sp`
- Allow wrapping

Accessibility fails silently when layouts break under scaling.

---

## Custom gestures and actions

If your UI supports:
- Swipe
- Drag
- Long press

You must expose:
- Equivalent semantic actions

TalkBack users cannot perform complex gestures reliably.

If an action is unreachable, the feature does not exist.

---

## Testing TalkBack support

### Manual testing

Required steps:
1. Enable TalkBack
2. Navigate without looking
3. Verify order, meaning, and state

If it feels confusing, it is.

---

### Automated checks

Use:
- Accessibility Scanner
- Compose UI tests with semantics assertions

Automation catches regressions, not design flaws.

---

## Common Compose accessibility failures

- Clickable containers without roles
- Missing state descriptions
- Overuse of `clearAndSetSemantics`
- Custom components without actions
- Ignoring font scale

Most of these ship to production unnoticed.

---

## Performance and accessibility

Semantics have a cost:
- Larger semantics trees
- More nodes

But:
> Accessibility cost is not optional

Optimize structure, not correctness.

---

## Mental model

Accessibility in Compose is:
- Explicit
- Declarative
- Developer responsibility

If TalkBack output is wrong, the code is wrong.

---

## Official documentation

- Accessibility overview: https://developer.android.com/jetpack/compose/accessibility
- Semantics: https://developer.android.com/jetpack/compose/accessibility#semantics
- Testing accessibility: https://developer.android.com/jetpack/compose/accessibility#testing

---

## Senior-level takeaway

If you:
- Treat semantics as part of your API
- Test with TalkBack enabled
- Design custom components accessibly

You’re building **real, production-grade Compose UIs**, not demos.