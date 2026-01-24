# DataStore — Preferences vs Proto

This document explains **Android DataStore** properly: what it replaces, how it works internally, and when to use **Preferences DataStore** vs **Proto DataStore**. This is not a migration guide — it’s a decision and design guide.

---

## Mental model: what DataStore actually is

DataStore is:
- An **asynchronous, transactional key–value storage**
- Built on **Kotlin coroutines and Flow**
- Designed to replace **SharedPreferences**

DataStore is **not**:
- A database
- A cache layer
- A replacement for Room

If you store relational or large structured data here, you’re misusing it.

---

## Why SharedPreferences was replaced

SharedPreferences problems:
- Synchronous I/O on main thread
- No transaction guarantees
- Silent corruption
- No type safety

DataStore fixes all of these by design.

---

## Two DataStore flavors

| Feature | Preferences DataStore | Proto DataStore |
|------|-----------------------|------------------|
| Schema | No | Yes (proto) |
| Type safety | Weak | Strong |
| Keys | Dynamic | Fixed |
| Refactoring safety | Low | High |
| Best for | Simple flags | Structured settings |

---

# Preferences DataStore

## What it is

Preferences DataStore is:
- A **key–value map**
- Similar to SharedPreferences
- Schema-less

Use it only when you truly don’t need structure.

---

## Defining keys

```kotlin
val DARK_MODE = booleanPreferencesKey("dark_mode")
```

Keys are strings. Rename them and you break stored data.

---

## Reading data

```kotlin
val darkModeFlow: Flow<Boolean> = dataStore.data
    .map { prefs -> prefs[DARK_MODE] ?: false }
```

Facts:
- Always returns a Flow
- Emits on change
- Safe for Compose

---

## Writing data

```kotlin
dataStore.edit { prefs ->
    prefs[DARK_MODE] = true
}
```

Edits are:
- Atomic
- Serialized
- Off main thread

---

## When Preferences DataStore is appropriate

Good uses:
- Feature flags
- Simple toggles
- One-off values

Bad uses:
- Complex settings
- Versioned data
- Anything long-lived

---

# Proto DataStore

## What it is

Proto DataStore:
- Uses **Protocol Buffers**
- Has a **strict schema**
- Is fully type-safe

This is the **default choice** for non-trivial data.

---

## Defining a proto schema

```proto
syntax = "proto3";

message UserSettings {
  bool dark_mode = 1;
  int32 font_size = 2;
}
```

Fields are numbered. Never change numbers after release.

---

## Creating the DataStore

```kotlin
object UserSettingsSerializer : Serializer<UserSettings> {
    override val defaultValue = UserSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserSettings =
        UserSettings.parseFrom(input)

    override suspend fun writeTo(t: UserSettings, output: OutputStream) =
        t.writeTo(output)
}
```

---

## Reading proto data

```kotlin
val settingsFlow: Flow<UserSettings> = dataStore.data
```

Strongly typed. No missing keys.

---

## Writing proto data

```kotlin
dataStore.updateData { current ->
    current.toBuilder()
        .setDarkMode(true)
        .build()
}
```

Updates are:
- Atomic
- Thread-safe
- Schema-validated

---

## Migration support

Proto DataStore supports:
- Schema evolution
- Field addition
- Backward compatibility

Preferences DataStore does not.

---

## Performance characteristics

Facts:
- Single file
- Serialized access
- Not for high-frequency writes

Use Room for anything write-heavy.

---

## DataStore + Architecture

Correct placement:
- Data layer
- Exposed via repository

Never access DataStore directly from UI.

---

## Common mistakes (seen in production)

- Using Preferences DataStore for structured data
- Using DataStore as a database
- Writing on every UI interaction
- Injecting DataStore into Composables

---

## Decision rule (memorize this)

- Simple flags → Preferences DataStore
- Structured, evolving data → Proto DataStore
- Lists / relations / queries → Room

If you hesitate between Room and DataStore, the answer is usually Room.

