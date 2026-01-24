# JIT vs AOT Compilation — How Android Code Actually Runs

## The blunt reality
- **AOT** gives fast startup and predictable performance
- **JIT** gives adaptability and peak performance
- **Modern Android uses both** — intentionally

If you think it’s one or the other, you’re outdated.

---

## Definitions (precise, not textbook)

### Ahead-Of-Time (AOT)
Code is compiled **before execution** into native machine code.

On Android:
- Happens at install time, idle time, or background maintenance
- Uses **profile-guided data** when available

### Just-In-Time (JIT)
Code is compiled **during execution** when it becomes hot.

On Android:
- Happens while the app runs
- Feeds data back into profiles

---

## Historical context (why this matters)

### Dalvik era
- JIT-only
- Cold starts were slow
- UI jank during compilation

### Early ART (Android 5–6)
- Heavy AOT at install
- Fast runtime
- Terrible install times

### Modern ART
- Minimal install work
- JIT + profiles first
- AOT later when device is idle

This is the sweet spot.

---

## How modern ART actually executes code

Execution pipeline:
```
DEX bytecode
   ↓
Interpreter (cold paths)
   ↓
JIT (hot paths)
   ↓
Profile data collected
   ↓
AOT (idle/background)
   ↓
Optimized native code
```

Nothing here is accidental.

---

## Profiles: the missing piece most devs ignore

Profiles tell ART:
- Which classes matter
- Which methods are hot
- What to compile first

Sources of profiles:
- Runtime execution (JIT)
- Baseline Profiles (developer-provided)

Without profiles, AOT is blind.

---

## Baseline Profiles (developer control)

Baseline Profiles allow you to ship **startup-critical paths precompiled**.

Benefits:
- Faster cold start
- Less JIT work
- More consistent performance

Conceptually:
```text
Launch app → critical code already native
```

This is one of the **highest ROI optimizations** today.

---

## Startup performance implications

### Cold start
- Dominated by AOT + profiles
- Sensitive to static initialization

### Warm start
- Mostly native code
- Minimal JIT

### Hot start
- Everything already compiled

Bad architecture defeats all of this.

---

## Memory and battery tradeoffs

### AOT
Pros:
- Less CPU at runtime
- Predictable execution

Cons:
- Uses disk space
- Uses memory for native code

### JIT
Pros:
- Adapts to real usage
- Saves storage

Cons:
- Runtime CPU cost
- Can cause jank if abused

Hybrid = best compromise.

---

## Why JIT is still necessary

Without JIT:
- ART can’t adapt to real usage
- Profiles become stale
- Performance regresses

JIT is not a fallback — it’s a **feedback engine**.

---

## Developer mistakes that sabotage ART

❌ Heavy static initialization
❌ Reflection-heavy startup paths
❌ Massive dependency graphs
❌ Ignoring Baseline Profiles

These hurt both JIT and AOT.

---

## What developers can actually influence

You control:
- Startup code paths
- Static initialization
- Baseline Profiles
- Avoiding unnecessary work on launch

You do *not* control:
- When ART compiles
- Exact compilation strategy

Design for cooperation, not control.

---

## Interview-grade summary

- AOT = fast, predictable startup
- JIT = adaptive, long-term optimization
- Modern ART uses **both**
- Profiles connect JIT to AOT
- Baseline Profiles are the dev leverage point

Understanding JIT vs AOT means understanding **why performance varies across launches and devices**.

