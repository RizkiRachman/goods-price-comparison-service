---
name: devops-expert
description: DevOps — CI/CD pipelines, infrastructure as code, containerization, monitoring, and deployment strategies
license: MIT
compatibility: opencode
metadata:
  role: devops
  domain: infrastructure
---

# DevOps Expert

## CI/CD
- Pipeline stages should fail fast — lint before build, build before test
- Build artifacts should be immutable and versioned
- Secrets never in code — use vault, env vars, or secrets manager
- Each environment (dev/staging/prod) should be as identical as possible
- Database migrations must be backward-compatible for zero-downtime deploys
- Rollback plan before every deploy — test it, don't just document it

## Infrastructure as Code
- All infrastructure changes go through the same review process as code
- State files are sensitive — encrypt at rest, never commit to git
- Use modules for reusable patterns (don't copy-paste HCL/YAML)
- Tag all resources: environment, project, cost center, owner

## Containerization
- One process per container — don't run multiple services in one container
- Use distroless or minimal base images to reduce attack surface
- Pin base image versions — don't use `latest`
- Containers should be stateless; persistence goes in volumes or external services
- Health checks: liveness (is it alive?) vs readiness (can it serve traffic?)

## Monitoring & Observability
- Three pillars: logs, metrics, traces. You need all three.
- Logs are for debugging specific incidents
- Metrics are for detecting trends and anomalies
- Traces are for understanding request flow across services
- Alert on symptoms (error rate > 1%), not causes (disk at 80%)
- On-call needs runbooks, not guesses

## Deployment Strategies
- Blue-green: two identical environments, switch traffic atomically
- Canary: roll out to a subset of users, monitor, then full roll
- Feature flags: decouple deploy from release — turn features on/off without redeploy
- Never deploy on Friday afternoon

## Security in DevOps
- Scan dependencies for known vulnerabilities in CI
- Container images should be scanned before deploy
- Least privilege for all service accounts and IAM roles
- Network policies: default-deny, allow by exception
- Audit logging on all infrastructure changes

---

## Token Optimization

When working with infrastructure and deployment configurations, optimize context usage:

```bash
/skill token-optimize
```

**Key practices:**
- Focus on relevant config files and environments
- Use targeted searches for specific infrastructure components
- Summarize deployment plans concisely
- Reuse context when iterating on configurations
