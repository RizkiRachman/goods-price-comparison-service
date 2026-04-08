# Tekton CI/CD Setup

This directory contains the Tekton CI/CD pipeline configuration for local development.

## ⚠️ IMPORTANT: GitHub Credentials Setup

Before running the pipeline, you must configure GitHub credentials for Maven to download dependencies from GitHub Packages.

**Quick Setup (2 minutes):**
```bash
# 1. Create GitHub PAT: https://github.com/settings/tokens/new
#    - Scope: read:packages (only)
#    - Copy the token

# 2. Configure credentials
./setup-maven-credentials.sh

# 3. Verify
./setup-maven-credentials.sh verify

# 4. Continue below...
```

For detailed instructions, see **`QUICK_START.md`** or **`MAVEN_CREDENTIALS.md`**

## Prerequisites

```bash
# Install required tools
brew install kind kubectl tektoncd-cli

# Verify installations
kind version
kubectl version --client
tkn version
```

## Quick Start

### 1. Start the Tekton Stack

```bash
cd ci/local
./start.sh tekton
```

This will:
- Create a kind cluster named `goods-price-ci`
- Install Tekton Pipelines and Triggers
- Create the `goods-price-ci` namespace
- Apply the pipeline configuration
- Set up required PVCs

### 2. Configure GitHub Credentials (Required!)

```bash
cd tekton
./setup-maven-credentials.sh    # Interactive setup
./setup-maven-credentials.sh verify  # Verify it worked
```

See **`QUICK_START.md`** for detailed instructions.

### 3. Install Tekton Dashboard (Optional but recommended)

```bash
./tekton/install-dashboard.sh install
```

**Access the dashboard (Recommended - kubectl proxy):**
```bash
# Use any available port (e.g., 8081)
kubectl proxy --port=8081
# Then open: http://localhost:8081/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/
```

**Alternative - Port forward:**
```bash
./tekton/install-dashboard.sh proxy
# Then open: http://localhost:9097
```

### 4. Run the Pipeline

**Option A: Using PipelineRun (recommended)**
```bash
kubectl create -f tekton/pipeline-run.yaml
```

**Option B: Using tkn CLI**
```bash
tkn pipeline start goods-price-service-pipeline \
  --showlog \
  -n goods-price-ci \
  -w name=shared-workspace,volumeClaimTemplateFile=tekton/workspace-template.yaml \
  -w name=maven-settings,config=maven-settings
```

**Option C: Using pipeline runner script**
```bash
cd tekton
./run-pipeline.sh run
```

### 5. Monitor Pipeline Runs

```bash
# List pipeline runs
tkn pipelinerun list -n goods-price-ci

# View logs
tkn pipelinerun logs -f -n goods-price-ci <pipeline-run-name>

# Or use the pipeline runner
cd tekton
./run-pipeline.sh logs
```

## Pipeline Structure

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐    ┌────────────┐
│   clone     │───▶│ maven-build  │───▶│ docker-build│───▶│  deploy    │
│  (git-clone)│    │  (mvn build) │    │   (kaniko)  │    │ (kubectl)  │
└─────────────┘    └──────────────┘    └─────────────┘    └────────────┘
```

## Files

| File | Description |
|------|-------------|
| `pipeline.yaml` | Complete pipeline definition with all Tasks |
| `pipeline-run.yaml` | Sample PipelineRun for manual execution |
| `maven-cache-pvc.yaml` | PersistentVolumeClaim for Maven dependencies |
| `workspace-template.yaml` | Template for pipeline workspaces |
| `install-dashboard.sh` | Script to install Tekton Dashboard |
| `run-pipeline.sh` | Pipeline runner with 20+ commands |
| `setup-maven-credentials.sh` | Setup GitHub credentials for Maven |
| `maven-settings-configmap.yaml` | Maven settings with GitHub configuration |
| `maven-credentials-secret.yaml` | Kubernetes secret template for credentials |
| `QUICK_START.md` | 2-minute setup guide |
| `MAVEN_CREDENTIALS.md` | Comprehensive Maven authentication guide |

## Tasks

### git-clone
Clones the Git repository into the shared workspace.

### maven-build
Builds the Spring Boot application using Maven with dependency caching.
**Note**: Uses GitHub credentials from ConfigMap and Secret for private dependencies.

### docker-build-push
Builds Docker image using Kaniko and pushes to local registry.

### deploy-to-local
Deploys the application to the local kind cluster.

## Cleanup

```bash
# Stop Tekton kind cluster
./start.sh tekton-stop

