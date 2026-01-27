# JNI Basics for Android (Kotlin / C / C++)

> JNI (Java Native Interface) is **not magic**, **not fast by default**, and **not something you use casually**.
> This document explains **what JNI really is**, **how it works**, and **how to use it correctly** in Android projects — without folklore or fear.

This is senior-level groundwork. If you plan to touch **NDK, FFI, emulators, DSP, crypto, or performance-critical code**, you need this.

---

## What JNI Actually Is (No Myths)

JNI is a **bridge** between:
- Managed world: **Kotlin / Java (ART, GC, objects)**
- Native world: **C / C++ (manual memory, raw pointers)**

JNI does NOT:
- Make code automatically faster
- Replace Kotlin/Java
- Protect you from crashes

JNI DOES:
- Allow calling native code from JVM
- Allow native code to call back into JVM
- Give you access to low-level APIs

One mistake = process crash. No stacktrace mercy.

---

## When JNI Is Actually Worth Using

Use JNI only if you need:
- Heavy computation (DSP, image processing, emulation)
- Existing C/C++ libraries
- Low-level OS or hardware access
- Deterministic performance (no GC pauses)

Do NOT use JNI for:
- Business logic
- Networking
- JSON parsing
- Anything you can do cleanly in Kotlin

JNI is a **scalpel**, not a hammer.

---

## High-level Architecture

```text
Kotlin / Java
   ↓ (JNI call)
JNI layer (glue code)
   ↓
C / C++ implementation
```

The JNI layer should be **thin**.
If your JNI files are big, you already messed up.

---

## Android NDK Basics

Android does NOT run C/C++ directly.
You compile native code into `.so` libraries using the **NDK**.

Typical structure:
```text
app
└── src/main
    ├── cpp
    │   ├── native-lib.cpp
    │   └── CMakeLists.txt
    └── java/com/example/nativebridge
```

Gradle loads `.so` files at runtime.

---

## Declaring a Native Function (Kotlin)

```kotlin
object NativeBridge {
    init {
        System.loadLibrary("native-lib")
    }

    external fun add(a: Int, b: Int): Int
}
```

Key points:
- `external` means implementation lives in native code
- Library name must match the compiled `.so`

---

## Implementing the Native Function (C++)

```cpp
#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_nativebridge_NativeBridge_add(
        JNIEnv* env,
        jobject /* this */,
        jint a,
        jint b
) {
    return a + b;
}
```

Yes, the name is ugly. That’s JNI.

---

## Naming Rules (Why People Hate JNI)

Function name pattern:
```text
Java_<package>_<class>_<method>
```

Dots → underscores
Nested classes → `_00024`

Mistype it → **UnsatisfiedLinkError at runtime**.

---

## Using JNI_OnLoad (IMPORTANT)

This runs when the library is loaded.

```cpp
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
```

Use this to:
- Cache class references
- Register native methods
- Fail fast if setup is wrong

---

## Registering Native Methods (Avoid name hell)

Better approach than long method names:

```cpp
static JNINativeMethod methods[] = {
    {"add", "(II)I", (void*) add}
};
```

Register them in `JNI_OnLoad`.
This makes refactoring survivable.

---

## Data Types Mapping (Critical)

| Kotlin / Java | JNI Type |
|--------------|----------|
| Int          | jint     |
| Long         | jlong    |
| Boolean      | jboolean |
| Float        | jfloat   |
| Double       | jdouble  |
| String       | jstring  |
| ByteArray   | jbyteArray |

Get this wrong → crash or corrupted memory.

---

## Strings (Common Footgun)

```cpp
const char* str = env->GetStringUTFChars(jStr, nullptr);
// use it
env->ReleaseStringUTFChars(jStr, str);
```

Forget `Release` → memory leak.
Do it twice → crash.

JNI is unforgiving.

---

## Local vs Global References

- Local refs → valid only during the call
- Global refs → must be manually freed

```cpp
jobject global = env->NewGlobalRef(obj);
// later
env->DeleteGlobalRef(global);
```

Leak globals → permanent memory leak.

---

## Threading Rules (DO NOT VIOLATE)

- JNI calls must happen on **attached threads**
- Native threads must attach to JVM manually

```cpp
vm->AttachCurrentThread(&env, nullptr);
// work
vm->DetachCurrentThread();
```

Wrong thread = instant crash.

---

## Performance Reality

JNI has overhead:
- Context switching
- Data copying

Rule:
- Fewer, larger calls
- Not many tiny calls

Batch work in native code.

---

## Common JNI Anti-patterns

### ❌ Business logic in C++
Harder to test. Harder to maintain.

### ❌ Chatty JNI calls
Performance death by a thousand cuts.

### ❌ Ignoring crashes
JNI crashes are **fatal**. Always use logs and symbols.

---

## JNI vs FFI (Context)

JNI:
- Low-level
- Verbose
- Maximum control

FFI (e.g. Dart FFI):
- Cleaner
- Less JVM involvement
- Still dangerous

JNI is still king on Android.

---

## Final Verdict

JNI is powerful, sharp, and dangerous.

If you need it:
- Keep the bridge thin
- Own memory explicitly
- Respect threading rules

If you don’t need it:
- Stay in Kotlin

JNI doesn’t forgive ignorance — it exposes it.

