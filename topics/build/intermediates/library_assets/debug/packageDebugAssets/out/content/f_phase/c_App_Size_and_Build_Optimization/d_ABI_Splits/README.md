# ABI Splits in Android

This document explains **ABI (Application Binary Interface) splits**, how to configure them, their benefits, pitfalls, and best practices for senior Android developers.

ABI splits allow distributing device-specific binaries (native libraries) to **reduce APK/AAB size**.

---

## What are ABI splits

- Android devices use different CPU architectures: `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`.
- Native libraries (C/C++ via NDK or FFI) must match device ABI.
- ABI splits create separate APKs per architecture to avoid shipping unused binaries.

---

## Enabling ABI splits in Gradle

```gradle
android {
    splits {
        abi {
            enable true
            reset() // clear previous configs
            include 'armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64'
            universalApk false // optional: generate universal APK
        }
    }
}
```

- `enable true` → activate ABI splits
- `include` → specify supported ABIs
- `universalApk false` → avoid creating a single APK with all ABIs

---

## How it works

1. Gradle generates **separate APKs per ABI**
2. Each APK includes only the **native libraries** for that ABI
3. Reduces download size for end users
4. Google Play serves the correct APK automatically when using **AAB + Dynamic Delivery**

---

## Benefits

- Smaller download and install size
- Faster app updates
- Optimized for device architecture

---

## Pitfalls

1. **Testing**
   - Need to test on all ABIs you support
   - Some native code may behave differently per architecture

2. **CI/CD complexity**
   - Multiple APKs generated per build
   - Must ensure proper signing and upload for each split

3. **Third-party libraries**
   - Ensure included `.so` files support all targeted ABIs

---

## Senior-level best practices

1. Always **enable ABI splits for release builds** with AAB
2. Generate a **universal APK** only for internal testing if needed
3. Combine with **resource and density splits** for maximum optimization
4. Verify **native library coverage** across architectures
5. Monitor **APK/AAB size reductions** per split

---

## Compose and ABI considerations

- Pure Compose code does not generate native binaries
- ABI splits mainly matter if using **NDK, FFI, or third-party native libraries**
- Ensure Compose modules interact correctly with ABI-specific native components

---

## Mental model

> ABI splits = delivering only the native binaries a device needs, like giving each user a tailored toolkit.

---

## Interview takeaway

**Senior Android developers leverage ABI splits to reduce app size, optimize delivery, and ensure native library correctness per architecture.**

