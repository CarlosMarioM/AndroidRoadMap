# Hilt & Dagger — Fundamentals Without Magic

This document explains **what Dagger actually does**, how **Hilt sits on top of it**, and how to use both **correctly in real Android apps**. No annotations worship. No copy‑paste recipes. Dependency Injection is about **object ownership and lifetime**, not libraries.

---

## 1. What Dependency Injection Really Is

Dependency Injection (DI) is simply:

> **Objects do not create their dependencies. They receive them.**

That’s it.

Everything else (graphs, scopes, components) exists to **manage object creation and lifetime safely**.

If you understand ownership and lifetime, Dagger makes sense.

---

## 2. The Problem DI Solves

Without DI:
- Classes create their own dependencies
- Code is tightly coupled
- Testing requires hacks or reflection
- Lifetimes are implicit and buggy

Example of bad code:
```kotlin
class UserViewModel {
    private val repository = UserRepositoryImpl(Api())
}
```

Problems:
- Impossible to replace dependencies
- Impossible to test cleanly
- Lifetime is unclear

---

## 3. Constructor Injection (The Core Pattern)

The correct baseline:
```kotlin
class UserViewModel(
    private val repository: UserRepository
)
```

Now:
- Dependencies are explicit
- Easy to test
- Creation is delegated elsewhere

Dagger exists to automate **who creates this and when**.

---

## 4. Dagger Fundamentals (Before Hilt)

### Key Concepts

#### Component
- Object graph owner
- Knows how to create dependencies

#### Module
- Tells Dagger *how* to create objects

#### Binding
- Mapping from interface → implementation

#### Scope
- Controls lifetime of instances

Dagger is a **compile‑time graph generator**. No reflection. No runtime magic.

---

## 5. Binding Interfaces

```kotlin
interface UserRepository
```

```kotlin
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository
```

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
```

Rule:
- Interfaces live in domain
- Implementations live in data

---

## 6. Scopes (This Is the Hard Part)

Scopes define **how long an object lives**, not where it’s used.

Common scopes:
- `@Singleton` → app lifetime
- `@ActivityScoped` → activity lifetime
- `@ViewModelScoped` → ViewModel lifetime

Example:
```kotlin
@Singleton
class Api @Inject constructor()
```

Wrong scoping causes:
- Memory leaks
- Duplicate objects
- Subtle bugs

---

## 7. What Hilt Actually Does

Hilt is **Dagger with opinionated defaults**.

It:
- Predefines components
- Wires Android lifecycles
- Removes boilerplate

You are still using Dagger.

---

## 8. Hilt Component Hierarchy

```
SingletonComponent
   ↓
ActivityRetainedComponent
   ↓
ViewModelComponent
   ↓
ActivityComponent
   ↓
FragmentComponent
   ↓
ViewComponent
```

Each child can access parent dependencies, never the reverse.

---

## 9. Enabling Hilt

```kotlin
@HiltAndroidApp
class App : Application()
```

This creates the root component.

---

## 10. Injecting ViewModels

```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()
```

Compose:
```kotlin
val viewModel: UserViewModel = hiltViewModel()
```

No factories. No manual wiring.

---

## 11. Providing Objects with @Provides

Use `@Provides` when:
- You don’t own the class
- Construction requires logic

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApi(): UserApi = UserApi()
}
```

Prefer `@Inject` constructors when possible.

---

## 12. Hilt + Clean Architecture

Correct alignment:
- Domain → no DI annotations
- Data → DI bindings
- Presentation → injection targets

If your domain knows Hilt, you broke the boundary.

---

## 13. Common DI Mistakes

### ❌ Injecting Context everywhere
### ❌ Using @Singleton as default
### ❌ Injecting ViewModels into other ViewModels
### ❌ Depending on implementations instead of interfaces
### ❌ Creating objects manually “just this once”

DI inconsistencies rot fast.

---

## 14. Testing with Hilt

Replace modules:
```kotlin
@UninstallModules(NetworkModule::class)
class UserViewModelTest
```

Provide fakes:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object FakeModule {
    @Provides fun provideRepo(): UserRepository = FakeRepo()
}
```

If DI is clean, testing is easy.

---

## Final Verdict

Dagger is about **object graphs and lifetime control**.

Hilt removes friction, not responsibility.

If you don’t understand scopes and ownership, Hilt will hide bugs — not prevent them.

Learn Dagger fundamentals first. Hilt just makes them easier to apply.


---

## Practical Implementation — Step by Step (Hilt in a Real App)

This section shows **exactly how to wire Hilt**, end‑to‑end, without hand‑waving.

### 1. Gradle setup

```kotlin
// root build.gradle
buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
    }
}
```

```kotlin
// module build.gradle
plugins {
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
}
```

If this is missing, **nothing works**. No runtime fallback.

---

### 2. Application entry point

```kotlin
@HiltAndroidApp
class MyApp : Application()
```

This generates the **root component**. Without it, the graph does not exist.

---

### 3. Defining injectable classes

Constructor injection is the default. Prefer it.

```kotlin
class UserRepository @Inject constructor(
    private val api: UserApi
)
```

If you can do constructor injection, **do not use modules**.

---

### 4. Providing interfaces and third‑party objects

Use `@Binds` for interfaces:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
```

Use `@Provides` for things you don’t own:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()
}
```

Rule:
- `@Binds` → cheaper, compile‑time safe
- `@Provides` → last resort

---

### 5. Scoping correctly

```kotlin
@Singleton
class SessionManager @Inject constructor()
```

Scopes **define lifetime**, not caching.

Wrong scope = memory leaks or recreated objects.

Common scopes:
- `@Singleton` → app lifetime
- `@ViewModelScoped` → same ViewModel instance
- `@ActivityRetainedScoped` → survives config changes

---

### 6. ViewModel injection

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel()
```

Used from Compose:

```kotlin
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
}
```

The ViewModel is **not recreated on recomposition**.

---

### 7. Injecting into Activities / Fragments

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager
}
```

This just requests dependencies. **It does not create them.**

---

### 8. Assisted injection (runtime params)

When values are only known at runtime:

```kotlin
class DetailViewModel @AssistedInject constructor(
    @Assisted private val itemId: String,
    private val repo: Repo
)
```

```kotlin
@AssistedFactory
interface DetailViewModelFactory {
    fun create(itemId: String): DetailViewModel
}
```

This is mandatory for navigation arguments.

---

### 9. Testing: replacing bindings

```kotlin
@HiltAndroidTest
@UninstallModules(NetworkModule::class)
class ProfileViewModelTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeNetworkModule {
        @Provides fun provideApi(): UserApi = FakeApi()
    }
}
```

No service locators. No reflection hacks.

---

### 10. Non‑negotiable rules

- DI graph must be **acyclic**
- Constructor injection first
- Scope intentionally
- Never inject `Context` blindly
- Never inject Composables

If Hilt feels "hard", the problem is usually **bad architecture**, not DI.

