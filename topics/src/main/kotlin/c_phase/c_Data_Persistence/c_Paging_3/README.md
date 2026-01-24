# Paging 3 — Complete Guide (Concepts, Internals, and Implementation)

This section explains **what Paging 3 is**, **how it actually works**, **when to use it**, and **how to implement it correctly** in real projects. No magic, no UI tricks — just data flow.

---

## 1. What problem Paging 3 solves (and what it does NOT)

Paging 3 solves **efficient incremental loading of large datasets**.

It handles:
- Loading data in chunks
- Prefetching next data automatically
- UI updates without manual diffing
- Error & retry handling
- Invalidation when data changes

It does **NOT**:
- Replace architecture
- Simplify bad APIs
- Make small lists faster
- Eliminate the need for proper state handling

If your list is small → **don’t use Paging**.

---

## 2. Paging 3 mental model (mandatory)

Paging is a **reactive pipeline**:

```
Data source → PagingSource → Pager → Flow<PagingData<T>> → UI
```

Key rules:
- The UI never requests “page 1, page 2”
- The UI only reacts to **streams of items**
- PagingSource is **stateless and disposable**

---

## 3. PagingSource (core abstraction)

`PagingSource<Key, Value>` defines **how to load a single page**.

### Responsibilities
- Load data for a given key
- Provide previous/next keys
- Report errors

### Forbidden responsibilities
- Caching
- Holding mutable state
- UI logic
- Thread management

### Example: page-based REST API

```kotlin
class UsersPagingSource(
    private val api: UsersApi
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val page = params.key ?: 1
            val response = api.getUsers(page, params.loadSize)

            LoadResult.Page(
                data = response.users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.users.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }
}
```

### Important
> PagingSource instances are **recreated frequently**. Treat them as disposable.

---

## 4. Pager (paging coordinator)

`Pager`:
- Creates PagingSource instances
- Controls page size and prefetch
- Emits `Flow<PagingData<T>>`

```kotlin
fun usersPager(api: UsersApi): Pager<Int, User> = Pager(
    config = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        enablePlaceholders = false
    ),
    pagingSourceFactory = { UsersPagingSource(api) }
)
```

### PagingConfig rules
- `pageSize` → backend page size
- `prefetchDistance` → when next load starts
- `placeholders` → almost always `false`

---

## 5. ViewModel integration (correct scope)

Paging always lives in the **ViewModel**.

```kotlin
@HiltViewModel
class UsersViewModel @Inject constructor(
    api: UsersApi
) : ViewModel() {

    val users: Flow<PagingData<User>> = usersPager(api)
        .flow
        .cachedIn(viewModelScope)
}
```

### Why `cachedIn` matters
- Prevents reload on rotation
- Shares paging state
- Keeps scroll position stable

No `cachedIn` = unnecessary reloads.

---

## 6. UI integration (Compose)

```kotlin
@Composable
fun UsersScreen(viewModel: UsersViewModel) {
    val users = viewModel.users.collectAsLazyPagingItems()

    LazyColumn {
        items(users) { user ->
            user?.let { UserRow(it) }
        }

        users.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingView() }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingMoreView() }
                }
                loadState.refresh is LoadState.Error -> {
                    item { ErrorView("Failed to load") }
                }
            }
        }
    }
}
```

### Rules
- Items may be `null`
- LoadState handling is mandatory
- UI must be resilient to partial data

---

## 7. Paging + Room (database as source of truth)

Recommended architecture:

```
Network → Room → PagingSource(Room) → UI
```

### DAO

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY id")
    fun pagingSource(): PagingSource<Int, UserEntity>()
}
```

### Pager

```kotlin
Pager(
    config = PagingConfig(pageSize = 20),
    pagingSourceFactory = { userDao.pagingSource() }
)
```

Room automatically invalidates PagingSource when data changes.

---

## 8. RemoteMediator (true offline-first)

Use `RemoteMediator` when:
- You want cached data
- Network + database must stay in sync
- Refresh should repopulate DB

```kotlin
@OptIn(ExperimentalPagingApi::class)
class UsersRemoteMediator(
    private val api: UsersApi,
    private val db: AppDatabase
) : RemoteMediator<Int, UserEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        return try {
            // Decide page
            // Fetch network
            // Save in DB transaction
            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
```

This is **advanced**. Don’t use unless necessary.

---

## 9. LoadState (production requirement)

Three phases:
- `refresh` → initial load
- `append` → loading more
- `prepend` → rarely used

Good apps:
- Show loading indicators
- Show retry actions
- Preserve scroll position

---

## 10. Common mistakes

- Using Paging for small lists
- Business logic inside PagingSource
- Missing `cachedIn`
- Manual list mutation
- Ignoring LoadState
- Overusing RemoteMediator

---

## 11. When Paging helps / hurts

### Helps
- Infinite feeds
- Large DB queries
- Offline-first apps
- Expensive queries

### Hurts
- Static lists
- Small datasets
- Simple screens
- Tight deadlines

---

## Final rule

Paging 3 is powerful **only when the problem is real**.

If pagination is fake → Paging is overkill.
If data is large → Paging is mandatory.

