# Intent Filters

Intent filters are **the contract between your app and the system**. They define **how your app can be launched and what data it can receive**.

Misunderstanding intent filters causes:
- app hijacking,
- unexpected launches,
- security vulnerabilities,
- broken navigation.

---

## What an Intent Filter really is

An intent filter:
- Declares **what actions** your component can handle
- Declares **what data** formats your component can consume
- Declares **which categories** your component belongs to

It is *not* a magic router. It is **a system entry point declaration**.

---

## Components that can have intent filters

- Activities
- Services
- BroadcastReceivers

Each type has **different semantics**, but the filter works the same way: match system or app intents.

---

## Key parts of an intent filter

### Action

Defines the **verb** or purpose.

Example:
```xml
<action android:name="android.intent.action.VIEW" />
```

### Data

Defines **what kind of data** the component can handle:
- Scheme (`http`, `https`, `content`, `file`)
- Host (`example.com`)
- Path (`/path`)
- MIME type (`image/*`)

Example:
```xml
<data android:scheme="https" android:host="example.com" />
```

### Category

Defines **context or role**:
- `DEFAULT` = required for explicit start via `startActivity`
- `BROWSABLE` = can be launched from web links
- `LAUNCHER` = main entry point

---

## Matching rules (how the system decides)

System matches based on:
1. Action
2. Data type & URI
3. Categories

Only fully matching filters are invoked.

---

## Risks and common mistakes

1. **Overly broad filters**
   - Can capture intents intended for other apps
   - Opens app hijacking

2. **Ambiguous filters**
   - Multiple apps match → chooser dialog
   - User confusion and bad UX

3. **Assuming `DEFAULT` automatically applies**
   - Can block internal explicit navigation

4. **Treating filters as event buses**
   - Filters are **entry points**, not internal messaging systems

---

## Security implications

- Filters define **exported surfaces**
- Combined with `android:exported`, they control **who can start your components**
- Misconfiguration leads to:
  - Malicious invocation
  - Data leaks
  - Unauthorized actions

Always validate incoming intents and sanitize data.

---

## Best practices

- Be as narrow as possible
- Only declare what is actually needed
- Always validate incoming data
- Avoid exposing sensitive functionality via broad filters
- Combine with explicit permissions if needed

---

## Senior-level mental model

Intent filters are **entry point contracts**:
- Match system or external requests
- Declare capabilities
- Expose only what is safe

They are **not internal routers**.

Treat every filter as a potential security boundary.

Think: “who could call this, and what could they do?”

If you cannot answer that clearly, the filter is too broad.

