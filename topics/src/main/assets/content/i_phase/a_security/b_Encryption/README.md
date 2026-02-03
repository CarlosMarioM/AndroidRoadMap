# Encryption on Android

This is **real encryption**, not cargo-cult crypto.

If you don’t understand *why* each choice is made, you are doing it wrong.

---

## What Encryption Is (and Is Not)

Encryption:
- Protects **data at rest** and **data in transit**
- Assumes the attacker *will* get access to storage

Encryption does **NOT**:
- Protect against compromised devices
- Protect secrets embedded in your APK
- Replace authentication or authorization

If your threat model is wrong, encryption is theater.

---

## Core Crypto Goals

Every crypto system tries to achieve:

1. **Confidentiality** – data is unreadable
2. **Integrity** – data cannot be modified silently
3. **Authenticity** – data comes from who you expect

On Android, this usually means:
- AES for data
- RSA or EC for key exchange
- HMAC or AEAD for integrity

---

## AES on Android (The Only Correct Default)

### Use AES-GCM

**AES-GCM** gives you:
- Encryption
- Authentication
- Integrity

In one operation.

Never use:
- AES-ECB ❌
- AES-CBC without MAC ❌

### Correct Parameters

- Key size: **256 bits** (128 is fine, but no reason not to)
- Mode: `GCM`
- Padding: `NoPadding`
- IV: **12 bytes**, random, unique per encryption

---

## Android Keystore + AES (Correct Pattern)

Keys **must never leave the Keystore**.

You encrypt/decrypt *through* the key.

Benefits:
- Hardware-backed when available
- Keys non-exportable
- Protected from app backup

Use cases:
- Encrypt SharedPreferences
- Encrypt databases
- Encrypt files

---

## RSA / EC on Android (When and Why)

Asymmetric crypto is **not** for bulk data.

Use it for:
- Key exchange
- Signing
- Secure bootstrap

### Correct Usage

- Generate RSA/EC keypair in Keystore
- Use it to encrypt or unwrap an AES key
- Store AES key encrypted

If you encrypt files directly with RSA, you failed.

---

## Encryption at Rest

### Correct Stack (High-Level)

- EncryptedSharedPreferences
- EncryptedFile
- SQLCipher (with Keystore-protected key)

These use:
- AES-GCM
- Random IVs
- Secure key storage

### Common Mistakes

- Hardcoding passwords
- Reusing IVs
- Using Base64 as “encryption”

---

## Encryption in Transit

### TLS (Always)

- HTTPS only
- TLS 1.2+ minimum
- Certificate validation ON

### Certificate Pinning

Use pinning if:
- High-value targets
- Financial or medical data

Be aware:
- Pinning breaks on cert rotation
- Needs update strategy

---

## Password-Based Encryption (Avoid If Possible)

If you must:

- Use **PBKDF2**, **scrypt**, or **Argon2**
- High iteration count
- Unique salt per user

Never:
- Store raw passwords
- Roll your own KDF

---

## Encryption + NDK (Native Crypto)

### When Native Crypto Makes Sense

- Performance-critical pipelines
- DSP / emulators
- Streaming encryption

### Risks

- Easier to reverse engineer
- Harder to audit
- Memory handling errors

If you do crypto in C/C++:
- Use battle-tested libs (BoringSSL, libsodium)
- Zero memory explicitly
- Avoid JNI round-trips per block

---

## Key Lifecycle (Most Apps Ignore This)

You must define:

- Key creation
- Key rotation
- Key invalidation
- Key revocation

Android Keystore supports:
- User authentication requirements
- Device lock dependency
- Biometric gating

Ignoring lifecycle = future breach.

---

## Threat Reality Check

What encryption protects:
- Lost phone
- Backup extraction
- Offline attackers

What it doesn’t:
- Rooted devices
- Runtime memory inspection
- Hooking (Frida, Xposed)

Defense is **layers**, not crypto alone.

---

## Senior-Level Rules (Non-Negotiable)

- Never invent crypto
- Never store secrets in code
- Never reuse IVs
- Never skip authentication
- Always define threat model first

If you violate any of these, your encryption is broken — period.

---

## What Comes Next

Natural continuation topics:

- Key Management & Rotation Strategies
- Biometric-bound encryption
- Anti-tampering vs crypto
- Secure communication protocols
- Reverse engineering encrypted apps

Pick one. This stack builds fast.