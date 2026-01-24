# LeakCanary (Deep Usage)

This document explains **how to use LeakCanary to detect, analyze, and fix memory leaks** in Android apps, with advanced patterns and best practices for senior developers.

Memory leaks are one of the **leading causes of ANRs, jank, and app crashes**.

---

## What LeakCanary is

- Open-source memory leak detection library by Square
- Automatically detects retained objects that should have been garbage collected
- Provides heap dumps, references, and leak traces

---

## Setting up LeakCanary

```kotlin
dependencies {
    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.12'
}

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
    }
}
```

- `isInAnalyzerProcess` prevents running app code inside LeakCanary process
- Installed automatically hooks Activity/Fragment lifecycles

---

## How LeakCanary works

1. Monitors lifecycle-aware components
2. Detects references that **should have been cleared**
3. Triggers heap dump
4. Analyzes object graph and shows **shortest strong reference paths**
5. Notifies developer with **notification and LeakTrace**

---

## Detecting leaks

### Common leak sources

- **Activity / Fragment references** held by static fields
- **View bindings not cleared**
- **Callbacks or listeners** not unregistered
- **Coroutines / Jobs** not cancelled on lifecycle end

```kotlin
// Fragment example
private var binding: FragmentHomeBinding? = null

override fun onDestroyView() {
    super.onDestroyView()
    binding = null // prevent leak
}
```

---

### Advanced usage: custom watch

LeakCanary can watch any object explicitly:

```kotlin
val watchedObject = MyClass()
LeakCanary.refWatcher.watch(watchedObject, "MyClass should be GC'd")
```

- Useful for **long-lived objects** that may leak outside lifecycle
- Helps detect **repository, cache, or singleton leaks**

---

### Analyzing LeakTrace

Key points in LeakTrace:
- **Retained size:** memory held by object graph
- **Shortest path to GC root:** shows why object not collected
- **Reference chain:** objects preventing GC

```text
GC Root → Handler → Activity → Fragment → View
```

- Focus on **why the Activity/Fragment is retained**
- Trace references to static fields, singleton caches, or background tasks

---

## Best practices

1. **Clear references in Fragments and Activities**
2. **Cancel background work** (coroutines, WorkManager) on lifecycle end
3. **Use weak references for listeners**
4. **Avoid static references to context**
5. **Integrate LeakCanary in debug builds only**

---

## Compose-specific notes

- Compose state objects can leak if they hold references to long-lived scopes
- Example: `rememberCoroutineScope()` used incorrectly outside lifecycle
- Monitor `ViewModel` scope objects for leaks

---

## Senior-level workflow

1. Enable LeakCanary in debug builds
2. Navigate app flows, simulate rotation and backgrounding
3. Check LeakCanary notifications
4. Inspect LeakTrace → identify root cause
5. Fix reference chain / cancel background jobs / clear bindings
6. Re-test until leaks gone

---

## Mental model

> Any object retained unintentionally is a leak.

LeakCanary = automated microscope for memory retention. Detect, analyze, fix.

---

## Interview takeaway

**Memory leaks are systemic problems. Senior Android devs use LeakCanary to detect leaks, trace references, and enforce lifecycle discipline.**

