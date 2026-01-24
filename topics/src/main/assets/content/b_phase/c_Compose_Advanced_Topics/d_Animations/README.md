# Animations — Jetpack Compose

## What animations are in Compose

In Jetpack Compose, animations are **state-driven**. You don’t animate views; you animate **state changes**, and Compose interpolates values over time.

Key idea:
> UI = f(state)

When state changes, Compose recomposes, and the animation system produces intermediate values between the old and new state.

This is fundamentally different from the imperative View system (ObjectAnimator / ValueAnimator).

Official reference:
- https://developer.android.com/jetpack/compose/animation

---

## Core animation primitives

### `animate*AsState`

The simplest animation API.

Used when:
- You have a **single value**
- You want automatic animation on state change
- No complex lifecycle control is needed

Examples:
- `animateDpAsState`
- `animateFloatAsState`
- `animateColorAsState`

Characteristics:
- Tied directly to composition
- Automatically cancels and restarts
- No manual control

Limitations:
- Not suitable for sequential animations
- No interruption handling logic
- Re-runs whenever the target value changes

---

### `updateTransition`

Used when **multiple values** depend on the same state change.

Instead of multiple independent animations, a `Transition`:
- Coordinates them
- Keeps them in sync
- Reduces recomposition noise

Best use cases:
- Expanding / collapsing UI
- State-based layout changes
- Animating size + alpha + color together

Key rule:
> One state → many animated properties

---

### `Animatable`

The **lowest-level** animation API.

Used when you need:
- Imperative control
- Manual start/stop
- Gesture-driven animations
- Physics-based interactions

Characteristics:
- Controlled from coroutines
- Not automatically tied to recomposition
- Requires `LaunchedEffect` or a scope

This is the closest equivalent to `ValueAnimator`, but **Compose-safe**.

---

## Infinite animations

### `rememberInfiniteTransition`

Used for animations that **never stop**:
- Loading indicators
- Pulsing effects
- Repeating shimmer

Rules:
- Must be remembered
- Automatically runs while in composition
- Stops when the composable leaves composition

Warning:
> Infinite animations are always running — misuse causes battery drain.

---

## Animation specs

Animation specs define **how values interpolate over time**.

Common specs:
- `tween` → time-based, easing curves
- `spring` → physics-based
- `keyframes` → precise timeline control
- `snap` → no animation, immediate jump

Guidelines:
- UI feedback → short tweens
- Natural motion → springs
- Complex choreography → keyframes

Avoid over-customization unless needed — defaults are well tuned.

---

## Visibility animations

### `AnimatedVisibility`

Handles:
- Enter animations
- Exit animations
- Size + alpha + slide combinations

Why it matters:
- Automatically handles layout participation
- Prevents measurement glitches
- Safer than manual alpha/size animation

Use instead of:
- Manually animating `alpha = 0f`
- Toggling `if` blocks with animations

---

## Content transitions

### `AnimatedContent`

Used when **content itself changes**, not just properties.

Examples:
- Screen state changes
- Step-based flows
- Conditional UI branches

Key concept:
> Old content and new content exist simultaneously during transition

This avoids flicker and enables smooth crossfades or transforms.

---

## Gesture-driven animations

Animations often pair with gestures:
- Drag
- Swipe
- Fling

Compose integrates animation with gestures via:
- `Animatable`
- `DecayAnimationSpec`
- `Velocity`

This enables:
- Natural momentum
- Interruptible animations
- Touch-driven physics

This is where Compose clearly surpasses the View system.

---

## Recomposition and animations

Critical rules:

- Animations **do not trigger recomposition per frame**
- Only animated value reads cause recomposition
- Animation clocks live in the runtime, not the UI tree

This is why Compose animations are cheap when used correctly.

Common mistake:
- Recomputing heavy logic inside animated value reads

Fix:
- Precompute using `remember` or `derivedStateOf`

---

## Performance pitfalls

Avoid:
- Creating animation objects on every recomposition
- Infinite animations in offscreen UI
- Animating layout size unnecessarily
- Animating large composables instead of leaf nodes

Best practices:
- Animate **small, isolated values**
- Prefer `updateTransition` for coordinated motion
- Use `Animatable` for gestures only

---

## Mental model

Compose animations are:
- Declarative
- State-driven
- Lifecycle-aware
- Cancellation-safe

If you fight the system imperatively, performance and correctness suffer.

If you model motion as state changes, animations become trivial.

---

## Official documentation

- Animation overview: https://developer.android.com/jetpack/compose/animation
- animate*AsState: https://developer.android.com/jetpack/compose/animation#animate-value
- Transitions: https://developer.android.com/jetpack/compose/animation#transitions
- Animatable: https://developer.android.com/jetpack/compose/animation#animatable
- AnimatedVisibility: https://developer.android.com/jetpack/compose/animation#animatedvisibility
- AnimatedContent: https://developer.android.com/jetpack/compose/animation#animatedcontent

---

## Senior-level takeaway

If you can:
- Choose the right animation primitive
- Predict recomposition behavior
- Avoid layout thrashing
- Control interruption and cancellation

You understand Compose animations at a **production level**, not a demo level.

