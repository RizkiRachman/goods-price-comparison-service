# 03 - Tekton Stack Setup (Kubernetes)

This is the **modern, cloud-native path**. Tekton runs on a local Kubernetes cluster using kind.

## What Gets Started?

| Component | Description |
|-----------|-------------|
| kind cluster | Local Kubernetes cluster in Docker |
| Tekton Pipelines | Cloud-native CI/CD engine |
| Tekton Triggers | Webhook triggers for pipelines |
| Tekton Dashboard | Web UI for viewing pipelines |
| Your Application | Deployed inside the cluster |

**Note:** With Tekton, most services run INSIDE the cluster, not directly on localhost.

## Step-by-Step Setup

### Step 1: Prerequisites

Make sure you have:
- Docker Desktop running
- kubectl installed
- kind installed
- tkn (Tekton CLI) installed

[Install Prerequisites →](01-prerequisites.md)

### Step 2: Start the Tekton Stack

```bash
cd /Users/rizkirachman/IdeaProjects/goods-price-comparison-service/ci/local
./start.sh tekton
```

This will:
1. Create a kind cluster named "goods-price-ci"
2. Install Tekton Pipelines
3. Install Tekton Triggers
4. Create the `goods-price-ci` namespace
5. Apply pipeline configuration

**First time startup takes 3-5 minutes** as it downloads images.

### Step 3: Verify Installation

```bash
# Check Tekton pods
kubectl get pods -n tekton-pipelines

# Should show pods like:
# tekton-pipelines-controller
# tekton-pipelines-webhook

# Check your namespace
kubectl get namespace goods-price-ci
```

### Step 4: Install Tekton Dashboard

```bash
./tekton/install-dashboard.sh install
```

Wait for installation to complete.

### Step 5: Access Tekton Dashboard

**Option A: Port-forward (recommended)**
```bash
./tekton/install-dashboard.sh proxy
```

**Option B: Auto-restart mode**
```bash
./tekton/install-dashboard.sh proxy-always
```

Then open: http://localhost:9097

**No token needed!** Tekton Dashboard doesn't require authentication in read-only mode.

## Running Your First Pipeline

### Option 1: Using PipelineRun (Easiest)

```bash
kubectl create -f tekton/pipeline-run.yaml
```

### Option 2: Using tkn CLI

```bash
tkn pipeline start goods-price-service-pipeline \
  --showlog \
  -n goods-price-ci \
  -w name=shared-workspace,volumeClaimTemplateFile=tekton/workspace-template.yaml \
  -w name=maven-cache,claimName=maven-cache-pvc
```

### Monitor the Pipeline

**In Dashboard:**
- Go to http://localhost:9097
- Click "PipelineRuns" in left menu
- Watch your pipeline execute in real-time

**In Terminal:**
```bash
# List pipeline runs
tkn pipelinerun list -n goods-price-ci

# View logs
tkn pipelinerun logs -f -n goods-price-ci <pipeline-run-name>

# Or use kubectl
kubectl get pipelinerun -n goods-price-ci
```

## Pipeline Stages

Your build goes through:

```
1. git-clone     → Clone code from GitHub
2. maven-build   → Compile and test with Maven
3. docker-build  → Build image with Kaniko
4. deploy        → Deploy to cluster
```

## Accessing Your Deployed App

### Port-forward to the app
```bash
kubectl port-forward -n goods-price-ci service/goods-price-service 8080:8080
```

Then visit: http://localhost:8080

### Check app is running
```bash
kubectl get pods -n goods-price-ci
kubectl logs -n goods-price-ci -l app=goods-price-service
```

## Common Operations

### Stop the Cluster
```bash
./start.sh tekton-stop
```

This deletes the entire kind cluster (including all data).

### View Pipeline Status
```bash
tkn pipelinerun list -n goods-price-ci
tkn taskrun list -n goods-price-ci
```

### View Pod Logs
```bash
# Maven build logs
kubectl logs -n goods-price-ci <maven-build-pod>

# App logs
kubectl logs -n goods-price-ci -l app=goods-price-service
```

### Delete a PipelineRun
```bash
kubectl delete pipelinerun -n goods-price-ci <name>
```

## Understanding the Architecture

```
┌─────────────────────────────────────┐
│        kind cluster (Docker)        │
│  ┌───────────────────────────────┐  │
│  │  goods-price-ci namespace   │  │
│  │  ┌───────────────────────┐  │  │
│  │  │   PipelineRun         │  │  │
│  │  │  ┌──────┐ ┌──────┐   │  │  │
│  │  │  │clone │ │build │   │  │  │
│  │  │  └──┬───┘ └──┬───┘   │  │  │
│  │  │     └────┬───┘       │  │  │
│  │  │     ┌────┴───┐       │  │  │
│  │  │     │ docker │       │  │  │
│  │  │     └───┬────┘       │  │  │
│  │  │     ┌───┴────┐       │  │  │
│  │  │     │ deploy │────┐  │  │  │
│  │  │     └────────┘    │  │  │  │
│  │  └───────────────────┼──┘  │  │
│  └───────────────────────┼─────┘  │
│                         │        │
│  ┌───────────────────────┴────┐   │
│  │  Deployment (your app)     │   │
│  │  Pod: goods-price-service  │   │
│  └────────────────────────────┘   │
└─────────────────────────────────────┘
```

## Troubleshooting Tekton

### PipelineRun stuck
```bash
# Check status
kubectl describe pipelinerun -n goods-price-ci <name>

# Check task runs
kubectl get taskrun -n goods-price-ci
kubectl describe taskrun -n goods-price-ci <name>
```

### Can't access dashboard
```bash
# Check dashboard pod
kubectl get pods -n tekton-pipelines -l app.kubernetes.io/part-of=tekton-dashboard

# Check logs
kubectl logs -n tekton-pipelines -l app.kubernetes.io/part-of=tekton-dashboard
```

### Build fails (Maven)
```bash
# Check Maven cache PVC exists
kubectl get pvc maven-cache-pvc -n goods-price-ci

# If missing:
kubectl apply -f tekton/maven-cache-pvc.yaml
```

### kind cluster issues
```bash
# List clusters
kind get clusters

# Delete and recreate
kind delete cluster --name goods-price-ci
./start.sh tekton
```

## Next Steps

- [Access Tekton Dashboard →](05-tekton-dashboard.md)
- [Setup Kubernetes Dashboard →](04-k8s-dashboard.md)
- [Troubleshooting →](99-troubleshooting.md)

---

**Want the easier Docker Compose approach?** [Jenkins Setup →](02-jenkins-setup.md)
