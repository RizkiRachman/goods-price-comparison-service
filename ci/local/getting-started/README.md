# Getting Started Guide for Local CI/CD Stack

Welcome! This guide will walk you through setting up and using the local CI/CD stack, even if you're completely new to Kubernetes, Docker, and CI/CD tools.

## 📚 Quick Reference

**Just need commands?** → [Quick Reference Card](00-quick-reference.md)

## What is This?

This local CI/CD stack simulates production tools on your Mac:

| Production Tool | Local Alternative | Purpose |
|----------------|------------------|---------|
| **Quay** | Docker Registry | Container Registry |
| **Harness** | Jenkins / Tekton | CI/CD Pipeline |
| **OpenShift** | Docker Compose / kind | Container Platform |
| **Gravitee** | Kong | API Gateway |

## Quick Start Checklist

- [ ] [Quick Reference](00-quick-reference.md) - Essential commands
- [ ] [Install Prerequisites](01-prerequisites.md) - Docker, kubectl, kind
- [ ] [Start Jenkins Stack](02-jenkins-setup.md) - Docker Compose approach
- [ ] [Start Tekton Stack](03-tekton-setup.md) - Kubernetes approach
- [ ] [Access Kubernetes Dashboard](04-k8s-dashboard.md) - View cluster resources
- [ ] [Access Tekton Dashboard](05-tekton-dashboard.md) - View pipelines
- [ ] [Login to Jenkins](06-jenkins-login.md) - Run CI/CD jobs
- [ ] [Troubleshooting](99-troubleshooting.md) - Fix common issues

## Which Path Should I Choose?

### Path 1: Jenkins (Easier) ⭐ Recommended for Beginners
- Uses Docker Compose
- Familiar UI
- Good for beginners
- **Start here:** [Jenkins Setup](02-jenkins-setup.md)

### Path 2: Tekton (More Modern)
- Uses Kubernetes (kind)
- Cloud-native approach
- Similar to production Harness
- **Start here:** [Tekton Setup](03-tekton-setup.md)

## Service URLs (After Starting)

### Jenkins Stack

| Service | URL | Credentials |
|---------|-----|-------------|
| **Jenkins** | http://localhost:8082 | admin / admin123 |
| **Application** | http://localhost:8080 | - |
| **Docker Registry** | http://localhost:5000 | - |
| **Harbor UI** | http://localhost:8081 | admin / Harbor12345 |
| **Kong Proxy** | http://localhost:8000 | - |
| **Kong Admin** | http://localhost:8001 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin / admin |

### Tekton Stack

| Service | Access Method | Credentials |
|---------|--------------|-------------|
| **Tekton Dashboard** | http://localhost:9097 (port-forward) | None needed |
| **Kubernetes Dashboard** | Via kubectl proxy | Token required |
| **Application** | kubectl port-forward | - |

## First Time Setup

**Completely new?** Follow this order:

1. [Install Prerequisites](01-prerequisites.md) - One-time setup
2. Choose your path:
   - [Jenkins Setup](02-jenkins-setup.md) - Easier, recommended
   - [Tekton Setup](03-tekton-setup.md) - More advanced
3. [Access Dashboards](04-k8s-dashboard.md) - Visual management

## Need Help?

1. **Quick fix?** → [Quick Reference](00-quick-reference.md)
2. **Common problems?** → [Troubleshooting Guide](99-troubleshooting.md)
3. **Specific tool?**
   - [Main README](../README.md)
   - [Tekton README](../tekton/README.md)

## File Structure

```
ci/local/getting-started/
├── README.md                    # This file - Start here!
├── 00-quick-reference.md      # Command cheat sheet
├── 01-prerequisites.md          # Install required tools
├── 02-jenkins-setup.md          # Jenkins path guide
├── 03-tekton-setup.md           # Tekton path guide
├── 04-k8s-dashboard.md          # Kubernetes Dashboard
├── 05-tekton-dashboard.md       # Tekton Dashboard
├── 06-jenkins-login.md          # Jenkins access
└── 99-troubleshooting.md        # Problem solving
```

---

**Ready to start?** Go to [Prerequisites →](01-prerequisites.md) or grab the [Quick Reference →](00-quick-reference.md)
