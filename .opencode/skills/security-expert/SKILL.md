---
name: security-expert
description: Security — vulnerability assessment, threat modeling, secure coding review, and compliance checking
license: MIT
compatibility: opencode
metadata:
  role: security
  domain: engineering
---

# Security Expert

## Threat Modeling
- STRIDE per component: Spoofing, Tampering, Repudiation, Information Disclosure, Denial of Service, Elevation of Privilege
- Identify trust boundaries — where does data cross from one trust level to another?
- Assume external inputs are malicious until proven safe
- Consider: what's the worst that could happen if this component is compromised?

## Input Validation (OWASP Top 10)
- Validate length, range, format, and character set on every input
- Never concatenate user input into SQL, shell commands, or HTML
- Use parameterized queries, not escaped strings
- Whitelist validation (allow known-good) over blacklist (block known-bad)
- Validate on the server, not just the client

## Authentication & Authorization
- Check auth on every endpoint, not just the ones you remember
- Fail closed — if you can't verify access, deny access
- Use constant-time comparison for secrets and tokens
- Session tokens: random, high entropy, expire, rotate
- API keys: least privilege per key, rotate regularly, revoke immediately on compromise

## Data Protection
- Encrypt data in transit (TLS 1.2+) and at rest (AES-256)
- Never log: passwords, tokens, PII, payment data
- Mask or truncate sensitive data in logs and error messages
- Secrets in environment variables or vaults, never in config files or code
- Database: least-privilege accounts, encrypted connections, audit logging

## Common Vulnerabilities to Check
- SQL/NoSQL injection: dynamic queries with user input
- XSS: unescaped user input rendered in HTML
- CSRF: state-changing requests without anti-forgery tokens
- SSRF: server making requests to user-supplied URLs
- Insecure deserialization: accepting serialized objects from untrusted sources
- Broken access control: can user A access user B's data?
- Security misconfiguration: default credentials, debug endpoints in prod, excessive CORS

## Dependency Security
- Audit all dependencies for known CVEs before adding
- Keep dependencies updated — stale versions accumulate vulnerabilities
- Minimize dependency count — each dependency is an attack surface
- Verify integrity of downloaded dependencies (checksums, signatures)

## Incident Response
- Detect: monitoring, anomaly detection, user reports
- Contain: isolate affected systems, revoke compromised credentials
- Eradicate: remove the root cause, patch the vulnerability
- Recover: restore from clean backup, verify no persistence
- Post-mortem: root cause, timeline, improvements — no blame

---

## Token Optimization

When conducting security reviews and vulnerability assessments, optimize context usage:

```bash
/skill token-optimize
```

**Key practices:**
- Focus on specific security-sensitive files (auth, validation, data handling)
- Search for vulnerability patterns efficiently
- Summarize threat models and findings concisely
- Reuse context when analyzing related security controls
