# Systrace and Perfetto

This document explains **how to use Systrace and Perfetto to profile Android apps**, analyze performance issues, and detect jank, ANRs, and threading problems.

These tools provide a **system-wide view of your app and the OS**, unlike Android Studio Profiler which is app-focused.

---

## What Systrace / Perfetto are

- **Systrace**: legacy tool for tracing system and app events, included in Android SDK
- **Perfetto**: modern replacement, more powerful, used by Android Studio, traces CPU, GPU, threads, and more
- Both allow **frame-by-frame analysis** of rendering and resource usage

---

## Key metrics to track

- **Frame rendering times** (UI thread + RenderThread)
- **CPU usage per thread**
- **Thread scheduling / blocking events**
- **Async operations** (network, disk, jobs)
- **GPU and SurfaceFlinger activity**

---

## Using Perfetto in Android Studio

1. Open **View → Tool Windows → Profiler**
2. Click **Record System Trace**
3. Select **categories**:
   - `gfx` (frame rendering)
   - `sched` (thread scheduling)
   - `freq` (CPU frequency)
   - `disk` / `mem` for IO and memory
4. Run realistic app flow
5. Stop recording and open trace

---

## Interpreting the trace

- Timeline view shows **main thread, RenderThread, GPU**
- Each spike = time spent processing
- Missed vsync → jank
- Thread blocking → ANR risk
- CPU throttling or contention visible via `freq` events

### Example:
```
16ms frame budget
Main thread: 10ms
RenderThread: 12ms → frame missed → jank
```

---

## Systrace CLI usage

```bash
# Record 10 seconds of system trace
systrace --time=10 -o mytrace.html sched gfx view wm
```

- Open `mytrace.html` in browser
- Inspect threads, scheduling, and frame timelines

---

## Senior-level usage

1. Start with **realistic user flows**
2. Record traces on multiple devices (low-end, high-end)
3. Look for:
   - Frames exceeding 16ms (60Hz) or 8ms (120Hz)
   - Long blocking operations on main thread
   - Excessive CPU or GPU usage
4. Cross-check with **Profiler and LeakCanary** for full picture
5. Optimize layout, recomposition, network/disk operations, and memory usage

---

## Compose-specific considerations

- Use **Layout Inspector with recomposition counts** alongside Perfetto
- Look for **recomposition storms** that spike the main thread
- Inspect **Draw operations** in timeline to detect unnecessary draws

---

## Mental model

> Systrace / Perfetto = X-ray of the entire Android system.

Use it to see **why frames are missed, threads are blocked, or the GPU is saturated**.

---

## Interview takeaway

**Senior developers rely on Perfetto for system-level performance analysis**. It shows **thread interactions, frame timing, and resource bottlenecks**, enabling precise fixes for jank, ANRs, and inefficient layouts.

