# Android Senior Developer Roadmap

This roadmap defines **practical, production-level Android seniority**.  
It is ordered from fundamentals to system-level mastery and real-world ownership.

Covering this end to end means you can **design, build, debug, scale, and maintain Android applications in production**.

---

## PHASE 1 — Core Foundations (Non-Negotiable)

### Kotlin Language Mastery
- Kotlin syntax and idioms
- Null safety and platform types
- Data classes vs sealed classes vs value classes
- Sealed interfaces
- Inline functions and reified generics
- Lambdas and higher-order functions
- Collections and immutability
- Operator overloading (use cases and risks)

### Coroutines Fundamentals
- `suspend` functions
- Coroutine scopes and lifecycle awareness
- Dispatchers (`Main`, `IO`, `Default`)
- Structured concurrency
- Cancellation and cooperative cancellation
- Exception propagation in coroutines

### Android Core Components
- Activities (role, lifecycle, task stack)
- Fragments (why they exist, when to avoid them)
- Services:
    - Started services
    - Bound services
    - Foreground services
- BroadcastReceivers (static vs dynamic)
- ContentProviders (why they exist, real use cases)

### Android Manifest & App Configuration
- Permissions (normal vs dangerous)
- Runtime permissions flow
- Intent filters
- Exported components
- Launch modes and task behavior

### Lifecycle & State Management
- Activity lifecycle (cold start → background → kill)
- Fragment lifecycle vs View lifecycle
- Configuration changes
- Process death vs recreation
- SavedInstanceState
- SavedStateHandle

---

## PHASE 2 — UI Systems (Views and Compose)

### View System (Required Knowledge)
- View hierarchy
- Measure / Layout / Draw phases
- ConstraintLayout
- RecyclerView internals
- ViewHolder pattern
- DiffUtil and ListAdapter
- Custom Views:
    - `onMeasure`
    - `onLayout`
    - `onDraw`

### Jetpack Compose Fundamentals
- Composables and composition
- State and state hoisting
- Recomposition rules
- Side effects (`LaunchedEffect`, `DisposableEffect`)
- Remember vs derivedStateOf
- CompositionLocal

### Compose Advanced Topics
- Performance pitfalls and recomposition control
- Stable vs unstable parameters
- Custom layouts (measure/layout in Compose)
- Animations
- Compose Navigation
- Interop:
    - Compose in View-based apps
    - Views inside Compose

### Accessibility & Internationalization
- TalkBack support
- Content descriptions
- Focus handling
- Localization and resource qualifiers
- RTL support

---

## PHASE 3 — Architecture & Data Layer

### Architecture Patterns
- MVVM (proper responsibilities)
- MVI
- Clean Architecture (when it helps, when it doesn’t)
- Unidirectional data flow
- State vs events

### Dependency Injection
- Hilt (Dagger fundamentals)
- Component scopes
- Custom scopes
- Assisted injection
- When DI becomes harmful

### Data Persistence
- Room:
    - Entities
    - DAOs
    - Transactions
- DataStore (Preferences vs Proto)
- Paging 3
- Offline-first strategies
- Data synchronization patterns

### Networking
- Retrofit internals
- OkHttp:
    - Interceptors
    - Caching
    - Retry strategies
- Error modeling
- API versioning
- Pagination and partial loading
- Authentication and token refresh

---

## PHASE 4 — Concurrency & Background Work

### Advanced Coroutines & Flow
- Cold vs hot flows
- StateFlow vs SharedFlow
- Backpressure
- Flow operators
- Error handling strategies
- Thread confinement

### Background Execution
- WorkManager (constraints, chaining)
- AlarmManager (limitations)
- JobScheduler
- Foreground service limitations
- Background execution limits (modern Android)

---

## PHASE 5 — Android OS & System Knowledge (Senior Requirement)

### Android Runtime & Processes
- Zygote process
- App process lifecycle
- Isolated processes
- ART vs Dalvik
- JIT vs AOT compilation

### IPC & System Communication
- Binder fundamentals
- AIDL basics
- When IPC matters

### Memory & Performance
- Java/Kotlin heap
- Native heap
- Stack vs heap
- Garbage collection behavior
- Memory leaks and reference chains

### ANRs & System Constraints
- Input dispatch timeout
- Broadcast timeout
- Service timeout
- Power management:
    - Doze
    - App Standby Buckets
- Background restrictions across Android versions

---

## PHASE 6 — Performance, Debugging & Stability

### Performance Optimization
- Startup optimization (cold/warm/hot)
- Frame rendering and jank
- Layout overdraw
- Threading issues
- Network and disk bottlenecks

### Debugging & Profiling
- Android Studio Profiler
- LeakCanary (deep usage)
- StrictMode
- Systrace / Perfetto
- Logcat analysis at scale

### App Size & Build Optimization
- APK vs AAB
- R8 / ProGuard rules
- Resource shrinking
- ABI splits

---

## PHASE 7 — Testing & Quality

### Testing Strategy
- Unit tests (ViewModels, business logic)
- Integration tests
- UI tests:
    - Espresso
    - Compose testing
- Fake vs mock vs stub
- Flaky test mitigation

### CI/CD
- Gradle task optimization
- Test automation
- Build variants for CI
- Release pipelines

---

## PHASE 8 — Build System & Tooling

### Gradle Advanced
- Build lifecycle
- Custom tasks
- Build caching
- Build scans
- Plugin development basics

### Kotlin DSL
- Migration from Groovy
- Type-safe accessors
- Multi-module configuration

### Native Interop
- JNI basics
- NDK integration
- C/C++ performance use cases

---

## PHASE 9 — Security & Production Readiness

### Security
- Secure storage
- Encryption
- Certificate pinning
- BiometricPrompt
- Protecting secrets

### App Distribution
- Play Console workflows
- Signing configs
- Rollouts and staged releases
- Crash monitoring
- Production incident handling

---

## PHASE 10 — Ownership Skills (Often Ignored)

- Technical decision-making
- Trade-off analysis
- Codebase evolution and refactoring
- Legacy system migration
- Mentoring developers
- Writing technical documentation
- Owning features from design to production

---

## Final Note

Senior Android development is **not about APIs**.  
It is about **understanding the platform, owning production issues, and making correct trade-offs under constraints**.
