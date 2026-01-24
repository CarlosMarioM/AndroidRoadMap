# Localization & Resource Qualifiers — Jetpack Compose

## Why localization is not optional

Localization is **not translation at the end**. It affects:
- UI layout
- Accessibility
- State descriptions
- Formatting and pluralization

If your UI only works in English, it is broken.

---

## Compose and Android resources

Compose does **not** replace the Android resource system.

Compose relies on:
- `res/values` XML
- Resource qualifiers
- Android configuration changes

Compose simply **reads** resources declaratively.

---

## String resources

### `stringResource()`

The correct way to load localized strings.

Rules:
- Always use resources for user-facing text
- Never hardcode visible strings
- Never hardcode content descriptions

Strings loaded with `stringResource()` automatically update on:
- Locale change
- Configuration change

---

### Plurals

Use plural resources for counts.

Rules:
- Never concatenate numbers and strings
- Never fake plurals in code

Plural rules vary per language. Android handles this — let it.

---

## Formatting localized text

### Arguments

Use string arguments instead of manual interpolation.

Why:
- Word order differs per language
- Grammar differs per language

Hardcoded string building breaks immediately outside English.

---

### Dates, times, and numbers

Rules:
- Never format dates manually
- Never assume decimal separators
- Never assume 12h/24h time

Always use locale-aware formatters.

---

## Right-to-left (RTL) support

Compose respects RTL automatically **if you let it**.

Requirements:
- Use start/end, not left/right
- Avoid hardcoded padding directions
- Avoid manual layout mirroring

Icons:
- Directional icons must auto-mirror
- Decorative icons are fine

If your UI breaks in Arabic or Hebrew, the layout logic is wrong.

---

## Resource qualifiers

Android selects resources based on configuration.

Common qualifiers:
- Language (`values-es`, `values-fr`)
- Region (`values-en-rGB`)
- Screen size (`sw600dp`)
- Orientation (`land`)
- Night mode (`night`)
- Font scale (implicit)

Compose reacts automatically when configuration changes.

---

## Layout qualifiers and Compose

Compose layouts:
- Replace XML layouts
- Do NOT replace configuration-based design

Use qualifiers for:
- Large screens
- Tablets
- TVs

Avoid:
- Massive `if (screenWidth)` logic

Let the resource system do its job.

---

## Accessibility and localization

Accessibility strings must be localized:
- `contentDescription`
- `stateDescription`
- Live region announcements

Accessibility users rely on **language correctness** more than visuals.

---

## Dynamic locale changes

Compose automatically recomposes on locale change **if**:
- Resources are used correctly
- Strings are not cached incorrectly

Anti-pattern:
- Caching strings with `remember`

Strings are configuration-bound, not state-bound.

---

## Testing localization

Required tests:
- Change system language
- Increase font scale
- Enable RTL

If the UI clips, overlaps, or truncates:
- The layout is fragile

Testing only English at default scale is not testing.

---

## Common localization failures

Very common mistakes:
- Hardcoded strings
- Hardcoded punctuation
- Concatenated strings
- Missing plurals
- Ignoring RTL
- Caching strings incorrectly

Most apps ship with several of these.

---

## Performance considerations

Localization has negligible runtime cost.

Performance problems come from:
- Bad caching
- Over-measuring due to fragile layouts

Correctness beats micro-optimizations.

---

## Mental model

Localization in Compose is:
- Declarative
- Resource-driven
- Configuration-aware

If localization breaks, the architecture is wrong — not Compose.

---

## Senior-level takeaway

If you:
- Use resources consistently
- Respect RTL and font scaling
- Avoid hardcoded assumptions
- Test non-English layouts

You are building **globally correct Compose UIs**, not demos.