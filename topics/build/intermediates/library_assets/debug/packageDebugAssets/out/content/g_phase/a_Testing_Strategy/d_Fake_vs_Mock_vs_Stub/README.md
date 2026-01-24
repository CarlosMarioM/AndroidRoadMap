# Fake vs Mock vs Stub in Android Testing

This document explains the **difference between fakes, mocks, and stubs**, when to use each one, and how senior Android developers choose the right test double.

This topic is critical for **unit tests, integration tests, and test architecture quality**.

---

## What are test doubles

A **test double** is any object that replaces a real dependency in tests.

Common types:
- Stub
- Mock
- Fake

They are **not interchangeable**. Using the wrong one leads to brittle or meaningless tests.

---

## Stub

### What it is

A **stub** is a simple object that:
- Returns **predefined data**
- Does **not contain logic**
- Does **not verify interactions**

It exists only to satisfy a dependency.

### Example

```kotlin
class UserStubRepository : UserRepository {
    override suspend fun getUser(id: Int): User {
        return User(id, "Mario")
    }
}
```

### When to use stubs

- Testing ViewModels
- Testing simple business logic
- You only care about **output**, not behavior

### When NOT to use

- When behavior varies by input
- When state or persistence matters

---

## Mock

### What it is

A **mock**:
- Verifies **interactions** (calls, order, parameters)
- Usually created via a mocking framework
- Can fail tests if expectations are not met

### Example (MockK)

```kotlin
val repository = mockk<UserRepository>()
coEvery { repository.getUser(1) } returns User(1, "Mario")

viewModel.loadUser(1)

coVerify { repository.getUser(1) }
```

### When to use mocks

- Verifying **side effects**
- Ensuring a dependency is called correctly
- Testing orchestration logic

### When NOT to use

- Core business logic
- Data transformation logic
- Overuse leads to fragile tests

---

## Fake

### What it is

A **fake** is a **working implementation** that:
- Contains real logic
- Is simplified compared to production
- Often uses in-memory data

### Example

```kotlin
class FakeUserRepository : UserRepository {
    private val users = mutableMapOf<Int, User>()

    override suspend fun getUser(id: Int): User? = users[id]

    override suspend fun insertUser(user: User) {
        users[user.id] = user
    }
}
```

### When to use fakes

- Integration tests
- Repository tests
- ViewModel + data flow tests

### When NOT to use

- Very small unit tests
- When setup cost outweighs benefit

---

## Side-by-side comparison

| Type  | Logic | State | Verifies calls | Best for |
|------|------|-------|----------------|----------|
| Stub | ❌   | ❌    | ❌             | Simple unit tests |
| Mock | ❌   | ❌    | ✅             | Interaction testing |
| Fake | ✅   | ✅    | ❌             | Integration tests |

---

## Senior-level rules of thumb

1. Prefer **fakes over mocks** for anything involving state
2. Prefer **stubs over mocks** when verifying behavior is irrelevant
3. Use mocks **only at boundaries**
4. Avoid mocking data models or collections
5. If a test breaks after refactoring internals, you over-mocked

---

## Common mistakes

- Mocking repositories instead of using fakes
- Verifying implementation details instead of outcomes
- Mixing mocks and fakes unnecessarily
- Using mocks as stubs (bad smell)

---

## Mental model

> Stub = canned answers
> Mock = spy watching calls
> Fake = simplified real system

---

## Interview takeaway

**Senior Android developers choose the right test double deliberately**, understanding the tradeoffs between stubs, mocks, and fakes to write stable, meaningful tests.

