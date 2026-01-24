# R8 / ProGuard Rules

This document explains **R8 and ProGuard in Android**, how they work, their differences, and how to properly configure rules to optimize APK/AAB size and maintain runtime correctness.

---

## What are R8 and ProGuard

- **ProGuard:** older tool for shrinking, optimizing, and obfuscating Java bytecode
- **R8:** new default shrinker, optimizer, and obfuscator for Android, replaces ProGuard
- Both tools:
  - Remove unused code and resources
  - Obfuscate class, method, and field names
  - Optimize bytecode

---

## Differences between R8 and ProGuard

| Feature                  | ProGuard         | R8                  |
|--------------------------|-----------------|-------------------|
| Default in Android Studio | No              | Yes (AGP >= 3.4)  |
| Shrinking & obfuscation  | Yes             | Yes + optimized   |
| Resource shrinking        | Partial         | Integrated        |
| Performance              | Slower          | Faster + incremental |

---

## Basic R8 / ProGuard setup

- `build.gradle` example:
```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```
- `minifyEnabled` → enables shrinking and obfuscation
- `shrinkResources` → removes unused resources
- `proguardFiles` → rules to preserve required classes/methods

---

## Common rule types

### 1. Keep rules

- Prevent R8 from removing classes or methods used via reflection or JNI

```proguard
# Keep all ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Room entities and DAO
-keep class com.example.app.data.** { *; }
```

### 2. Library-specific rules

- Many libraries require specific keep rules to avoid crashes
- Examples: Gson, Retrofit, Dagger/Hilt, Compose

```proguard
# Retrofit annotations
-keepattributes RuntimeVisibleAnnotations

# Gson model classes
-keep class com.example.models.** { *; }
```

### 3. Obfuscation control

- `-keepnames` → keep names for logging or reflection
- `-dontobfuscate` → disable obfuscation globally (rare)

### 4. Optimization rules

- `-optimizations !code/simplification/arithmetic` → sometimes needed to avoid issues

---

## Senior-level considerations

1. **Test thoroughly in release build**
   - Obfuscation can break reflection, annotations, or serialization

2. **Use library-provided rules**
   - Compose, Room, Retrofit, Gson often require specific keeps

3. **Incremental builds**
   - R8 supports incremental shrinking → faster CI builds

4. **Resource shrinking**
   - Enable for production to reduce APK/AAB size

5. **Debug vs release**
   - Keep `minifyEnabled false` in debug builds for easier debugging

---

## Compose-specific notes

- Keep composable functions and state holder classes if referenced by reflection
- Common issue: accidentally stripping `@Stable` or `@Composable` classes used indirectly

```proguard
-keep class androidx.compose.** { *; }
```

---

## Mental model

> R8/ProGuard = the gatekeeper of your final APK. They remove what’s not needed, but you must guide them to preserve runtime-critical classes.

---

## Interview takeaway

**Senior Android developers understand R8 rules deeply**: know what to keep, why, and how to test release builds to avoid reflection or library-related crashes.

