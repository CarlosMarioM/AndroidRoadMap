# Android Release Pipelines

This document explains **how senior Android teams design, automate, and harden release pipelines**, from code freeze to Play Store rollout.

Release pipelines are not about speed — they are about **correctness, reproducibility, and risk control**.

---

## What a release pipeline really is

A release pipeline is:
- A **deterministic sequence** of steps
- Produces **signed, reproducible artifacts**
- Has **clear promotion gates**
- Minimizes human error

If you can’t re-run yesterday’s release and get the same output, your pipeline is broken.

---

## Core goals of a release pipeline

1. **Reproducibility** – same input → same AAB
2. **Traceability** – know exactly what code shipped
3. **Safety** – limit blast radius
4. **Auditability** – who released what, when, and why

---

## Typical Android release stages

### 1. Code freeze

- Only critical fixes allowed
- Feature flags preferred over late merges
- Version bump prepared

---

### 2. Release build creation

```bash
./gradlew clean bundleRelease
```

Requirements:
- R8 enabled
- Resource shrinking enabled
- ProGuard rules locked
- Deterministic dependencies

---

### 3. Automated verification

Mandatory checks:
- Unit tests (already green)
- Smoke UI tests (minimal)
- Lint (release variant)

```bash
./gradlew lintRelease testReleaseUnitTest
```

---

### 4. Signing and artifact handling

- Use **CI-managed signing keys**
- Never sign locally
- Secure keystore access via secrets manager

Artifacts to store:
- AAB
- Mapping files (`mapping.txt`)
- Native symbols

---

### 5. Distribution

Options:
- Internal testing
- Closed testing
- Staged production rollout

Never jump straight to 100%.

---

### 6. Rollout monitoring

Monitor:
- Crash rate
- ANRs
- Startup time
- User feedback

If metrics spike → halt rollout.

---

## Versioning strategy

### Semantic-ish versioning

```text
versionCode = monotonically increasing
versionName = X.Y.Z
```

- Automate versionCode
- Never reuse versionCode

---

## Release branching strategy

### Common patterns

- `main` + `release/*`
- Trunk-based + tags

Rule:
> The release branch should only exist to stabilize, not to develop.

---

## Feature flags in releases

- Ship code dark
- Enable features remotely
- Roll back without redeploying

Feature flags reduce release pressure.

---

## Failure handling

### Rollback options

- Halt rollout
- Roll forward with hotfix
- Disable features remotely

Never rely on Play Store rollback alone.

---

## Security considerations

- Lock release pipeline permissions
- Separate build and approval roles
- Audit access to signing keys

---

## Common anti-patterns

- Manual signing
- Rebuilding artifacts after QA approval
- Releasing from developer machines
- No monitoring after rollout

These cause real incidents.

---

## Senior rules of thumb

1. Release pipelines change **less often** than CI pipelines
2. Every release must be traceable to a commit
3. Humans approve — machines build
4. If a step is manual, it will fail eventually

---

## Mental model

> A release pipeline is a **risk management system**, not a delivery shortcut.

---

## Interview takeaway

**Senior Android developers design release pipelines to be boring, predictable, and safe**, prioritizing reproducibility, security, and controlled rollouts over raw speed.

