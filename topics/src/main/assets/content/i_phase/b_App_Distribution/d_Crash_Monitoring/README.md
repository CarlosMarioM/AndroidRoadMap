# Crash Monitoring on Android

Crash monitoring is **not optional**. Unhandled crashes destroy user experience, retention, and trust. This guide covers real crash monitoring workflows using Play Console, Firebase, and best practices.

---

## 1. Crash Sources

- Java/Kotlin exceptions
- Native crashes (NDK / JNI)
- ANRs (Application Not Responding)
- Background services

---

## 2. Play Console Crash Reports

- Collects crashes and ANRs from production devices
- Shows stack traces, device info, and impact
- Provides aggregated metrics (crash-free users, affected sessions)

### Best Practices
- Monitor daily
- Investigate high-impact crashes first
- Correlate with recent releases or features

---

## 3. Firebase Crashlytics

- Real-time crash reporting
- Symbolicated stack traces for native crashes
- Custom logging and breadcrumbs
- Alerts and dashboards

### Integration Tips
- Initialize early in app startup
- Log relevant contextual information
- Set user identifiers carefully (privacy-compliant)

---

## 4. Native Crash Monitoring (NDK)

- Use Firebase NDK crash reporting or Breakpad/Crashpad
- Include proper symbol mapping (debug symbols) for stack traces
- Monitor memory corruption, illegal operations, and JNI misuse

---

## 5. ANR Monitoring

- ANRs are not exceptions — they require separate attention
- Use Play Console ANR reports
- Detect main thread blockages and long-running operations

### Best Practices
- Avoid blocking main thread
- Use WorkManager, coroutines, or background threads
- Profile UI responsiveness regularly

---

## 6. Metrics to Track

- Crash-free users
- Crash-free sessions
- Top crashes by frequency and impact
- ANR rate
- Device and OS distribution of crashes

---

## 7. Logging & Context

- Use breadcrumbs for feature-level context
- Log network, user actions, and device state
- Avoid logging sensitive information
- Helps correlate crash to cause

---

## 8. Automated Alerts

- Configure notifications for high-frequency crashes
- Use Slack, email, or monitoring dashboards
- Respond quickly to spikes after release

---

## 9. Release Integration

- Monitor crashes after internal/closed track releases
- Compare metrics against previous release
- Adjust rollout if critical issues detected

---

## 10. Senior-Level Rules

- Treat crashes and ANRs as high-priority production issues
- Integrate native and Java/Kotlin crash reporting
- Use CI/CD to ensure debug symbols are uploaded
- Continuously monitor and triage crashes
- Correlate crashes with user feedback and release changes

Crash monitoring is **foundational** for app quality and user trust.

---

## What Comes Next

Logical continuations:
1. Crash triaging and root cause analysis workflows
2. Automated regression detection
3. Performance monitoring integration (CPU, memory, battery)
4. High-risk feature monitoring during staged rollouts
5. Multi-module app crash isolation strategies

