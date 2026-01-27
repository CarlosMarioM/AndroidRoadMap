# Certificate Pinning on Android

Certificate pinning is **not security by default**.

It is a **high-risk, high-reward** technique that breaks apps when done wrong — which is most of the time.

---

## What Certificate Pinning Actually Does

Pinning restricts which certificates or public keys your app trusts.

Instead of trusting **any CA** trusted by the OS, you trust:
- A specific certificate, or
- A specific public key (preferred)

This protects against:
- Compromised CAs
- Corporate MITM proxies
- Malicious Wi‑Fi interception

It does **not** protect against:
- Rooted devices
- Hooking (Frida, Xposed)
- Runtime TLS bypass

---

## When You SHOULD Use Pinning

Use pinning **only if**:

- You handle financial data
- You handle medical data
- You are a high‑value target
- MITM risk is unacceptable

If your app is a todo list, don’t pin. You’ll just break prod.

---

## What to Pin (This Matters)

### ❌ Pinning Leaf Certificates

Bad idea:
- Certs expire
- Certs rotate
- Emergency renewals happen

You *will* brick your app.

### ✅ Pinning Public Keys (SPKI)

Correct approach:
- Pin the **public key hash**
- Allow multiple pins (current + backup)

This survives cert rotation.

---

## Android Network Security Config (Preferred)

Use **network_security_config.xml**.

Benefits:
- Declarative
- No code
- Harder to bypass than app code

Example logic:
- Trust system CAs
- Add pins for your domain
- Set expiration

If you do pinning in OkHttp only, attackers laugh.

---

## Pin Expiration (Non‑Optional)

Pins **must expire**.

Why:
- Key compromise happens
- Crypto agility matters

If your pin has no expiration, you’re irresponsible.

---

## Pinning with OkHttp (Reality Check)

OkHttp supports pinning, but:

- Easy to bypass with hooks
- Easy to misconfigure
- Often duplicated incorrectly

Use it:
- As an extra layer
- Not as your only defense

Never hardcode pins in random utils.

---

## Debug vs Release Behavior

You **must** handle this:

- Debug builds: pinning OFF
- Release builds: pinning ON

Otherwise:
- Developers can’t debug
- QA can’t proxy traffic

Do not ship debug exceptions to prod.

---

## Common Failures (Seen in Real Apps)

- Single pin only
- No backup pin
- No expiration
- Pinning leaf cert
- Forgetting CDN certificates
- Breaking after server migration

Any one of these = outage.

---

## Pinning + CDN + Load Balancers

If you use:
- Cloudflare
- AWS ALB
- Firebase

You **must**:
- Understand who owns the cert
- Coordinate rotations
- Pin upstream keys if possible

Blind pinning + CDN = disaster.

---

## Bypass Reality (Be Honest)

Pinning can be bypassed by:
- Frida hooks
- Custom TrustManagers
- Native TLS interception

Pinning raises cost.
It does not make you invincible.

---

## Correct Mental Model

Pinning is:
- A delay tactic
- A risk reducer
- Not a silver bullet

Combine with:
- Keystore‑backed secrets
- Runtime integrity checks
- Backend anomaly detection

---

## Senior Rules (Non‑Negotiable)

- Pin public keys, not certs
- Always have backup pins
- Always set expiration
- Never rely on one layer
- Test rotation before prod

Ignore these and you will ship an outage.

---

## What Comes Next

Logical follow‑ups:

- TLS internals on Android
- Anti‑MITM beyond pinning
- Runtime integrity & tamper checks
- Reverse‑engineering TLS bypasses
- Secure backend validation strategies

Pick the next layer.

