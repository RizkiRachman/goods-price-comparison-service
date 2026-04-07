# 05 - Tekton Dashboard Guide

The **Tekton Dashboard** is a web UI for viewing and managing your CI/CD pipelines.

## What's the Difference?

| Dashboard | Shows | Login Required? |
|-----------|-------|-----------------|
| **Tekton Dashboard** | Pipelines, tasks, logs | ❌ No (read-only mode) |
| **Kubernetes Dashboard** | All cluster resources | ✅ Yes (token needed) |

## Quick Access

### Method 1: kubectl proxy (Recommended - Most Stable)

```bash
# Use any available port (e.g., 8081, 8082, etc.)
kubectl proxy --port=8081
```

Open: `http://localhost:8081/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/`

**Why this is better:**
- No "broken pipe" errors
- More stable connection
- Works through the Kubernetes API server

**Keep the terminal open** - closing it stops the proxy.

### Method 2: Port-forward (Alternative)

```bash
./tekton/install-dashboard.sh proxy
```

Open: `http://localhost:9097`

**Note:** You may see "broken pipe" warnings in terminal - these are normal and don't affect functionality.

### Not Installed Yet?

```bash
./tekton/install-dashboard.sh install

# Then use kubectl proxy (recommended):
kubectl proxy --port=8081
```

## Dashboard Interface

### Left Sidebar Navigation

| Menu Item | What You See |
|-----------|--------------|
| **Pipelines** | List of all pipeline definitions |
| **PipelineRuns** | Running and completed pipeline executions |
| **Tasks** | Reusable task definitions |
| **TaskRuns** | Individual task executions |
| **Triggers** | Webhook trigger configurations |
| **EventListeners** | Listeners for GitHub/GitLab webhooks |
| **Secrets** | Kubernetes secrets |
| **ServiceAccounts** | Service accounts for pipeline execution |

### Main Views

#### 1. PipelineRuns (Most Important)

This is where you monitor your builds.

**Columns:**
- Name: Auto-generated run name
- Pipeline: Which pipeline ran
- Status: ⏳ Pending, 🔄 Running, ✅ Succeeded, ❌ Failed
- Started: When it started
- Duration: How long it took

**Actions:**
- Click a row → View details
- Click "Logs" → See real-time logs
- Click "Delete" → Remove old runs

#### 2. Pipeline Details

Click on a Pipeline name to see:
- **YAML**: Pipeline definition
- **PipelineRuns**: History of executions
- **Tasks**: Tasks used in the pipeline

#### 3. Live Logs

When viewing a running PipelineRun:
1. Click on any task (clone, build, docker-build, deploy)
2. See real-time logs streaming
3. Logs auto-refresh as the task runs

## Starting a Pipeline from Dashboard

**Note:** Read-only mode doesn't allow creating runs from UI. Use CLI instead:

```bash
# Start a new run
tkn pipeline start goods-price-service-pipeline \
  --showlog \
  -n goods-price-ci \
  -w name=shared-workspace,volumeClaimTemplateFile=tekton/workspace-template.yaml \
  -w name=maven-cache,claimName=maven-cache-pvc
```

Or create a PipelineRun:
```bash
kubectl create -f tekton/pipeline-run.yaml
```

Then refresh the Dashboard to see it running!

## Reading Pipeline Status

### Status Indicators

- 🟢 **Succeeded** - All tasks completed
- 🔴 **Failed** - One or more tasks failed
- 🟡 **Running** - Currently executing
- ⚪ **Pending** - Waiting for resources
- ⏸️ **Cancelled** - Manually stopped

### Finding Failures

1. Go to PipelineRuns
2. Look for ❌ Failed status
3. Click the run name
4. Find the red (failed) task
5. Click "Logs" to see the error

**Common failures:**
- `git-clone`: Network issues, wrong URL
- `maven-build`: Compilation errors, test failures
- `docker-build`: Dockerfile issues, registry not accessible
- `deploy`: Kubernetes resource conflicts

## Advanced Features

### Filtering PipelineRuns

In the Dashboard:
1. Go to PipelineRuns
2. Use the filter dropdown (top right)
3. Filter by:
   - Status (Running, Succeeded, Failed)
   - Pipeline name
   - Time range

### Viewing Task Details

1. Go to Tasks
2. Click a task name (e.g., `maven-build`)
3. See:
   - Task definition (YAML)
   - All TaskRuns using this task
   - Parameters and workspaces

### Secrets Management

⚠️ View-only in read-only mode

To create secrets, use kubectl:
```bash
kubectl create secret generic my-secret \
  --from-literal=key=value \
  -n goods-price-ci
```

Then view in Dashboard → Secrets

## Auto-Restart Port-Forward

If your connection drops frequently:

```bash
./tekton/install-dashboard.sh proxy-always
```

This automatically restarts the connection every 2 seconds if it fails.

## Common Commands Reference

```bash
# Install dashboard
./tekton/install-dashboard.sh install

# Uninstall dashboard
./tekton/install-dashboard.sh uninstall

# Check status
./tekton/install-dashboard.sh status

# Start proxy (normal)
./tekton/install-dashboard.sh proxy

# Start proxy (auto-restart)
./tekton/install-dashboard.sh proxy-always

# Direct kubectl port-forward
kubectl port-forward -n tekton-pipelines service/tekton-dashboard 9097:9097

# Alternative: kubectl proxy
kubectl proxy --port=8080
# Then: http://localhost:8080/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/
```

## Troubleshooting

### Dashboard shows "No pipelines found"
Make sure you're in the right namespace (goods-price-ci):
```bash
kubectl get pipelines -n goods-price-ci
```

If empty, apply the pipeline:
```bash
kubectl apply -f tekton/pipeline.yaml
```

### "Service not found" error
Dashboard isn't installed:
```bash
./tekton/install-dashboard.sh install
```

### Blank page / Can't load
Try hard refresh: `Cmd+Shift+R` (Mac) or `Ctrl+F5` (Windows)

### Broken pipe errors in terminal
Ignore them - the browser makes keep-alive connections. Dashboard still works.

## Next Steps

- [Setup Kubernetes Dashboard →](04-k8s-dashboard.md) (for viewing cluster resources)
- [Troubleshooting →](99-troubleshooting.md)
- Go back to [Getting Started Overview →](README.md)
