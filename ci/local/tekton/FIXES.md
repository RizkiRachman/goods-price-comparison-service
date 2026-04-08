# Fixes for Tekton CI/CD Issues

## ✅ Issue 1: PVC Binding Error - RESOLVED

**Problem:** The pipeline had 2 workspaces defined but deploy task didn't use workspaces, causing Tekton to try binding PVCs for all workspaces.

**Solution:** Updated pipeline to use only one shared workspace. The `pipeline-clean.yaml` and `02-deploy-task.yaml` files implement this fix.

## ✅ Issue 2: Health Probe Returns 404 - RESOLVED

**Problem:** Deployment probes configured with `/actuator/health` but the application doesn't have this endpoint. The app only has root endpoint `/` which returns 200.

**Solution:** Changed health probe paths from `/actuator/health` to `/` in:
- `ci/local/tekton/pipeline.yaml` (lines 346, 355)
- `ci/local/tekton/pipeline-clean.yaml`
- `ci/local/tekton/02-deploy-task.yaml`

## Issue 3: ImagePullBackOff - RESOLVED

**Problem:** Kind cluster's container runtime tried to pull image via HTTPS but registry only serves HTTP.

**Solution:** Set `imagePullPolicy: Always` (or `IfNotPresent`) for local registry images.

## Running the Pipeline

### Quick Start

```bash
# 1. Apply clean pipeline resources
kubectl apply -f ci/local/tekton/pipeline-clean.yaml -n goods-price-ci
kubectl apply -f ci/local/tekton/02-deploy-task.yaml -n goods-price-ci
kubectl apply -f ci/local/tekton/03-pipeline.yaml -n goods-price-ci

# 2. Create PVC
kubectl apply -f - <<EOF
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: tekton-workspace-pvc
  namespace: goods-price-ci
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 2Gi
EOF

# 3. Run pipeline
tkn pipeline start goods-price-service-pipeline \
  --namespace goods-price-ci \
  --workspace name=shared-workspace,claimName=tekton-workspace-pvc \
  --param registry=localhost:5000 \
  --showlog
```

### With GitHub Credentials (for private repos)

```bash
# Create secret
kubectl create secret docker-registry github-maven-credentials \
  --from-literal=username=YOUR_USERNAME \
  --from-literal=token=YOUR_TOKEN \
  --namespace=goods-price-ci
```

## Pipeline Architecture

```
┌─────────┐     ┌───────────┐     ┌──────────────┐     ┌─────────┐
│  Clone  │────▶│   Build   │────▶│ Docker Build │────▶│ Deploy  │
└─────────┘     └───────────┘     └──────────────┘     └─────────┘
    │               │                    │                  │
    └── shared-workspace (PVC) ───────────────────────────────┘
```

## Key Files

- `01-tasks.yaml` - Git clone, Maven build, Docker build tasks
- `02-deploy-task.yaml` - Deploy to Kubernetes task (with health probes fixed)
- `03-pipeline.yaml` - Pipeline definition
- `pipeline-clean.yaml` - Combined version of above
- `workspace-template.yaml` - Volume claim template for dynamic workspaces
