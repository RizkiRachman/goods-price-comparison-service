# Tekton Deployment Setup

This directory contains Tekton configuration for building and deploying the goods-price-comparison-service to a remote Tekton server.

## Directory Structure

```
deployer/
├── .env.template          # Environment variables template
├── manifests/             # Kubernetes infrastructure manifests
│   ├── namespace.yaml
│   ├── serviceaccount.yaml
│   ├── registry-secret.yaml
│   └── triggers/
│       ├── rbac.yaml
│       └── triggerbinding.yaml
├── tasks/                 # Tekton task definitions
│   ├── git-clone.yaml
│   ├── maven-build.yaml
│   ├── maven-test.yaml
│   ├── docker-build.yaml
│   └── deploy.yaml
├── pipelines/             # Tekton pipeline definitions
│   ├── build-deploy-pipeline.yaml
│   └── pipeline-run.yaml
└── scripts/               # Helper scripts
    ├── apply.sh
    ├── run-pipeline.sh
    ├── status.sh
    └── cleanup.sh
```

## Quick Start

1. **Configure environment variables**
   ```bash
   cp .env.template .env
   # Edit .env with your specific values
   ```

2. **Apply Tekton resources**
   ```bash
   ./scripts/apply.sh
   ```

3. **Run the pipeline**
   ```bash
   ./scripts/run-pipeline.sh
   ```

4. **Check status**
   ```bash
   ./scripts/status.sh
   ```

## Environment Variables

Key variables in `.env`:

- `PIPELINE_NAMESPACE` - Kubernetes namespace for Tekton resources (default: `goods-price-ci`)
- `PIPELINE_SERVICE_ACCOUNT` - Service account for pipeline execution (default: `tekton-sa`)
- `REGISTRY_KIND_HOST` - Container registry hostname
- `REGISTRY_PORT` - Container registry port
- `REGISTRY_USERNAME` - Registry username (optional)
- `REGISTRY_PASSWORD` - Registry password (optional)
- `GIT_REPO_URL` - Git repository URL
- `GIT_REPO_DEFAULT_BRANCH` - Default git branch (default: `main`)
- `IMAGE_NAME` - Docker image name
- `IMAGE_TAG` - Docker image tag (default: `latest`)
- `DEPLOYMENT_NAMESPACE` - Kubernetes namespace for deployment
- `DEPLOYMENT_NAME` - Kubernetes deployment name

## Pipeline Flow

The `goods-price-pipeline` executes the following steps:

1. **git-clone** - Clone source code from Git repository
2. **maven-build** - Build the application with Maven (skip tests)
3. **maven-test** - Run Maven tests
4. **docker-build** - Build and push Docker image using Kaniko
5. **deploy** - Deploy to Kubernetes

## Scripts

### Tekton Scripts
- `apply.sh` - Apply all Tekton resources to the cluster
- `run-pipeline.sh` - Trigger a new PipelineRun
- `status.sh` - Check status of Tekton resources
- `cleanup.sh` - Remove all Tekton resources

### Kubernetes Dashboard Scripts
- `k8s-dashboard.sh` - Manage Kubernetes Dashboard (install/token/start)
  ```bash
  ./scripts/k8s-dashboard.sh install  # Install dashboard
  ./scripts/k8s-dashboard.sh token    # Get login token
  ./scripts/k8s-dashboard.sh start    # Start dashboard
  ```

## Prerequisites

- kubectl configured to connect to your Tekton server
- envsubst installed (for variable substitution)
- Container registry accessible from the Tekton cluster

## Troubleshooting

Check PipelineRun status:
```bash
kubectl get pipelineruns -n goods-price-ci
```

View PipelineRun logs:
```bash
tkn pipelinerun logs -f -n goods-price-ci <pipeline-run-name>
```

Check pod logs:
```bash
kubectl logs -n goods-price-ci -l tekton.dev/pipelineRun=<run-name> --all-containers
```