# Or manually:
kind delete cluster --name goods-price-ci
```

## Troubleshooting

### Build fails with "401 Unauthorized"
Maven can't authenticate with GitHub Packages. Fix credentials:
```bash
cd tekton
./setup-maven-credentials.sh delete
./setup-maven-credentials.sh
./setup-maven-credentials.sh verify
```

### Pod fails to start
```bash
# Check pod status
kubectl get pods -n goods-price-ci

# Check pod logs
kubectl logs -n goods-price-ci <pod-name>

# Use pipeline runner for diagnosis
cd tekton
./run-pipeline.sh diagnose
```

### Kaniko fails to push to registry
Ensure the local registry is accessible from within the kind cluster:
```bash
# Check if registry is running
docker ps | grep registry

# For kind, you may need to configure insecure registry
```

### PipelineRun stuck
```bash
# Describe the pipeline run
kubectl describe pipelinerun -n goods-price-ci <name>

# Check task runs
kubectl get taskrun -n goods-price-ci

# Use pipeline runner for more help
cd tekton
./run-pipeline.sh diagnose
./run-pipeline.sh force-cleanup
```

### ConfigMap or Secret not found
```bash
# Check what exists
kubectl get secrets -n goods-price-ci
kubectl get configmap -n goods-price-ci

# Recreate if missing
cd tekton
./setup-maven-credentials.sh
```

## Dashboard Access

After installing the dashboard:

```bash
# Port forward
cd ci/local/tekton
./install-dashboard.sh proxy

# Or manually:
kubectl port-forward -n tekton-pipelines service/tekton-dashboard 9097:9097
```

Open: http://localhost:9097

## Maven GitHub Credentials

The pipeline requires GitHub credentials to download private dependencies from GitHub Packages.

### Setup
```bash
cd tekton
./setup-maven-credentials.sh    # Interactive setup
./run-pipeline.sh maven-verify  # Verify credentials
```

### What it creates
- **Secret**: `github-maven-credentials` - stores username and token
- **ConfigMap**: `maven-settings` - contains Maven settings.xml with GitHub repository configuration

See **`MAVEN_CREDENTIALS.md`** for details.

## Webhook Triggers (Advanced)

The pipeline includes TriggerTemplate, TriggerBinding, and EventListener for GitHub webhooks.

To expose the webhook locally for testing:
```bash
# Using ngrok
ngrok http 8080

# Or using localtunnel
npx localtunnel --port 8080
```

Configure the webhook URL in your GitHub repository settings.

## Quick Commands

```bash
# Setup
./setup-maven-credentials.sh              # Configure GitHub credentials
./run-pipeline.sh maven-verify            # Verify credentials

# Run
./run-pipeline.sh run                     # Full setup and run
kubectl create -f pipeline-run.yaml       # Manual run

# Monitor
./run-pipeline.sh logs                    # Follow logs
./run-pipeline.sh status                  # Check status
./install-dashboard.sh proxy              # Open dashboard

# Troubleshoot
./run-pipeline.sh diagnose                # Diagnose issues
./run-pipeline.sh force-cleanup           # Clean stuck pods
./run-pipeline.sh maven-verify            # Verify credentials

# Cleanup
./run-pipeline.sh delete                  # Delete old runs
./run-pipeline.sh auto-cleanup            # Auto cleanup pods
```

## Documentation

- **Quick Start**: `QUICK_START.md` (2 minutes)
- **Maven Setup**: `MAVEN_CREDENTIALS.md` (comprehensive)
- **Pipeline Runner**: `./run-pipeline.sh help`
- **Setup Script**: `./setup-maven-credentials.sh help`

## References

- [Tekton Documentation](https://tekton.dev/docs/)
- [Tekton Dashboard](https://tekton.dev/docs/dashboard/)
- [tkn CLI Reference](https://tekton.dev/docs/cli/)
- [GitHub Packages](https://docs.github.com/en/packages)
- [Maven Settings](https://maven.apache.org/settings.html)
