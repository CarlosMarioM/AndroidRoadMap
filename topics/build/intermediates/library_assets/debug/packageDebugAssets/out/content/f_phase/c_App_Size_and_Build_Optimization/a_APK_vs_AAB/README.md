# APK vs AAB

This document explains the differences between **APK (Android Package)** and **AAB (Android App Bundle)**, their purposes, advantages, limitations, and considerations for senior Android developers.

---

## APK (Android Package)

- **Traditional format** for Android apps
- Contains **all code, resources, assets, and manifest** needed for the app
- Signed and ready to install on a device

### Characteristics

- Single file per app version
- Can be installed directly via `adb install` or Play Store
- Includes **all screen densities, languages, architectures** → larger size
- Direct debugging easier

### Pros

- Simple and straightforward
- Easy to distribute for testing (internal devices, beta)
- Full control over contents

### Cons

- Larger download size
- Includes resources not needed for all devices
- No dynamic delivery support

---

## AAB (Android App Bundle)

- **New publishing format** introduced by Google Play
- Contains **all code and resources** but allows Play Store to generate **optimized APKs per device**
- Supports **Dynamic Delivery** and **on-demand modules**

### Characteristics

- Not directly installable on a device
- Must be uploaded to **Google Play** to generate device-specific APKs
- Includes **multiple configurations** (languages, densities, ABIs)

### Pros

- Smaller APKs delivered to users → faster installs
- Supports **modularization and dynamic features**
- Reduces storage and download size
- Supports **play feature delivery and on-demand modules**

### Cons

- Cannot directly install on a device without bundletool
- Testing requires **bundletool** or internal test track
- Complexity in managing dynamic modules

---

## Senior-level considerations

1. **Testing**
   - Use `bundletool` to generate APKs from AAB for testing:
   ```bash
   bundletool build-apks --bundle=myapp.aab --output=myapp.apks --connected-device
   bundletool install-apks --apks=myapp.apks
   ```

2. **Dynamic Features**
   - Split features that are rarely used to reduce initial download size
   - Deliver them on-demand via Play Store

3. **App Size Optimization**
   - Leverage resource and ABI splits
   - Reduce unused languages and densities

4. **CI/CD**
   - Automate build of AABs
   - Integrate with Play Store internal testing

5. **Backward Compatibility**
   - Devices before Android 5.0 cannot install AABs directly
   - Still generate APKs for legacy support if needed

---

## Mental model

> APK = monolithic installable file. AAB = optimized, modular, Play Store-delivered package.

Think **AAB for production** and **APK for debugging and direct installs**.

---

## Interview takeaway

**Senior Android developers understand the trade-offs**: AAB for optimized delivery and modularization, APK for testing, quick installs, and simplicity.

