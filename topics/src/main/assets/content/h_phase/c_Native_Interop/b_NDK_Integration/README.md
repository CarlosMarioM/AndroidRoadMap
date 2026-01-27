# Android NDK Integration (Gradle + CMake)

> JNI without proper NDK integration is just wishful thinking.
> This document explains **how the Android NDK is actually wired into a Gradle project**, how CMake fits in, and how to avoid the classic traps that waste days.

This is the missing link between *"I wrote native code"* and *"it actually builds, loads, and runs"*.

---

## What the NDK Really Is

The Android NDK is:
- A **toolchain** to compile C/C++ into Android-compatible `.so` libraries
- A **bridge** between Gradle and native build systems

The NDK is NOT:
- A runtime
- A magic performance switch
- Optional once JNI is involved

No NDK → no native code on Android.

---

## Native Build Options (Pick One)

Android supports **two** native build systems:

### 1. CMake (Recommended)
- Actively supported
- Well-documented
- Plays nicely with Gradle

### 2. ndk-build (Makefiles)
- Legacy
- Harder to maintain
- Avoid unless you inherit it

If you’re starting fresh: **use CMake**.

---

## Minimal Project Structure

```text
app
└── src/main
    ├── cpp
    │   ├── native-lib.cpp
    │   └── CMakeLists.txt
    └── AndroidManifest.xml
```

Gradle will:
- Invoke CMake
- Compile `.so` per ABI
- Package them into the APK/AAB

---

## Enabling NDK in Gradle

### Module build.gradle.kts

```kotlin
android {
    defaultConfig {
        minSdk = 24

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}
```

This is the **minimum viable wiring**.

---

## CMakeLists.txt (The Real Build File)

```cmake
cmake_minimum_required(VERSION 3.22.1)

project(native_lib)

add_library(
    native-lib
    SHARED
    native-lib.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    native-lib
    ${log-lib}
)
```

Gradle doesn’t compile C++.
CMake does.

---

## Library Naming Rules (Critical)

If you load:
```kotlin
System.loadLibrary("native-lib")
```

Then CMake must produce:
```text
libnative-lib.so
```

Mismatch = `UnsatisfiedLinkError` at runtime.

---

## ABI Handling (Do NOT ignore this)

Android devices use different CPU architectures.

Common ABIs:
- `arm64-v8a` (modern default)
- `armeabi-v7a` (older)
- `x86_64` (emulators)

Rule:
- Support **arm64-v8a** minimum
- Add x86_64 if you care about emulators

More ABIs = bigger APK.

---

## Debug vs Release Builds

Gradle automatically builds:
- Debug `.so` with symbols
- Release `.so` optimized and stripped

To debug native crashes:
- Use **debuggable build**
- Keep symbols

```kotlin
ndk {
    debugSymbolLevel = "FULL"
}
```

Without symbols, native stacktraces are useless.

---

## Logging from Native Code

```cpp
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "NDK", __VA_ARGS__)

LOGI("Native code loaded");
```

If you’re not logging, you’re blind.

---

## Gradle Sync & Common Failures

### ❌ CMake not found

Fix:
- Install **CMake** and **NDK** via Android Studio SDK Manager

---

### ❌ ABI missing at runtime

Cause:
- APK doesn’t include the device ABI

Fix:
- Check `abiFilters`
- Inspect APK contents

---

### ❌ Undefined symbols

Cause:
- Missing source files
- Missing libraries

Fix:
- Fix `CMakeLists.txt`
- Don’t blame Gradle

---

## Multi-module + NDK

Yes, native code can live in library modules.

Rules:
- One `.so` per module
- Clear ownership
- Avoid circular native dependencies

Native spaghetti is worse than Java spaghetti.

---

## Performance Reality Check

NDK does not guarantee speed.

You only win if:
- Native code does heavy work
- JNI calls are coarse-grained
- Memory is handled correctly

Bad NDK code is slower than Kotlin.

---

## When NOT to Use the NDK

Don’t integrate NDK if:
- You only need portability
- You only want "speed"
- You don’t control crashes

NDK increases:
- Build complexity
- Debug difficulty
- Maintenance cost

Make it earn its place.

---

## Final Verdict

NDK integration is plumbing.

Do it once.
Do it clean.

If native code becomes painful, it’s not Android’s fault — it’s your build setup.
