# Startup Optimization (Cold / Warm / Hot)

This document explains **how Android app startup really works**, how the system classifies startups, what slows each phase down, and how senior developers optimize without cargo-cult hacks.

Startup performance is one of the **top user-perceived quality metrics** on Android.

---

## Why startup matters

- First impression of your app
- Strongly correlated with retention
- Directly affected by system-level behavior (process, Zygote, ART)

Startup is **not just UI rendering** — it’s process creation, class loading, and main-thread work.

---

## Startup types

Android defines **three startup types**:

| Type | Description |
|----|------------|
| **Cold start** | App process does not exist |
| **Warm start** | Process exists, Activity recreated |
| **Hot start** | Activity resumed, no recreation |

Each has **different bottlenecks**.

---

# Cold Start

## What a cold start is

A cold start happens when:

- App process is not in memory
- Zygote must fork a new process
- App classes must be loaded

This is the **slowest and most important** case.

---

## Cold start sequence

Simplified flow:

```
Zygote fork
 → Application.attachBaseContext()
 → Application.onCreate()
 → ActivityThread
 → Activity.onCreate()
 → setContentView / Compose
 → First frame drawn
```

Everything before the first frame is **user-visible latency**.

---

## Common cold start killers

### 1. Heavy `Application.onCreate()`

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initAnalytics()      // ❌
        initDatabase()       // ❌
        initDI()             // ⚠️ often too heavy
    }
}
```

`Application.onCreate()` runs **before any UI exists**.

---

### 2. Eager dependency graphs

- Dagger/Hilt creating large graphs
- Singleton initialization cascades

Rule:
> If it’s not needed to draw the first screen, defer it.

---

### 3. Class loading & dex size

- Large method count
- Many referenced classes
- Reflection-heavy frameworks

Cold start cost increases with **dex size**.

---

## Cold start optimization strategies

### Defer initialization

```kotlin
lifecycleScope.launchWhenStarted {
    analytics.init()
}
```

Or lazy singletons.

---

### Use App Startup (Jetpack)

```kotlin
class AnalyticsInitializer : Initializer<Analytics> {
    override fun create(context: Context): Analytics {
        return Analytics()
    }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}
```

Benefits:
- Ordered initialization
- Lazy by default
- Better traceability

---

### Reduce work before first frame

Goal:
> First frame ASAP, everything else later.

---

# Warm Start

## What a warm start is

Occurs when:

- App process exists
- Activity was destroyed (e.g. configuration change)

No Zygote fork, no class loading.

---

## Warm start sequence

```
Activity.onCreate()
 → View inflation / Compose
 → First frame
```

Main cost = **UI creation**.

---

## Warm start killers

- Heavy view inflation
- Large Compose trees
- Synchronous state restoration

---

## Warm start optimizations

- Flatten view hierarchies
- Avoid deep Compose trees
- Defer state restoration

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    restoreLater()
}
```

---

# Hot Start

## What a hot start is

Occurs when:

- Activity is still in memory
- App resumes from background

Fastest startup type.

---

## Hot start sequence

```
Activity.onResume()
 → First frame
```

If hot start is slow, you have **logic problems**.

---

## Hot start killers

- Work in `onResume()`
- Blocking observers
- Synchronous refreshes

```kotlin
override fun onResume() {
    refreshAllData() // ❌
}
```

---

## Measuring startup

### Android Studio

- **App Startup Inspector**
- **System Trace**

### Metrics

- Time to first frame (TTFF)
- Fully drawn time

```kotlin
reportFullyDrawn()
```

---

## Compose-specific notes

- Composition cost matters
- Avoid recomposition storms on launch
- Use `remember` correctly

Cold start + Compose = sensitive to structure.

---

## Senior rules

- Cold start is king
- `Application.onCreate()` must be minimal
- Defer aggressively
- Measure, don’t guess

---

## Mental model

> Startup performance is a **budget**.

Spend it only on what the user sees.

---

## Interview takeaway

**Cold start is a system problem.
Warm start is a UI problem.
Hot start is a logic problem.**

