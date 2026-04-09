#!/bin/bash
# Trigger a PipelineRun for the build-deploy pipeline

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOYER_DIR="$(dirname "$SCRIPT_DIR")"

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

PIPELINE_NAMESPACE="${PIPELINE_NAMESPACE:-goods-price-ci}"

log "Creating PipelineRun in namespace: ${PIPELINE_NAMESPACE}"

# Create PipelineRun
envsubst '${PIPELINE_NAMESPACE} ${GIT_REPO_URL} ${GIT_REPO_DEFAULT_BRANCH} ${IMAGE_NAME} ${IMAGE_TAG} ${REGISTRY_KIND_HOST} ${REGISTRY_PORT} ${DEPLOYMENT_NAMESPACE} ${DEPLOYMENT_NAME}' < "$DEPLOYER_DIR/pipelines/pipeline-run.yaml" | kubectl create -f -

log "✅ PipelineRun created!"
log ""
log "To check status:"
log "  kubectl get pipelineruns -n ${PIPELINE_NAMESPACE}"
log ""
log "To view logs:"
log "  tkn pipelinerun logs -f -n ${PIPELINE_NAMESPACE} <pipeline-run-name>"
log ""
log "Or use the Tekton Dashboard at http://localhost:${TEKTON_DASHBOARD_PORT:-9097}"
