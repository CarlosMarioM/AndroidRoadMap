# RTL Support — Jetpack Compose

## What RTL support actually means

RTL (Right-to-Left) support is **not flipping the screen**.

It means:
- Correct reading order
- Correct navigation order
- Correct spatial meaning
- Correct accessibility output

If your UI is mirrored but interactions feel wrong, RTL is broken.

---

## How Compose handles RTL

Compose is **RTL-aware by default**.

It reacts to:
- System locale direction
- Layout direction from configuration

Compose will do the right thing **only if you let it**.

---

## LayoutDirection

Compose uses `LayoutDirection`:
- `LTR`
- `RTL`

This value flows implicitly through composition.

You almost never need to override it manually.

---

## Start / End vs Left / Right

### The most important rule

Always use:
- `start` / `end`

Never use:
- `left` / `right`

Applies to:
- Padding
- Alignment
- Arrangement
- Offsets

If you use left/right, RTL **will break**.

---

## Padding and alignment

Correct:
- `padding(start = …)`
- `Alignment.Start`

Wrong:
- `padding(left = …)`
- `Alignment.Left`

Compose cannot fix hardcoded direction.

---

## Text alignment

Text alignment is **language-dependent**.

Rules:
- Use `TextAlign.Start`
- Avoid `TextAlign.Left`

Text must flow naturally with the language.

---

## Icons and mirroring

### Auto-mirrored icons

Directional icons must support mirroring:
- Back
- Forward
- Arrows

Use:
- Auto-mirrored vector drawables

If an arrow points the wrong way in Arabic, it’s a bug.

---

### Decorative icons

Decorative icons:
- Do not need mirroring
- Should not imply direction

Meaning matters more than symmetry.

---

## Gestures and directionality

Gestures have **semantic meaning**.

Examples:
- Swipe start → back
- Swipe end → forward

Rules:
- Use start/end-based logic
- Avoid raw X-axis comparisons

Hardcoded gesture directions break RTL UX.

---

## Navigation and RTL

Navigation:
- Back gestures
- Drawer directions
- Transitions

Must respect layout direction.

Never assume:
- Back = swipe left

It depends on locale.

---

## Accessibility and RTL

TalkBack:
- Reads in logical order
- Follows semantics tree

If semantics are wrong:
- RTL reading order is wrong

RTL bugs often surface first via accessibility.

---

## Custom layouts and RTL

Custom layouts must:
- Read `LayoutDirection`
- Adjust positioning accordingly

Ignoring layout direction in custom measure/layout logic is a common senior-level bug.

---

## Forcing RTL (testing only)

You can force RTL to test layouts.

Rules:
- Never ship forced RTL
- Never rely on visual inspection only

If forced RTL breaks, real RTL will too.

---

## Common RTL failures

Extremely common:
- Hardcoded left/right paddings
- Absolute offsets
- Non-mirrored icons
- Gesture logic tied to X values
- Custom layouts ignoring layout direction

Most apps fail at least one of these.

---

## Performance considerations

RTL support has **no performance cost**.

If performance changes:
- The layout logic is wrong

RTL correctness is free — breaking it is optional.

---

## Mental model

RTL is:
- A first-class layout mode
- A correctness constraint
- Not an edge case

If RTL feels bolted on, the architecture is flawed.

---

## Senior-level takeaway

If you:
- Use start/end everywhere
- Respect layout direction in custom code
- Test forced RTL
- Validate with TalkBack

Your UI works globally, not just locally.