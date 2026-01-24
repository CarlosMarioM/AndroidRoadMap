# Network and Disk Bottlenecks

This document explains **how network and disk operations impact app performance**, why they often cause jank or ANRs, and how senior developers design around these bottlenecks.

These are not just IO delays — they are **main-thread hazards** and **startup killers**.

---

## Why IO is critical

- Network and disk are **orders of magnitude slower** than CPU and memory
- Blocking operations on main thread → dropped frames / ANRs
- High-latency IO affects startup, scrolling, and background sync

---

## Common mistakes

### 1. Synchronous IO on main thread

```kotlin
fun loadFile() {
    val content = File(context.filesDir, "data.json").readText() // ❌ blocks main thread
}
```

```kotlin
fun fetchData() {
    val response = HttpURLConnection(url).inputStream.readBytes() // ❌
}
```

Impact:
- Immediate jank
- ANR if > 5 seconds

---

### 2. Poor caching strategy

- Re-fetching the same data repeatedly
- Ignoring local storage for frequently-used content
- Leads to unnecessary network load and slow UI

---

### 3. Large payload processing on main thread

- Parsing JSON / images / compressed files synchronously
- Bitmap decoding on UI thread

```kotlin
val bitmap = BitmapFactory.decodeFile(path) // ❌ on UI thread
```

---

### 4. Ignoring network variability

- Assuming network is always fast
- No timeouts → stuck connections
- Blocking operations cascade into UI delays

---

## Senior-level mitigation strategies

### 1. Use asynchronous APIs

- Retrofit + coroutines
- OkHttp asynchronous calls
- `suspend` functions

```kotlin
suspend fun fetchUser(): User = withContext(Dispatchers.IO) {
    retrofitService.getUser()
}
```

### 2. Threading & Dispatcher confinement

- IO-bound work → `Dispatchers.IO`
- CPU-bound work → `Dispatchers.Default`
- Main thread → UI updates only

```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    val data = repository.loadFromDisk()
    withContext(Dispatchers.Main) { updateUI(data) }
}
```

### 3. Caching

- Memory cache for frequent reads
- Disk cache for larger datasets
- Use `DataStore`, Room, or custom caching

### 4. Paging & partial loading

- Avoid loading entire datasets at once
- Load in chunks with RecyclerView / Paging 3
- Reduce memory & CPU overhead

### 5. Timeouts and retries

- OkHttp: set connect/read/write timeouts
- Retry policies for transient failures
- Avoid indefinite blocking

```kotlin
val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .retryOnConnectionFailure(true)
    .build()
```

### 6. Image and media loading

- Glide, Coil, Picasso for async decoding
- Downscale images to display size
- Avoid synchronous decoding on UI thread

---

## Compose-specific notes

- Use `LaunchedEffect` for async loading
- Use `remember` + `produceState` to avoid recomposing during network load

```kotlin
val image by produceState<ImageBitmap?>(initialValue = null) {
    value = loadImageFromNetwork(url)
}
```

---

## Mental model

> UI thread is sacred. IO is slow. Always separate them.

Think of network/disk work as **external events** that must never block the frame pipeline.

---

## Interview takeaway

**Blocking IO = jank + ANRs.**

Senior developers always isolate IO, cache aggressively, and design for **unpredictable latency**.

