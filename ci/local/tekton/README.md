# Tekton CI/CD Setup

This directory contains the Tekton CI/CD pipeline configuration for local development.

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

### 2. Install Tekton Dashboard (Optional but recommended)

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

### 3. Run the Pipeline

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
  -w name=maven-cache,claimName=maven-cache-pvc
```

### 4. Monitor Pipeline Runs

```bash
# List pipeline runs
tkn pipelinerun list -n goods-price-ci

# View logs
tkn pipelinerun logs -f -n goods-price-ci <pipeline-run-name>

# View pipeline run status
kubectl get pipelinerun -n goods-price-ci
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

## Tasks

### git-clone
Clones the Git repository into the shared workspace.

### maven-build
Builds the Spring Boot application using Maven with dependency caching.

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

### Pod fails to start
```bash
# Check pod status
kubectl get pods -n goods-price-ci

# Check pod logs
kubectl logs -n goods-price-ci <pod-name>
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
```

## Dashboard Access

After installing the dashboard:

```bash
# Port forward
cd ci/local
./tekton/install-dashboard.sh proxy

# Or manually:
kubectl port-forward -n tekton-pipelines service/tekton-dashboard 9097:9097
```

Open: http://localhost:9097

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

## References

- [Tekton Documentation](https://tekton.dev/docs/)
- [Tekton Dashboard](https://tekton.dev/docs/dashboard/)
- [tkn CLI Reference](https://tekton.dev/docs/cli/)
