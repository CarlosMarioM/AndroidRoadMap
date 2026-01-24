# Offline‑First Strategies — Android Architecture (Real‑World Guide)

Offline‑first is **not a feature**. It is an **architectural decision**.

If you apply it blindly, you will slow development and add bugs. If you apply it correctly, your app becomes faster, more reliable, and resilient to bad networks.

This document explains **what offline‑first actually means**, **when it’s worth it**, and **how to implement it correctly** with Room, Paging 3, and modern Android architecture.

---

## 1. What “offline‑first” actually means (no myths)

Offline‑first means:

> **The database is the single source of truth.**

The UI **never depends directly on the network**.

Network becomes:
- A data synchronizer
- A cache warmer
- A background updater

If your UI breaks without internet → you are **online‑first**, not offline‑first.

---

## 2. When offline‑first is worth it (and when it’s not)

### Worth it when
- Feeds, timelines, catalogs
- Expensive or slow APIs
- Users with unstable connections
- Apps used daily
- Any app where latency matters

### Not worth it when
- Authentication screens
- One‑shot forms
- Small admin tools
- Simple CRUD apps
- Tight deadlines with limited scope

Offline‑first is **infrastructure**. Only build it if you need infrastructure.

---

## 3. Core offline‑first architecture

Canonical flow:

```
UI → ViewModel → Repository → Room (source of truth)
                          ↘ Network (sync)
```

Hard rules:
- UI observes **only Room**
- Network never feeds UI directly
- Repository coordinates sync

If you break any of these → offline‑first collapses.

---

## 4. Database as Source of Truth (mandatory)

Room is responsible for:
- Persisting data
- Notifying observers
- Handling invalidation

### DAO example

```kotlin
@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun observeArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ArticleEntity>)
}
```

UI **never** observes network responses.

---

## 5. Repository responsibility (critical)

The Repository:
- Decides **when to sync**
- Merges network + local data
- Handles errors
- Exposes stable flows

```kotlin
class ArticlesRepository(
    private val api: ArticlesApi,
    private val dao: ArticleDao
) {

    fun observeArticles(): Flow<List<Article>> =
        dao.observeArticles()
            .map { it.toDomain() }

    suspend fun refresh() {
        val remote = api.getArticles()
        dao.upsertAll(remote.toEntity())
    }
}
```

If your ViewModel calls the API directly → architecture failure.

---

## 6. Offline‑first + Paging 3 (recommended pattern)

This is the **gold standard** for large datasets.

### Architecture

```
Network → Room → PagingSource(Room) → UI
```

### DAO

```kotlin
@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun pagingSource(): PagingSource<Int, ArticleEntity>()
}
```

### Pager

```kotlin
Pager(
    config = PagingConfig(pageSize = 20),
    remoteMediator = ArticlesRemoteMediator(api, db),
    pagingSourceFactory = { dao.pagingSource() }
)
```

Room drives UI updates. Network only fills the DB.

---

## 7. RemoteMediator (network ↔ database bridge)

`RemoteMediator` decides:
- When to fetch
- What page to fetch
- When pagination ends

```kotlin
@OptIn(ExperimentalPagingApi::class)
class ArticlesRemoteMediator(
    private val api: ArticlesApi,
    private val db: AppDatabase
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            // 1. Decide page based on loadType
            // 2. Fetch from network
            // 3. Save to DB in transaction
            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
```

This is complex by design. Use only when pagination + caching matter.

---

## 8. Sync strategies (choose one)

### 1. Manual refresh (simple)
- User pulls to refresh
- Repository fetches and saves
- UI updates automatically

### 2. Time‑based sync
- Sync every X minutes/hours
- Cache validity window

### 3. App lifecycle sync
- Sync on app start
- Sync on foreground

### 4. Background sync (advanced)
- WorkManager
- Requires careful error handling

Don’t mix strategies without a reason.

---

## 9. Conflict handling (often ignored)

You must decide:
- Last‑write‑wins
- Server‑authoritative
- Merge logic

Example: server authoritative

```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsert(entity: ItemEntity)
```

If users can edit offline → conflict strategy is **not optional**.

---

## 10. Error handling in offline‑first

Golden rule:

> Network errors should **not break UI**.

UI continues to show cached data.
Errors are:
- Logged
- Exposed as side effects (snackbar, toast)
- Never fatal to rendering

---

## 11. Testing offline‑first logic

Test layers independently:

- DAO tests (Room)
- Repository tests (fake API)
- RemoteMediator tests

Never test offline‑first only through UI.

---

## 12. Common mistakes (be honest)

- UI observes network directly
- No database
- Manual cache invalidation everywhere
- Sync logic in ViewModel
- Overusing WorkManager

These kill offline‑first benefits.

---

## Final rule

Offline‑first works **only if the database is trusted more than the network**.

If your app still waits for API responses to render → you are not offline‑first.

