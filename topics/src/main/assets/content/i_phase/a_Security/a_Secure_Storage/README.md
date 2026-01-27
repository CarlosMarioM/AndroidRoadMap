# Secure Storage on Android (Keystore, Encrypted Storage, Native Considerations)

> Secure storage is not about hiding strings — it’s about **threat models**.
> This document explains **what Android can actually protect**, **what it cannot**, and **how to store sensitive data correctly** without security theater.

If your strategy is "obfuscate and hope", you are already compromised.

---

## First: Define the Threat Model (Mandatory)

Before choosing any storage:

Ask:
- Who is the attacker? (user, malware, rooted device, physical access)
- What are you protecting? (tokens, keys, PII)
- How long must it be protected?

No threat model → fake security.

---

## What Android Can and Cannot Protect

### Android CAN:
- Protect keys using hardware-backed keystore
- Prevent key extraction (even on rooted devices, usually)
- Encrypt data at rest

### Android CANNOT:
- Protect secrets once your app is fully compromised
- Hide data from a determined attacker with code execution
- Save you from bad architecture

Security is about **raising cost**, not perfection.

---

## The Android Keystore (The Foundation)

The Keystore:
- Stores **cryptographic keys**, not raw secrets
- Can be hardware-backed (TEE / StrongBox)
- Prevents key material export

You encrypt data **with keys**, not store data inside it.

---

## Generating a Secure Key (AES)

```kotlin
val keyGenerator = KeyGenerator.getInstance(
    KeyProperties.KEY_ALGORITHM_AES,
    "AndroidKeyStore"
)

val keyGenParameterSpec = KeyGenParameterSpec.Builder(
    "secure_key",
    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
)
    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
    .setUserAuthenticationRequired(false)
    .build()

keyGenerator.init(keyGenParameterSpec)
keyGenerator.generateKey()
```

Keys never leave the keystore.

---

## Encrypting Data Manually (Correct Way)

```kotlin
val cipher = Cipher.getInstance("AES/GCM/NoPadding")
cipher.init(Cipher.ENCRYPT_MODE, secretKey)

val iv = cipher.iv
val encrypted = cipher.doFinal(data)
```

You must store:
- Encrypted data
- IV

Lose the IV → data is gone.

---

## EncryptedSharedPreferences (Good Defaults)

For most apps, this is enough.

```kotlin
val prefs = EncryptedSharedPreferences.create(
    context,
    "secure_prefs",
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

Pros:
- Easy
- Correct by default

Cons:
- Not designed for large data

---

## Encrypted Files (Large Data)

```kotlin
val encryptedFile = EncryptedFile.Builder(
    File(context.filesDir, "secret.bin"),
    context,
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
).build()
```

Use this for:
- Cached credentials
- Local databases

---

## What NOT to Do (Ever)

### ❌ Store secrets in SharedPreferences
### ❌ Hardcode API keys
### ❌ Roll your own crypto
### ❌ Base64 ≠ encryption

These are rookie mistakes.

---

## Secure Storage + Native Code (JNI / NDK)

Important rule:

> **Never store secrets directly in C/C++**

Why:
- Native memory can be dumped
- No lifecycle protection
- Harder to wipe safely

Correct pattern:
```text
Keystore → Key
Kotlin → Encryption
Native → Uses decrypted data transiently
```

Native code should process data, not own secrets.

---

## Token Storage Strategy (Real Apps)

Best practice:
- Short-lived access tokens
- Refresh tokens encrypted
- Server-side revocation

Never trust local storage alone.

---

## Biometrics & User Authentication

Keys can be gated by biometrics:

```kotlin
.setUserAuthenticationRequired(true)
.setUserAuthenticationParameters(
    30,
    KeyProperties.AUTH_BIOMETRIC_STRONG
)
```

This protects against background extraction.

---

## Rooted Devices (Hard Truth)

On rooted devices:
- Memory inspection is possible
- Hooking frameworks exist

Keystore still helps, but:
- Assume eventual compromise
- Limit blast radius

Security is layers, not absolutes.

---

## Common Anti-patterns

### ❌ "It’s in native code so it’s safe"
False.

### ❌ "Obfuscation is security"
False.

### ❌ "No one will look"
They will.

---

## Final Verdict

Secure storage on Android is:
- Keystore-backed keys
- Strong encryption
- Minimal exposure

If your app handles real secrets:
- Design for compromise
- Reduce damage
- Rotate aggressively

Security is not hiding — it’s engineering.
