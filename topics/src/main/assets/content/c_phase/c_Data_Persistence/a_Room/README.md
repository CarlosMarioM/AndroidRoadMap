# Room — Entities, DAOs, and Transactions

This document explains **Room from the inside out**, focusing on how to model data correctly, how DAOs should be written, and how transactions actually work. This is not a CRUD tutorial — this is how to avoid corrupted state, broken migrations, and performance disasters.

---

## Mental model: what Room actually is

Room is:
- A **compile-time verified SQLite abstraction**
- A **code generator** (not an ORM like Hibernate)
- A thin layer over **raw SQLite semantics**

Room does **not**:
- Fix bad schemas
- Manage domain models
- Replace thinking about SQL

If you don’t understand SQLite concepts, Room will not save you.

---

# 1. Entities

Entities define **tables**, not domain models.

---

## Basic entity

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val createdAt: Long
)
```

Rules:
- One entity = one table
- Fields map directly to columns
- Keep entities **flat and boring**

---

## Primary keys

```kotlin
@PrimaryKey(autoGenerate = true)
val id: Long = 0
```

Guidelines:
- Use `autoGenerate` for local-only data
- Use stable IDs for synced data
- Never change primary keys after release

---

## Indices (performance-critical)

```kotlin
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
```

Use indices for:
- Lookup columns
- Foreign keys
- Sort-heavy queries

Missing indices = slow apps.

---

## Relationships (Room does NOT do joins automatically)

```kotlin
data class UserWithPosts(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val posts: List<PostEntity>
)
```

This is:
- A **query result model**
- Not a table
- Not persisted

Do not confuse relations with entities.

---

## What NOT to put in entities

Never put:
- Business logic
- UI state
- Calculated fields
- Nested domain objects

Entities are database rows. Nothing more.

---

# 2. DAOs

DAOs define **all database access**. If SQL leaks outside DAOs, the architecture is broken.

---

## Basic DAO

```kotlin
@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUser(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)
}
```

Rules:
- DAOs are **interfaces**
- Room generates implementations
- All DB access goes through DAOs

---

## Flow support (recommended)

```kotlin
@Query("SELECT * FROM users")
fun observeUsers(): Flow<List<UserEntity>>
```

Room:
- Re-runs the query on table changes
- Emits on a background thread
- Works naturally with Compose

---

## One DAO per aggregate

Bad:
- One DAO per table
- One giant DAO for everything

Correct:
- One DAO per **aggregate root**

DAOs model *use cases*, not tables.

---

## Query correctness

Room validates:
- SQL syntax
- Column existence
- Return type mapping

But Room does NOT validate:
- Query efficiency
- Index usage
- Logical correctness

You still need to think.

---

# 3. Transactions

Transactions guarantee **atomicity**. Either everything happens, or nothing happens.

---

## @Transaction annotation

```kotlin
@Transaction
suspend fun createUserWithPosts(
    user: UserEntity,
    posts: List<PostEntity>
) {
    insertUser(user)
    insertPosts(posts)
}
```

Rules:
- All calls run in a single DB transaction
- If one fails, all rollback
- Must be `suspend` or blocking

---

## Transactions with relations

```kotlin
@Transaction
@Query("SELECT * FROM users WHERE id = :id")
suspend fun getUserWithPosts(id: String): UserWithPosts?
```

Room ensures:
- Consistent snapshot
- No partial reads

---

## When transactions are REQUIRED

You must use transactions when:
- Writing to multiple tables
- Reading related data
- Performing read-modify-write

Skipping transactions causes:
- Inconsistent UI state
- Data corruption
- Heisenbugs

---

## Transactions and threading

Facts:
- Room runs suspend queries on IO threads
- Transactions block the DB connection
- Long transactions = ANRs

Rules:
- Keep transactions small
- No network calls inside transactions
- No heavy computation inside transactions

---

## Common Room mistakes (seen in production)

- Entities used as domain models
- Missing indices
- No transactions for multi-step writes
- One DAO per table
- Business logic inside DAOs

Room exposes bad data modeling fast.

---

## Rule of thumb

- Entities = tables
- DAOs = database API
- Transactions = safety net

If Room feels painful, the schema is probably wrong.

