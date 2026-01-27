# Protecting Secrets on Android

Protecting secrets is **not about hiding strings**, it’s about **engineering boundaries**. Mistakes here lead to leaked tokens, stolen keys, and compromised users.

---

## 1. Understand Your Threat Model

Before any secret storage or handling:
- Who is the attacker? (malware, rooted device, reverse engineering)
- What are you protecting? (API keys, tokens, private keys)
- How long must the secret remain safe?

No threat model → false sense of security.

---

## 2. Use Keystore for Keys

- Keys never leave secure hardware (TEE / StrongBox)
- Can be gated by user authentication (PIN/biometric)
- Use for AES encryption or signing

Correct usage:
```kotlin
val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
// configure KeyGenParameterSpec
keyGenerator.generateKey()
```

Never store keys in code.

---

## 3. Encrypt Secrets

- Always encrypt sensitive data at rest
- Use AES-GCM with random IVs
- Store IVs separately from encrypted data
- Use Keystore-backed keys

Do **not** roll your own crypto or reuse IVs.

---

## 4. Secrets in Memory

- Minimize time secrets live in memory
- Overwrite buffers after use if in native code
- Avoid exposing secrets in logs or stack traces

Native code helps but must be careful — memory is not magically safe.

---

## 5. JNI / NDK Considerations

- Secrets can be decrypted in native code, but do not store long-term in native memory
- Use native code for performance, not security
- Always gate native access with Keystore keys

Rule: Keystore + Kotlin orchestration, C/C++ hot path only

---

## 6. Network Secrets

- Do not hardcode API keys
- Use ephemeral tokens whenever possible
- Combine with TLS and certificate pinning
- Refresh tokens periodically

Even encrypted secrets are useless if network calls are intercepted or replayed.

---

## 7. Biometrics for Secret Access

- Biometrics do **not** protect data themselves
- Gate access to Keystore keys only
- Use proper fallback (device credential) for reliability

Example:
```kotlin
.setUserAuthenticationRequired(true)
.setUserAuthenticationValidityDurationSeconds(30)
```

---

## 8. Anti-Patterns to Avoid

- Hardcoding secrets in APK or native libraries
- Using obfuscation as a substitute for encryption
- Leaving secrets in logs or preferences unencrypted
- Assuming JNI protects secrets by default

---

## 9. Lifecycle Management

- Rotate keys and tokens regularly
- Invalidate keys when user logs out or device is compromised
- Plan key backup and recovery only through secure channels

Secrets are only as safe as your lifecycle practices.

---

## 10. Testing & Validation

- Test on rooted devices
- Test memory dumps and heap inspection
- Verify logs do not contain secrets
- Use static and dynamic analysis tools to catch leaks

---

## Senior Rules (Non-Negotiable)

- Always combine Keystore + encryption + lifecycle
- Minimize secret exposure in memory
- Rotate and revoke aggressively
- Never trust native code alone
- Always test against realistic attack scenarios

Protecting secrets is about **engineering discipline**, not magic APIs.

---

## What Comes Next

Logical continuation:
1. Secure token storage and refresh strategies
2. Secure session management in Android apps
3. Reverse-engineering prevention and runtime integrity
4. Secrets management in multi-module or multi-flavor projects

