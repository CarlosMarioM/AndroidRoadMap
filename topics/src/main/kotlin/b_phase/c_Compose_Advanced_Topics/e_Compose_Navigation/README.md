# Compose Navigation — Jetpack Compose

## What Compose Navigation actually is

Compose Navigation is a **state-driven navigation framework** built on top of the Navigation component, adapted to Compose’s declarative model.

Key idea:
> Navigation state determines what composable is shown.

You do **not** push screens imperatively. You update navigation state, and the UI reacts.

Official reference:
- https://developer.android.com/jetpack/compose/navigation

---

## Core concepts

### `NavController`

The single source of truth for navigation state.

Responsibilities:
- Holds the back stack
- Handles navigation actions
- Restores state across process death

Rules:
- One `NavController` per navigation graph
- Must be remembered (`rememberNavController()`)
- Never recreate it during recomposition

---

### `NavHost`

The container that displays destinations.

Responsibilities:
- Observes `NavController`
- Shows the current destination
- Owns the navigation graph

Key rule:
> `NavHost` is equivalent to a FragmentContainerView

It should be placed **once**, near the root of your UI.

---

### Destinations

Each destination is:
- A route (string or typed)
- A composable function

Example mental model:
> Route = state key
> Composable = UI for that state

Avoid deep nesting of NavHosts unless required (bottom tabs, modal flows).

---

## Routes and arguments

### String-based routes

Classic approach:
- Routes as strings
- Arguments encoded in the route

Problems:
- No compile-time safety
- Easy to break refactors

---

### Typed destinations (recommended)

Modern Compose Navigation supports **typed routes** using `@Serializable`.

Benefits:
- Type safety
- Refactor-friendly
- Clear contract between screens

Use this whenever possible in production apps.

---

## Navigation actions

### `navigate()`

Triggers a navigation state change.

Important flags:
- `popUpTo` → clears part of back stack
- `launchSingleTop` → avoids duplicate destinations
- `restoreState` → restores previously saved state

Bad practice:
- Navigating blindly without stack control

Good practice:
- Always define back stack intent

---

### `popBackStack()`

Removes destinations from the stack.

Rules:
- UI back = navigation back
- Don’t manually manage back stack state

Compose integrates automatically with system back handling.

---

## Lifecycle and ViewModels

### ViewModel scoping

Navigation defines **ViewModel lifetimes**.

Scopes:
- Destination-scoped ViewModel
- Graph-scoped ViewModel

Key insight:
> ViewModels survive recomposition but die with their nav scope

This replaces Fragment-based scoping patterns.

---

### SavedStateHandle

Used for:
- Passing small pieces of state
- Process death restoration

Do not abuse it for large objects or navigation-only data.

---

## Nested navigation graphs

Used for:
- Bottom navigation
- Authentication flows
- Feature isolation

Rules:
- One NavController
- Multiple graphs
- Clear ownership boundaries

Avoid:
- Multiple controllers unless absolutely required

---

## Dialogs and bottom sheets

Compose Navigation supports:
- Dialog destinations
- Bottom sheet destinations

Advantages:
- Fully integrated with back stack
- Lifecycle-aware
- No manual dismissal handling

Prefer these over manual `if (showDialog)` when navigation-related.

---

## State restoration

Compose Navigation automatically handles:
- Configuration changes
- Process death

Requirements:
- Stable routes
- Serializable arguments
- Remembered NavController

Common mistake:
- Putting navigation logic inside non-stable lambdas

---

## Animation and transitions

Compose Navigation integrates with Compose animation APIs.

Supports:
- Enter / exit transitions
- Pop transitions
- Shared element patterns (with care)

Guideline:
- Navigation animations should be subtle
- Heavy animations belong inside destinations

---

## Deep links

Navigation supports:
- App links
- Web URLs
- Intent-based navigation

Rules:
- Deep links must map to routes
- Arguments must be resolvable

Test deep links early — they break silently.

---

## Performance considerations

Avoid:
- Recreating NavController
- Heavy logic inside destination lambdas
- Massive graphs in a single file

Best practices:
- Feature-based graph files
- Typed routes
- Stable arguments

Navigation overhead is usually negligible — misuse is the problem.

---

## Common anti-patterns

- Treating navigation like Fragment transactions
- Passing entire objects through routes
- Multiple NavControllers for simple flows
- UI logic inside navigation lambdas

Compose Navigation is declarative — treat it as state, not commands.

---

## Mental model

Navigation in Compose is:
- State-driven
- Lifecycle-aware
- Back-stack safe

If navigation feels hard, the architecture is wrong.

---

## Official documentation

- Navigation overview: https://developer.android.com/jetpack/compose/navigation
- Routes and arguments: https://developer.android.com/jetpack/compose/navigation#routes
- Typed destinations: https://developer.android.com/jetpack/compose/navigation#typed-destinations
- ViewModel integration: https://developer.android.com/jetpack/compose/navigation#viewmodel
- Deep links: https://developer.android.com/jetpack/compose/navigation#deeplinks

---

## Senior-level takeaway

If you:
- Control the back stack intentionally
- Scope ViewModels correctly
- Use typed routes
- Avoid Fragment-era habits

You’re using Compose Navigation the **right way**, not just making it work.