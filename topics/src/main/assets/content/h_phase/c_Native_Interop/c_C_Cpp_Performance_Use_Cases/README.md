# C/C++ Performance Use Cases on Android

> C/C++ is not a silver bullet. It is a **specialized tool**.
> This document explains **when native code actually wins**, **why it wins**, and **when it absolutely does not**, in real Android systems.

If you use C++ just because "it’s faster", you will likely make things worse.

---

## The Core Question You Must Ask

Before touching C/C++:

> **What exact bottleneck am I solving?**

If you can’t answer that with:
- a profiler
- a measurable constraint
- a deterministic workload

Stop. Stay in Kotlin.

---

## Why Native Code Can Be Faster (Reality, Not Hype)

C/C++ can outperform Kotlin/Java because:
- No GC pauses
- Manual memory control
- Predictable data layout
- Better SIMD / vectorization
- Tighter CPU cache usage

But this only matters for **specific workloads**.

---

## Legitimate High-Value Use Cases

### 1. Emulation (CPU / GPU / Console)

Example:
- Game Boy / GBA / NES emulators

Why C/C++ wins:
- Millions of instructions per second
- Deterministic execution loops
- Tight memory + CPU coupling

Kotlin overhead here is fatal.

---

### 2. Digital Signal Processing (DSP)

Examples:
- Audio effects (EQ, reverb, autotune)
- Real-time audio analysis
- Video filters

Why native:
- SIMD (NEON)
- Fixed-size buffers
- Real-time constraints

Miss a frame → audible glitch.

---

### 3. Image & Video Processing

Examples:
- OpenCV pipelines
- Real-time camera filters
- Encoding / decoding stages

Why native:
- Large array math
- Cache-friendly loops
- Vector instructions

JNI overhead is negligible compared to work size.

---

### 4. Cryptography & Security Primitives

Examples:
- Hashing
- Encryption
- Secure key handling

Why native:
- Constant-time implementations
- Existing audited libraries
- Reduced attack surface

Still: correctness > speed.

---

### 5. Physics Engines & Simulations

Examples:
- Collision detection
- Particle systems
- Rigid body solvers

Why native:
- Tight loops
- Large numeric workloads
- Predictable memory

---

### 6. Large-Scale Parsing / Decoding

Examples:
- Binary formats
- Custom protocols
- Media container parsing

Why native:
- Zero-copy buffers
- Pointer arithmetic
- Minimal allocations

---

## Where Native Code Does NOT Win

### ❌ Business Logic

Branch-heavy, object-heavy code.
GC is not your bottleneck.

---

### ❌ Networking

Latency is dominated by I/O.
C++ won’t fix slow networks.

---

### ❌ JSON / REST APIs

Parsing cost is trivial compared to:
- Serialization
- Network
- Disk

---

### ❌ UI Logic

UI is main-thread bound.
Native code doesn’t help.

---

## JNI Overhead: The Real Cost

JNI is expensive per call.

Rule of thumb:
- **Few, large native calls** → good
- **Many tiny calls** → terrible

Batch data.
Process in native.
Return results in bulk.

---

## Memory Behavior: The Hidden Advantage

Native code lets you:
- Control allocation patterns
- Avoid temporary objects
- Align memory for cache lines

This is often **more important than raw CPU speed**.

---

## SIMD & NEON (Android’s Secret Weapon)

ARM devices support NEON:
- Vector math
- Parallel operations

C/C++ lets you:
- Use compiler auto-vectorization
- Write explicit NEON intrinsics

Kotlin cannot touch this.

---

## Determinism Matters More Than Speed

In real-time systems:
- Consistency beats peak performance
- Missed deadlines are worse than slow averages

Native code gives you deterministic behavior.

---

## Hybrid Pattern (Best Practice)

```text
Kotlin
  └── orchestration
  └── lifecycle
  └── UI

C/C++
  └── hot loops
  └── math
  └── buffers
```

JNI layer stays thin.
Logic stays readable.

---

## Measuring Before & After (Mandatory)

If you don’t measure:
- CPU time
- Allocation count
- Frame deadlines

You are guessing.

Native code without measurement is superstition.

---

## Common Performance Anti-patterns

### ❌ Native code doing small work

JNI overhead eats gains.

---

### ❌ Re-allocating buffers per call

You lost before you started.

---

### ❌ Ignoring cache behavior

Fast algorithms still stall on memory.

---

## Final Verdict

C/C++ is for:
- Hot loops
- Real-time constraints
- Deterministic execution

It is NOT for:
- General app logic
- Premature optimization

If Kotlin is "slow", your architecture is usually the problem — not the language.

