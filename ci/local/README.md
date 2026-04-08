# Local CI/CD with Tekton

Cloud-native CI/CD pipeline using Tekton on Kind (Kubernetes in Docker).

## Directory Structure

```
ci/local/
├── setup.sh              # Master setup script
├── setup/
│   └── start.sh          # Kind cluster setup
├── k8s/                  # Kubernetes infrastructure
│   ├── namespace.yaml
│   ├── registry.yaml     # Local Docker registry (NodePort 32242)
│   ├── deployment.yaml
│   ├── service.yaml
│   └── kustomization.yaml
├── tekton/               # CI/CD pipeline
│   ├── 01-tasks/
│   │   ├── git-clone.yaml
│   │   ├── maven-build.yaml
│   │   ├── docker-build.yaml        # Kaniko → local registry
│   │   ├── docker-build-quay.yaml   # Kaniko → Quay
│   │   ├── deploy.yaml              # Deploy from local registry
│   │   └── deploy-from-registry.yaml # Deploy from external registry
│   ├── config/
│   │   ├── rbac.yaml
│   │   ├── maven-settings-configmap.yaml
│   │   ├── maven-cache-pvc.yaml
│   │   └── registry.yaml
│   ├── pipeline.yaml                # Local registry pipeline
│   ├── pipeline-quay.yaml           # Quay pipeline
│   ├── pipeline-deploy-only.yaml    # Deploy-only (skip build)
│   ├── pipeline-run.yaml            # PipelineRun for local registry
│   ├── pipeline-run-quay.yaml       # PipelineRun for Quay
│   └── kustomization.yaml
└── helpers/              # Utility scripts
    ├── run-pipeline.sh
    ├── run-quay-pipeline.sh
    ├── apply-all.sh
    ├── setup-maven-credentials.sh
    └── setup-quay.sh
```

## Pipeline Flow

```
Clone → Build → Test → Build Image → Push → Deploy
```

1. **git-clone** — Clone source from GitHub
2. **maven-build** — `mvn clean package -DskipTests` (compile only)
3. **maven-test** — `mvn test` (unit tests; use `verify` for integration tests)
4. **kaniko-build** — Build Docker image with Kaniko, save as local tarball
5. **podman-push** — Push tarball to registry with Skopeo
6. **deploy-to-local** — Update Kubernetes deployment

## Quick Start

```bash
# 1. Start Kind cluster
cd ci/local
./setup/start.sh

# 2. Apply all resources
./setup.sh all

# 3. Run the pipeline
./setup.sh run
```

### Individual Commands

```bash
./setup.sh k8s       # Apply K8s resources
./setup.sh tekton    # Apply Tekton pipeline
./setup.sh config    # Apply Maven settings + RBAC
./setup.sh status    # Check pod status
./setup.sh logs      # View app logs
./setup.sh delete    # Delete all resources
```

## Pipelines

### Local Registry (default)

Builds and pushes to in-cluster registry at `<node-ip>:32242`.

```bash
./setup.sh run
# or
kubectl create -f tekton/pipeline-run.yaml
```

### Quay

Pushes to a local Quay instance at `localhost:8080`.

```bash
# Setup Quay first
./helpers/setup-quay.sh setup

# Run pipeline
./helpers/run-quay-pipeline.sh run
# or
kubectl create -f tekton/pipeline-run-quay.yaml
```

### Deploy Only

Deploy a pre-built image without running the build:

```bash
kubectl create -f - <<EOF
apiVersion: tekton.dev/v1
kind: PipelineRun
metadata:
  generateName: deploy-only-
  namespace: goods-price-ci
spec:
  pipelineRef:
    name: deploy-from-registry-pipeline
  params:
    - name: image
      value: "localhost:8080/admin/goods-price-comparison-service:latest"
EOF
```

## Maven Credentials

If your project uses GitHub Packages:

```bash
./helpers/setup-maven-credentials.sh
```

## Troubleshooting

```bash
# Check pipeline run status
kubectl get pipelineruns -n goods-price-ci

# View task logs
kubectl logs -n goods-price-ci -l tekton.dev/pipelineRun=<run-name> --all-containers

# Check app logs
./setup.sh logs

# Access app
kubectl port-forward -n goods-price-ci svc/goods-price-service 8080:8080
```
