# BiometricPrompt on Android

Biometrics are **UX for key access**, not magic security.

If you think biometrics "protect your data", you already misunderstand the model.

---

## What BiometricPrompt Actually Does

BiometricPrompt:
- Authenticates **the user**, not the device
- Gates access to **Keystore keys**
- Delegates trust to secure hardware when available

It does **NOT**:
- Encrypt your data by itself
- Replace passwords
- Stop a rooted or hooked device

Biometrics unlock keys — nothing more.

---

## Supported Authentication Methods

Depending on Android version and device:

- Fingerprint
- Face
- Iris (legacy)
- Device credential (PIN / Pattern / Password)

You **must** define acceptable authenticators explicitly.

---

## The Correct Mental Model

Think of it as:

> "Allow this Keystore key to be used **only after user authentication**"

If you are not using Keystore keys, BiometricPrompt adds **zero real security**.

---

## Correct Use Cases

BiometricPrompt is appropriate for:

- Unlocking encrypted local data
- Approving sensitive actions
- Protecting private keys
- Re-authentication after timeout

It is NOT appropriate for:

- Login replacement without backend verification
- Long-term session security
- Protecting API keys embedded in the app

---

## Biometric + Keystore (The Only Correct Pattern)

### Key Generation Rules

When creating a key:

- Set `setUserAuthenticationRequired(true)`
- Define authentication validity window or per-use
- Prefer **StrongBox** if available

This binds the key to:
- User presence
- Secure hardware (when supported)

---

## Authentication Validity Window

You must choose:

- **Per-use authentication** (most secure)
- **Time-based window** (better UX)

Trade-off:
- Short window = more prompts
- Long window = weaker protection

Never leave this undefined.

---

## BiometricPrompt Flow (Correct)

1. App requests crypto operation
2. System shows biometric UI
3. User authenticates
4. Keystore allows key usage
5. Crypto operation proceeds

The app **never sees biometric data**.

---

## Failure Modes You Must Handle

Common failures:

- Biometric lockout
- Hardware unavailable
- No biometrics enrolled
- User cancels

Always:
- Provide device credential fallback
- Explain failure clearly

No fallback = broken UX.

---

## Security Limitations (Be Honest)

Biometrics do NOT protect against:

- Runtime memory access
- Hooking frameworks
- Compromised OS

They protect against:

- Casual access
- Lost device scenarios
- Shoulder surfing

---

## Anti-Patterns (Do Not Do This)

- Using biometrics without Keystore
- Storing secrets after auth without encryption
- Treating biometric success as login
- Rolling your own biometric UI

These are rookie mistakes.

---

## BiometricPrompt + NDK

Native code can:
- Trigger biometric-gated key usage
- Perform crypto after auth

But:
- Auth decision stays in Java/Kotlin
- Native code must never decide trust

If native code decides auth, security is fake.

---

## Debugging & Testing

You must test:

- Fresh install
- No biometrics enrolled
- Lockout scenarios
- Device credential fallback
- API level differences

Emulators lie. Test real devices.

---

## Senior Rules (Non-Negotiable)

- Biometrics gate keys, not data
- Always use Keystore
- Always define fallback
- Never trust biometric success alone
- Treat UX and security separately

Break any of these and your design is flawed.

---

## What Comes Next

Logical continuations:

- Keystore internals & StrongBox
- Secure session re-auth flows
- Anti-tampering techniques
- Reverse-engineering biometric flows
- Threat modeling biometric apps

Pick the next layer.

