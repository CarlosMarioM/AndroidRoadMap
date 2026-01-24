# Resource Shrinking in Android

This document explains **resource shrinking in Android**, how it works, its benefits, pitfalls, and best practices for senior developers.

Resource shrinking is a **technique to reduce APK/AAB size** by removing unused resources.

---

## What is resource shrinking

- Enabled via `shrinkResources true` in Gradle
- Works in conjunction with `minifyEnabled true` (R8/ProGuard)
- Removes **drawables, layouts, strings, and other resources** not referenced by code
- Only removes resources **not reachable** from code, XML, or manifest

---

## Enabling resource shrinking

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

- `shrinkResources` only works when `minifyEnabled` is true
- Works at **build time** to create smaller APKs/AABs

---

## How it works

1. R8/ProGuard removes unused code and classes
2. Resource shrinker analyzes **resource usage graph**
3. Unreferenced resources are **eliminated** from APK/AAB
4. Optimizes final package size

### Example

- App has 100 drawable assets
- Only 70 are referenced in code or XML layouts
- Resource shrinker removes 30 unused drawables automatically

---

## Common pitfalls

1. **Dynamic resource access**
   ```kotlin
   val id = resources.getIdentifier(name, "drawable", packageName)
   imageView.setImageResource(id)
   ```
   - Resource shrinker may remove dynamically referenced resources
   - Solution: add `keep` rules in `res/raw/proguard-rules.pro`

2. **Custom views or layouts loaded via reflection**
   - May reference resources not detectable by shrinker
   - Solution: explicitly keep them

3. **Library resources**
   - Ensure libraries declare proper references or keep rules
   - Otherwise, unused library resources may be stripped incorrectly

---

## Senior-level best practices

1. Always **enable resource shrinking for release builds**
2. Combine with **ProGuard/R8** to maximize savings
3. **Test thoroughly**: check dynamic resources and reflection usage
4. **Use `tools:keep` or ProGuard keep rules** to prevent removing required resources
   ```xml
   <resources xmlns:tools="http://schemas.android.com/tools" tools:keep="@drawable/icon_important" />
   ```
5. Monitor **APK/AAB size differences** with and without shrinking

---

## Compose-specific notes

- Compose uses `R` references less directly, but resources still matter
- Ensure any dynamically loaded drawable or string used in Compose is **kept**
- Example for images in Compose:
   ```kotlin
   Image(painter = painterResource(id = R.drawable.dynamic_icon), contentDescription = null)
   ```
   - `R.drawable.dynamic_icon` must not be stripped

---

## Mental model

> Resource shrinking = automatic cleanup of unused resources.

Think of it as **garbage collection for images, strings, and layouts** that your app doesn’t use.

---

## Interview takeaway

**Senior Android developers always enable resource shrinking in release builds**, understand dynamic resource pitfalls, and combine it with R8/ProGuard to optimize APK/AAB size without breaking functionality.

