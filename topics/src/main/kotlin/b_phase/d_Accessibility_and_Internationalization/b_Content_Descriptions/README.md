# Content Descriptions — Jetpack Compose Accessibility

## What a content description really is

A `contentDescription` is **not a tooltip** and not a visual label.

It is:
- Spoken output for screen readers (TalkBack)
- The primary way non-visual users understand UI meaning

If your content description is wrong, **the feature is unusable**.

Official reference:
- https://developer.android.com/jetpack/compose/accessibility#content-descriptions

---

## How TalkBack uses content descriptions

TalkBack builds spoken output from:
- Semantics properties
- View/Compose roles
- State information

Priority order:
1. Explicit semantics (`contentDescription`)
2. Built-in component semantics
3. Guesses (worst case)

Rule:
> If you don’t define it, TalkBack invents it.

---

## Where content descriptions belong

### Icons

Rules:
- Decorative icon → **no contentDescription**
- Meaningful icon → short, clear description

Examples:
- Trash icon → "Delete"
- Back arrow → "Navigate up"

Anti-pattern:
- `"Trash icon"`

Users don’t care what it looks like — they care what it does.

---

### Images

Rules:
- Informational image → describe the information
- Decorative image → null contentDescription

Bad:
- Repeating nearby text

Good:
- Adding missing context

If the image adds no meaning, silence is correct.

---

### Buttons and click targets

Compose components like `Button` already expose semantics.

Do NOT:
- Add redundant contentDescription duplicating text

Example:
- Button text: "Save"
- contentDescription: ❌ unnecessary

Redundancy makes TalkBack verbose and annoying.

---

## How to set content descriptions

### Modifier-based

Use when:
- Custom components
- Non-standard layouts

`Modifier.semantics { contentDescription = "…" }`

---

### Painter-based

For images:

- `Image(contentDescription = …)`
- `Icon(contentDescription = …)`

Passing `null` explicitly means **decorative**.

---

## Dynamic content descriptions

Content descriptions can (and should) change with state.

Examples:
- Play / Pause
- Expand / Collapse
- Favorite / Unfavorite

Rule:
> Describe the current action or state, not the visual.

Bad:
- "Play icon"

Good:
- "Pause"

---

## Content description vs stateDescription

They are **not the same**.

- `contentDescription` → what the element is / does
- `stateDescription` → current state

Example:
- contentDescription: "Wi-Fi"
- stateDescription: "Connected"

Do not overload one to replace the other.

---

## Lists and repeated content

Avoid:
- Identical content descriptions in lists

Problem:
- TalkBack cannot differentiate items

Solution:
- Add contextual info

Example:
- "Email from Alice"
- "Email from Bob"

Context matters more than brevity.

---

## Merging semantics and content descriptions

When using `mergeDescendants = true`:
- Child contentDescriptions may be ignored

When using `clearAndSetSemantics`:
- You must define **everything** yourself

This is a common source of broken accessibility.

---

## Localization

Content descriptions:
- Must be localized
- Must respect pluralization

Never:
- Hardcode strings

Accessibility users are still users.

---

## Common mistakes (very common)

- Describing visuals instead of actions
- Repeating visible text
- Forgetting null for decorative icons
- Hardcoding English strings
- Forgetting to update description with state

Most apps ship with at least three of these.

---

## Testing content descriptions

Manual:
- Enable TalkBack
- Navigate without looking
- Listen for clarity and redundancy

Automated:
- Semantics assertions in Compose UI tests

Automation verifies presence — humans verify meaning.

---

## Performance impact

Content descriptions are cheap.

Over-optimization here is a red flag.

If you’re worried about performance instead of correctness, priorities are wrong.

---

## Mental model

Content descriptions are:
- API contracts for accessibility
- Part of your UI semantics
- Required for correctness

If a feature cannot be understood through TalkBack, it is incomplete.

---

## Official documentation

- Content descriptions: https://developer.android.com/jetpack/compose/accessibility#content-descriptions
- Semantics: https://developer.android.com/jetpack/compose/accessibility#semantics

---

## Senior-level takeaway

If you:
- Describe intent, not visuals
- Avoid redundancy
- Keep descriptions state-aware
- Test with TalkBack

You’re doing accessibility **properly**, not cosmetically.