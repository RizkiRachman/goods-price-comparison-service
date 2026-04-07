# 01 - Prerequisites Installation

Before you can use the local CI/CD stack, you need to install some tools on your Mac.

## Required Tools

### 1. Docker Desktop
**What it is:** Runs containers on your Mac  
**Why you need it:** Everything runs inside Docker containers

**Installation:**
```bash
# Download from official site
curl -L -o Docker.dmg "https://desktop.docker.com/mac/main/arm64/Docker.dmg"

# Or install via Homebrew (alternative)
brew install --cask docker
```

**After installing:**
1. Open Docker Desktop from Applications
2. Wait for it to start (whale icon in menu bar stops animating)
3. Verify:
```bash
docker --version
docker-compose --version
```

### 2. kubectl (Kubernetes CLI)
**What it is:** Command-line tool for Kubernetes  
**Why you need it:** Controls the Kubernetes cluster (for Tekton stack)

**Installation:**
```bash
brew install kubectl
```

**Verify:**
```bash
kubectl version --client
```

### 3. kind (Kubernetes in Docker)
**What it is:** Runs a local Kubernetes cluster inside Docker  
**Why you need it:** For the Tekton CI/CD stack

**Installation:**
```bash
brew install kind
```

**Verify:**
```bash
kind version
```

### 4. Tekton CLI (tkn) - Optional but recommended
**What it is:** Command-line tool for Tekton  
**Why you need it:** Easier pipeline management

**Installation:**
```bash
brew install tektoncd-cli
```

**Verify:**
```bash
tkn version
```

## Full One-Command Install

If you have Homebrew installed:
```bash
brew install kubectl kind tektoncd-cli
brew install --cask docker
```

## Verify Everything Works

Run this checklist:

```bash
# 1. Check Docker
docker ps
# Should show: CONTAINER ID   IMAGE   COMMAND   CREATED   STATUS   PORTS   NAMES

# 2. Check kubectl
kubectl version --client
# Should show: Client Version: v1.x.x

# 3. Check kind
kind version
# Should show: kind v0.x.x

# 4. Check tkn (optional)
tkn version
# Should show: Client version: 0.x.x
```

## Common Issues

### "Cannot connect to Docker daemon"
**Fix:** Docker Desktop isn't running. Open it from Applications.

### "brew command not found"
**Fix:** Install Homebrew first:
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### Permission denied errors
**Fix:** Some commands might need sudo, but prefer using Homebrew which doesn't require sudo.

## What's Next?

Choose your path:
- [Jenkins Setup (Easier) →](02-jenkins-setup.md)
- [Tekton Setup (Modern) →](03-tekton-setup.md)

Or go back to [Getting Started Overview →](README.md)
