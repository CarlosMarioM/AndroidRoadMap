# Android Studio Profiler

This document explains **how to use Android Studio Profiler** to measure, analyze, and optimize app performance, focusing on CPU, memory, network, and energy metrics.

Profiling is essential for **detecting bottlenecks, jank, and memory leaks**.

---

## Overview of Profiler tools

### 1. CPU Profiler

- Shows **method execution over time**
- Detects **slow methods, UI thread blocking, excessive recompositions**
- Modes:
  - **Sampled**: low overhead, approximate timings
  - **Instrumented**: precise, higher overhead

**Use case:** Finding methods causing jank or startup delays

### 2. Memory Profiler

- Tracks **heap allocations and deallocations**
- Detects **leaks, over-allocation, and GC events**
- Features:
  - Heap dump
  - Allocation tracking
  - Reference tree

**Use case:** Detect memory leaks in Activities, Fragments, or Compose

### 3. Network Profiler

- Shows **HTTP/HTTPS traffic, bytes transferred, request/response times**
- Detects unnecessary requests or large payloads

**Use case:** Network bottlenecks, redundant calls, inefficient image loading

### 4. Energy Profiler

- Estimates **battery impact** of CPU, network, and GPS
- Detects **power-hungry background work**

**Use case:** Optimize WorkManager, location updates, background sync

---

## How to use Profiler effectively

1. **Start app in profiling mode**
   - `Run → Profile 'app'` or `Shift+Alt+F10 → Profile`

2. **Select profiler tab**
   - CPU, Memory, Network, Energy

3. **Record sessions**
   - Start, perform user flows, stop
   - Inspect timelines for spikes or gaps

4. **Analyze traces**
   - Look for **long-running methods**
   - Identify **allocation spikes**
   - Detect **network delays or retries**

5. **Heap dump & GC analysis**
   - Dump memory at strategic points
   - Look for retained objects, leaks, or excessive allocations

6. **Compare frames**
   - Check for **UI jank**, slow frame rendering
   - Use **Frame Timeline** alongside CPU profiler

---

## Advanced tips

### 1. CPU sampling vs instrumentation

- Sampling: low overhead, may miss very short calls
- Instrumentation: precise, may introduce overhead and change behavior
- Choose based on **diagnostic need**

### 2. Memory leaks

- Use **Allocation Tracker**
- Look at **retained sizes**
- Cross-reference with **Garbage Collection events**

### 3. Network issues

- Profile HTTP requests
- Check payload size and frequency
- Identify redundant fetches

### 4. Compose-specific profiling

- Check **recomposition counts**
- Identify **expensive modifiers**
- Use **Layout Inspector with recomposition highlighting**

---

## Workflow example

```text
1. Launch app in Profiler
2. Record CPU while performing navigation
3. Check main thread spikes -> method X is slow
4. Memory profiler -> allocation spikes -> bitmap decoded on main thread
5. Network profiler -> request Y is slow -> caching missing
6. Fix issues -> re-profile -> verify improvements
```

---

## Senior rules

- Always measure before optimizing
- Correlate CPU, Memory, and Network timelines
- Use sampling first, instrument only if needed
- Cross-check Compose recomposition counts
- Treat profiler as **source of truth**, not guesswork

---

## Mental model

> Profiler = X-ray of your app. Every jank, ANR, or memory spike leaves traces. Read them carefully.

---

## Interview takeaway

**Senior Android developers never guess performance issues. They measure, analyze, and fix using Profiler.**

