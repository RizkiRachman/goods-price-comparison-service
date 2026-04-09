#!/bin/bash
# Apply all Tekton resources to the cluster
# This script applies tasks, pipelines, and infrastructure manifests

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOYER_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$DEPLOYER_DIR")"

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
log()   { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# Load environment
if [ -f "$DEPLOYER_DIR/.env" ]; then
    export $(grep -v '^#' "$DEPLOYER_DIR/.env" | xargs)
else
    error ".env file not found. Please copy .env.template to .env and configure it."
fi

# Set defaults for optional variables
export KUBECTL_IMAGE="${KUBECTL_IMAGE:-bitnami/kubectl:latest}"
export MAVEN_IMAGE="${MAVEN_IMAGE:-maven:3.9-eclipse-temurin-17}"
export KANIKO_IMAGE="${KANIKO_IMAGE:-gcr.io/kaniko-project/executor:latest}"
export DEPLOYMENT_PORT="${DEPLOYMENT_PORT:-8080}"

# Prerequisite checks
log "Checking prerequisites..."
if ! kubectl cluster-info &>/dev/null; then
    error "Cannot connect to Kubernetes cluster. Please ensure kubectl is configured."
fi

if ! kubectl get crd tasks.tekton.dev &>/dev/null; then
    error "Tekton Pipelines not installed. Please install Tekton Pipelines first."
fi

log "Prerequisites check passed."

# Set defaults
PIPELINE_NAMESPACE="${PIPELINE_NAMESPACE:-goods-price-ci}"
PIPELINE_SERVICE_ACCOUNT="${PIPELINE_SERVICE_ACCOUNT:-tekton-sa}"

MANIFEST_DIR="$DEPLOYER_DIR/manifests"
TASKS_DIR="$DEPLOYER_DIR/tasks"
PIPELINES_DIR="$DEPLOYER_DIR/pipelines"

log "Applying Tekton resources to namespace: ${PIPELINE_NAMESPACE}"

# Create namespace
log "Creating namespace..."
envsubst '${PIPELINE_NAMESPACE}' < "$MANIFEST_DIR/namespace.yaml" | kubectl apply -f -

# Apply service account and RBAC
log "Applying service account and RBAC..."
envsubst '${PIPELINE_NAMESPACE} ${PIPELINE_SERVICE_ACCOUNT}' < "$MANIFEST_DIR/serviceaccount.yaml" | kubectl apply -f -

# Apply registry secret
log "Applying registry secret..."
envsubst '${PIPELINE_NAMESPACE} ${REGISTRY_KIND_HOST} ${REGISTRY_PORT} ${REGISTRY_USERNAME} ${REGISTRY_PASSWORD}' < "$MANIFEST_DIR/registry-secret.yaml" | kubectl apply -f -

# Apply PVCs
log "Applying PVCs..."
PVC_DIR="$DEPLOYER_DIR/pvc"
if [ -f "$PVC_DIR/workspace-pvc.yaml" ]; then
    envsubst '${PIPELINE_NAMESPACE} ${DEPLOYMENT_NAME}' < "$PVC_DIR/workspace-pvc.yaml" | kubectl apply -f -
    log "  Applied: workspace-pvc.yaml"
else
    warn "workspace-pvc.yaml not found, skipping PVC"
fi
if [ -f "$PVC_DIR/maven-cache-pvc.yaml" ]; then
    envsubst '${PIPELINE_NAMESPACE}' < "$PVC_DIR/maven-cache-pvc.yaml" | kubectl apply -f -
    log "  Applied: maven-cache-pvc.yaml"
else
    warn "maven-cache-pvc.yaml not found, skipping Maven cache PVC"
fi

# Create GitHub Maven credentials secret if it doesn't exist
log "Checking github-maven-credentials secret..."
if ! kubectl get secret github-maven-credentials -n "$PIPELINE_NAMESPACE" &>/dev/null; then
    warn "github-maven-credentials secret not found. Please create it manually:"
    warn "  kubectl create secret generic github-maven-credentials --from-literal=username=<username> --from-literal=token=<token> -n $PIPELINE_NAMESPACE"
else
    log "github-maven-credentials secret exists"
fi

# Create maven-settings-secret from github-maven-credentials
log "Creating maven-settings-secret..."
if [ -f "$SCRIPT_DIR/create-maven-settings-secret.sh" ]; then
    "$SCRIPT_DIR/create-maven-settings-secret.sh" || warn "Failed to create maven-settings-secret"
else
    warn "create-maven-settings-secret.sh not found, skipping"
fi

# Apply triggers RBAC (only if Tekton Triggers is installed)
log "Checking for Tekton Triggers..."
if kubectl get crd triggerbindings.triggers.tekton.dev &>/dev/null; then
    log "Tekton Triggers found, applying triggers RBAC..."
    envsubst '${PIPELINE_NAMESPACE}' < "$MANIFEST_DIR/triggers/rbac.yaml" | kubectl apply -f -
    envsubst '${PIPELINE_NAMESPACE}' < "$MANIFEST_DIR/triggers/triggerbinding.yaml" | kubectl apply -f -
else
    warn "Tekton Triggers CRD not found, skipping trigger resources"
    warn "Triggers are optional - you can still run pipelines manually"
fi

# Apply tasks
log "Applying Tekton tasks..."
for task in "$TASKS_DIR"/*.yaml; do
    if [ -f "$task" ]; then
        envsubst '${PIPELINE_NAMESPACE} ${DEPLOYMENT_NAMESPACE} ${DEPLOYMENT_NAME} ${DEPLOYMENT_PORT} ${KUBECTL_IMAGE} ${MAVEN_IMAGE} ${KANIKO_IMAGE}' < "$task" | kubectl apply -f -
        log "  Applied: $(basename "$task")"
    fi
done

# Install git-clone task from Tekton catalog
log "Installing git-clone task from Tekton catalog..."
kubectl apply -n "${PIPELINE_NAMESPACE}" -f https://raw.githubusercontent.com/tektoncd/catalog/main/task/git-clone/0.9/git-clone.yaml || warn "Failed to install git-clone task"

# Apply pipelines (skip pipeline-run files as they use generateName)
log "Applying Tekton pipelines..."
for pipeline in "$PIPELINES_DIR"/*.yaml; do
    if [ -f "$pipeline" ]; then
        # Skip pipeline-run files as they use generateName and must be created with kubectl create
        if [[ "$(basename "$pipeline")" == *pipeline-run* ]]; then
            log "  Skipping: $(basename "$pipeline") (use run-pipeline.sh to create)"
            continue
        fi
        envsubst '${PIPELINE_NAMESPACE} ${GIT_REPO_URL} ${GIT_REPO_DEFAULT_BRANCH} ${IMAGE_NAME} ${IMAGE_TAG} ${REGISTRY_KIND_HOST} ${REGISTRY_PORT} ${DEPLOYMENT_NAMESPACE} ${DEPLOYMENT_NAME}' < "$pipeline" | kubectl apply -f -
        log "  Applied: $(basename "$pipeline")"
    fi
done

log "✅ All Tekton resources applied successfully!"
log ""
log "To run the pipeline, execute:"
log "  ./scripts/run-pipeline.sh"
