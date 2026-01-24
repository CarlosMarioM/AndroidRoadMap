## Pagination and Partial Loading — Architecture, Patterns, and Android Implementation

Pagination and partial loading are **not UI problems**. They are **data consistency and performance problems**. Bad pagination causes duplicated items, missing data, broken scrolling, cache corruption, and impossible-to-fix bugs.

This document explains *why pagination exists*, *which strategies exist*, *how they fail*, and *how to implement them correctly on Android*, including **Paging 3**, **manual pagination**, and **offline-first considerations**.

---

## Why pagination exists (real reasons)

Pagination exists because:
- APIs cannot return unbounded datasets
- Mobile memory is limited
- Networks are unreliable
- Users don’t need everything at once

Pagination is fundamentally about **controlling cost and risk**.

---

## Pagination vs partial loading (important distinction)

| Concept | Meaning |
|------|--------|
| Pagination | Data is fetched in ordered chunks (pages) |
| Partial loading | Only part of an object or list is loaded |

You can have:
- Pagination without partial loading
- Partial loading without pagination
- Both at the same time

---

## Common pagination strategies

### 1. Offset-based pagination

```
GET /items?offset=40&limit=20
```

**Pros**
- Simple
- Easy to implement

**Cons**
- Breaks on inserts/deletes
- Causes duplicates or skips

**Rule:** Avoid for feeds.

---

### 2. Page-number pagination

```
GET /items?page=3&pageSize=20
```

**Pros**
- Human-readable

**Cons**
- Same problems as offset

Still unsafe for dynamic data.

---

### 3. Cursor-based pagination (recommended)

```
GET /items?cursor=abc123&limit=20
```

**Pros**
- Stable under inserts
- Safe for feeds
- Best UX

**Cons**
- Harder backend implementation

This is the correct default for mobile feeds.

---

## Partial loading strategies

### Field-level partial loading

```
GET /items?fields=id,title,thumbnail
```

Used for:
- Lists vs detail screens
- Faster initial load

### Two-phase loading (list → detail)

1. Load lightweight list items
2. Load full item on navigation

```kotlin
class ItemsRepository {
    suspend fun list(): List<ItemSummary>
    suspend fun detail(id: String): ItemDetail
}
```

---

## Manual pagination (without Paging 3)

Used when:
- Custom backends
- Non-standard pagination
- Extreme control required

### Repository-level pagination

```kotlin
class FeedRepository(
    private val api: FeedApi
) {
    private var cursor: String? = null

    suspend fun loadNext(): List<Post> {
        val response = api.feed(cursor)
        cursor = response.nextCursor
        return response.items
    }
}
```

**Rules:**
- Cursor state never belongs in UI
- Repository owns pagination state

---

## Paging 3 — what it actually does

Paging 3 is:
- A state machine
- A concurrency controller
- A cache boundary

It is **not just lazy loading**.

---

## Paging 3 core components

```
PagingSource → Pager → PagingData → UI
```

### PagingSource

```kotlin
class FeedPagingSource(
    private val api: FeedApi
) : PagingSource<String, Post>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        return try {
            val response = api.feed(
                cursor = params.key,
                limit = params.loadSize
            )
            LoadResult.Page(
                data = response.items,
                prevKey = null,
                nextKey = response.nextCursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
```

---

### Pager configuration

```kotlin
Pager(
    config = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        enablePlaceholders = false
    ),
    pagingSourceFactory = { FeedPagingSource(api) }
)
```

---

## Paging 3 + Room (offline-first)

### Correct architecture

```
Network
↓
RemoteMediator
↓
Room
↓
PagingSource (Room)
↓
UI
```

### RemoteMediator skeleton

```kotlin
class FeedRemoteMediator(
    private val api: FeedApi,
    private val db: AppDatabase
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        return MediatorResult.Success(endOfPaginationReached = false)
    }
}
```

RemoteMediator decides:
- When to refresh
- How to merge pages
- When pagination ends

---

## Load states and UI reactions

```kotlin
when (loadState.refresh) {
    is LoadState.Loading -> showSpinner()
    is LoadState.Error -> showError()
    is LoadState.NotLoading -> showContent()
}
```

Load states are **UI events**, not data.

---

## Pagination failure modes (real bugs)

❌ Duplicate items after refresh
❌ Skipped items on insert
❌ Endless loading
❌ Incorrect end-of-list detection
❌ Cache mismatch

Almost always caused by:
- Wrong keys
- Wrong merge logic
- UI owning pagination state

---

## When NOT to paginate

- Small static lists
- Config data
- Local-only datasets

Pagination adds complexity. Don’t cargo-cult it.

---

## Senior rules

- Cursor-based pagination is the default
- Repository owns pagination state
- UI only reacts to load states
- Paging 3 is a tool, not a requirement
- Offline-first requires Room + RemoteMediator

---

## What this connects to

- Paging 3
- Offline-first strategies
- Error modeling
- API versioning
- Performance & memory management

Strong next topics:
- LoadState error recovery patterns
- Paging + MVI integration
- Scroll position restoration
- Memory pressure & large lists

