# Signing Configs on Android

Signing configs are **critical**. Incorrect signing breaks releases, updates, and security. This guide covers proper Gradle configuration, key management, and best practices.

---

## 1. Why Signing Matters

- Ensures **app authenticity**
- Enables **updates** (version code matching)
- Prevents others from publishing under your package name
- Protects users from tampered APKs/AABs

No signature → app will not install from Play Store.

---

## 2. Keystore Basics

### Key Types
- **Private Key**: used for signing (never shared)
- **Certificate**: public part, verifies signature

### Key Attributes
- Alias: identifier for key
- Password: protects private key
- Validity: 25+ years recommended
- Algorithm: RSA or EC (EC preferred for new apps)

---

## 3. Gradle Signing Configs

### Basic Setup

```gradle
android {
    signingConfigs {
        release {
            keyAlias 'mykey'
            keyPassword 'keypassword'
            storeFile file('keystore.jks')
            storePassword 'storepassword'
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

> Never commit passwords or keystore files to source control.

---

### Secure Gradle Properties

- Use `gradle.properties` or environment variables
- Reference them in build.gradle:

```gradle
keyAlias = project.property('KEY_ALIAS')
keyPassword = project.property('KEY_PASSWORD')
storeFile = file(project.property('STORE_FILE'))
storePassword = project.property('STORE_PASSWORD')
```

This avoids exposing secrets in code.

---

## 4. App Bundle vs APK

- **AAB**: recommended, Play Store signs with your key
- **APK**: manual signing required

Play App Signing helps you **secure original key** while Google handles distribution.

---

## 5. Signing for CI/CD

- Never store signing keys in repo
- Use CI/CD secrets (GitHub Actions, GitLab, etc.)
- Automate signing and uploads
- Ensure correct signing per flavor/buildType

---

## 6. Key Rotation

- Possible with Play App Signing
- Always maintain backup keys securely
- Avoid losing keys: cannot update app without them

---

## 7. Debug vs Release Signing

- **Debug**: auto-generated key by Android Studio
- **Release**: your secure key, never use debug for production
- Keep keys separate to prevent accidental upload

---

## 8. Common Mistakes

- Committing keystore files or passwords
- Using the same key for multiple apps
- Forgetting to increment versionCode before release
- Signing release with debug key by accident
- Not enabling Play App Signing for new apps

---

## 9. Senior-Level Rules

- Always secure keystore passwords outside code
- Use Play App Signing for production
- Never reuse keys across unrelated apps
- Automate signing in CI/CD pipelines
- Test signing workflow before production rollout

Signing configs are **foundational**. Mistakes here are catastrophic.

---

## What Comes Next

Logical continuations:
1. Play App Signing internals & key management
2. CI/CD automation for signing & release
3. Multi-flavor signing strategies
4. Secure key backup & rotation practices
5. Debugging signing & update issues

