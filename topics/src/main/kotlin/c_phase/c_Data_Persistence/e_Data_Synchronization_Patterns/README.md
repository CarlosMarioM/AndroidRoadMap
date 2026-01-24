# Data Synchronization Patterns — Android (No‑BS Guide)

Data synchronization is **not fetching data**. It is the controlled process of **keeping local and remote data consistent over time**.

Most apps get this wrong by:
- Syncing too often
- Syncing in the wrong layer
- Blocking UI on sync
- Not defining conflict rules

This document explains **real synchronization patterns**, **when to use each**, and **how to implement them correctly**.

---

## 1. First principles (non‑negotiable)

Before choosing a pattern, accept these rules:

1. UI must not depend on network availability
2. Local database is the source of truth
3. Sync is asynchronous and failure‑tolerant
4. Conflicts are inevitable — ignoring them is a bug

If your architecture violates these, synchronization will be fragile.

---

## 2. Synchronization ≠ Pagination ≠ Caching

Clarify terms:

- **Caching** → temporary storage to reduce latency
- **Pagination** → loading data in chunks
- **Synchronization** → keeping local & remote data consistent

Paging 3 helps pagination.
Room helps caching.
Sync logic is **separate**.

---

## 3. Pattern 1 — Manual pull‑to‑refresh (simplest)

### When to use
- Small to medium datasets
- User‑driven updates
- Low consistency requirements

### Flow

```
UI → ViewModel → Repository → API → DB → UI
```

### Implementation

```kotlin
class ArticlesRepository(
    private val api: ArticlesApi,
    private val dao: ArticleDao
) {

    fun observe(): Flow<List<Article>> =
        dao.observeArticles().map { it.toDomain() }

    suspend fun refresh() {
        val remote = api.getArticles()
        dao.replaceAll(remote.toEntity())
    }
}
```

### Pros
- Simple
- Predictable

### Cons
- No background updates
- Stale data if user doesn’t refresh

---

## 4. Pattern 2 — App lifecycle sync

Sync when:
- App launches
- App returns to foreground

### When to use
- Content apps
- News feeds
- Dashboards

### Implementation

```kotlin
class SyncManager(
    private val repository: ArticlesRepository
) {
    suspend fun syncOnAppStart() {
        repository.refresh()
    }
}
```

Trigger from Application or ProcessLifecycleOwner.

### Risk
- Over‑syncing
- Needs throttling

---

## 5. Pattern 3 — Time‑based sync (TTL)

Data has an expiration window.

### When to use
- Semi‑static data
- APIs with rate limits

### Implementation idea

```kotlin
if (lastSync < now - CACHE_TTL) {
    refresh()
}
```

Store `lastSync` in DataStore or DB.

### Pros
- Controlled network usage

### Cons
- Slightly stale data

---

## 6. Pattern 4 — Background sync (WorkManager)

Use when:
- Data must stay fresh
- User doesn’t open app often

### When NOT to use
- Real‑time apps
- Tight latency requirements

### WorkManager example

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ArticlesRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result {
        return try {
            repository.refresh()
            a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.success()
        } catch (e: Exception) {
            a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result.retry()
        }
    }
}
```

### Warning
Background sync is **expensive**. Use sparingly.

---

## 7. Pattern 5 — Streaming / near‑real‑time sync

### When to use
- Chat
- Live dashboards
- Collaborative apps

### Tools
- WebSockets
- WebRTC
- Firebase Realtime / Firestore

### Architecture rule

```
Stream → DB → UI
```

Never stream directly to UI state.

---

## 8. Offline‑first sync with Paging + RemoteMediator

This is the **most robust pattern**.

### Flow

```
Paging requests → RemoteMediator → API → DB → PagingSource → UI
```

### Characteristics
- Automatic paging
- Cache‑aware
- Resilient to network loss

Use only when data is large.

---

## 9. Conflict resolution strategies (mandatory decision)

### 1. Server‑authoritative (most common)

```kotlin
@Insert(onConflict = REPLACE)
suspend fun upsert(entity: Entity)
```

### 2. Last‑write‑wins
- Requires timestamps
- Risky for collaborative data

### 3. Merge strategy
- Domain‑specific
- Hard but correct

If users can edit offline, **you must choose one**.

---

## 10. Error handling philosophy

Golden rule:

> Sync failure must not break UI.

Errors are:
- Logged
- Exposed as events (snackbar)
- Retried opportunistically

UI always renders cached data.

---

## 11. Where sync logic belongs

| Layer | Responsibility |
|-----|---------------|
| UI | Display data |
| ViewModel | Trigger sync |
| Repository | Orchestrate sync |
| DB | Persist state |
| Network | Fetch data |

If sync logic is in ViewModel → design leak.

---

## 12. Common mistakes (very common)

- Syncing on every screen
- Syncing in composables
- Blocking UI on sync
- No conflict strategy
- Overusing WorkManager

---

## Final rule

Synchronization is a **background concern**.

If users notice your sync logic, you did it wrong.

