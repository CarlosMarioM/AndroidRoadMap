# AIDL Basics — Android IPC in Practice

## What AIDL actually is
**AIDL (Android Interface Definition Language)** is a **code generator on top of Binder**.

It:
- Generates **type-safe IPC stubs and proxies**
- Handles **parceling / unparceling**
- Does **not** change Binder behavior

Key truth:
> **AIDL is syntax sugar. Binder is the mechanism.**

---

## When AIDL exists (why it was created)

Binder is low-level and unsafe to use directly.

AIDL solves:
- Boilerplate IPC code
- Type safety across processes
- Versioned interfaces

Without AIDL, Binder IPC would be error-prone and fragile.

---

## When you SHOULD use AIDL

Use AIDL when:
- You need **cross-process communication**
- Calls are **frequent or performance-sensitive**
- You control both sides of the IPC
- You need **structured APIs**

Typical use cases:
- Media playback services
- System-style background engines
- Isolated processes

---

## When you SHOULD NOT use AIDL

Do NOT use AIDL for:
- Same-process communication
- Simple background work
- UI-to-ViewModel calls
- One-off tasks

AIDL adds complexity and maintenance cost.

---

## AIDL file basics

AIDL files define **interfaces**, not implementations.

Example:
```aidl
package com.example.ipc;

interface CalculatorService {
    int add(int a, int b);
}
```

Rules:
- Only supported types allowed
- No generics
- Explicit direction keywords (`in`, `out`, `inout`)

---

## Supported types (important limits)

AIDL supports:
- Primitives
- `String`
- `CharSequence`
- `List`, `Map` (with limits)
- `Parcelable`

Not supported:
- Arbitrary objects
- Lambdas
- Complex graphs

IPC is **data-oriented**, not object-oriented.

---

## Direction keywords

For non-primitive types:
- `in` → caller → callee
- `out` → callee → caller
- `inout` → both

Example:
```aidl
void process(in Data data);
```

Misusing these causes subtle bugs.

---

## Service-side implementation

The service implements the generated Stub:

```kotlin
class CalculatorService : Service() {

    private val binder = object : CalculatorServiceStub() {
        override fun add(a: Int, b: Int): Int = a + b
    }

    override fun onBind(intent: Intent): IBinder = binder
}
```

This code runs in the **service process**.

---

## Client-side usage

Binding to the service:

```kotlin
val service = CalculatorServiceStub.asInterface(binder)
val result = service.add(2, 3) // blocks
```

This is a **synchronous Binder call**.

---

## Threading model (critical)

- Incoming AIDL calls run on **Binder thread pool threads**
- NOT the main thread

Implications:
- You must manage synchronization
- You must not block Binder threads

Blocking here can freeze the system.

---

## One-way methods (async)

AIDL supports one-way calls:

```aidl
oneway void notifyEvent(String msg);
```

Characteristics:
- Fire-and-forget
- No return value
- No blocking

Still subject to Binder limits.

---

## Error handling

Remote calls can fail:
- `RemoteException`
- Process death

Always assume failure:

```kotlin
try {
    service.add(1, 2)
} catch (e: RemoteException) {
    // handle
}
```

IPC is not reliable.

---

## Versioning strategy (senior-level concern)

AIDL interfaces are **contracts**.

Rules:
- Never remove methods
- Only add new ones
- Maintain backward compatibility

Breaking AIDL breaks clients.

---

## Performance realities

AIDL calls:
- Are slower than in-process calls
- Involve context switching
- Are size-limited

Design APIs to be:
- Coarse-grained
- Efficient

---

## Common mistakes

❌ Calling AIDL on main thread
❌ Large payloads
❌ Blocking inside Stub
❌ Treating IPC as local calls

AIDL punishes sloppy design.

---

## Interview-grade summary

- AIDL is a **Binder abstraction**, not IPC itself
- Enables type-safe cross-process APIs
- Synchronous by default
- Strict size and threading limits
- Requires defensive coding

If you know AIDL, you understand **Android’s real IPC boundary**.

