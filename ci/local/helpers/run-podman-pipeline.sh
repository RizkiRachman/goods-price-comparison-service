#!/bin/bash
# Trigger the Podman pipeline (build + push to local Podman registry)
# Prerequisite: ./helpers/setup-podman-registry.sh setup

set -e

NAMESPACE="goods-price-ci"
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
log()   { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

command -v kubectl &>/dev/null || error "kubectl not found"

# Resolve Podman registry endpoint
if [[ "$OSTYPE" == "darwin"* ]]; then
    PODMAN_REGISTRY="host.docker.internal:5000"
else
    HOST_IP=$(ip route get 1 2>/dev/null | awk '{print $7; exit}' || echo "localhost")
    PODMAN_REGISTRY="${HOST_IP}:5000"
fi

IMAGE_TAG="${1:-latest}"

log "Triggering Podman pipeline..."
log "  Registry : $PODMAN_REGISTRY"
log "  Tag      : $IMAGE_TAG"

# Check registry is up
if ! curl -sf "http://localhost:5000/v2/_catalog" > /dev/null 2>&1; then
    warn "Podman registry not detected at localhost:5000"
    warn "Start it first: ./helpers/setup-podman-registry.sh setup"
fi

# Delete previous runs (keep last 3)
OLD_RUNS=$(kubectl get pipelineruns -n "$NAMESPACE" \
    --selector=tekton.dev/pipeline=goods-price-service-podman-pipeline \
    --sort-by=.metadata.creationTimestamp -o name 2>/dev/null | head -n -3)
if [ -n "$OLD_RUNS" ]; then
    log "Cleaning up old runs..."
    echo "$OLD_RUNS" | xargs kubectl delete -n "$NAMESPACE" 2>/dev/null || true
fi

# Create new PipelineRun
RUN_NAME=$(kubectl create -n "$NAMESPACE" -f - <<EOF | awk '{print $1}'
apiVersion: tekton.dev/v1
kind: PipelineRun
metadata:
  generateName: goods-price-podman-
  namespace: $NAMESPACE
spec:
  pipelineRef:
    name: goods-price-service-podman-pipeline
  taskRunTemplate:
    serviceAccountName: tekton-deployer
  workspaces:
    - name: shared-workspace
      volumeClaimTemplate:
        spec:
          accessModes: [ReadWriteOnce]
          resources:
            requests:
              storage: 1Gi
    - name: maven-settings
      configMap:
        name: maven-settings
  params:
    - name: git-url
      value: "https://github.com/RizkiRachman/goods-price-comparison-service.git"
    - name: git-revision
      value: "main"
    - name: image-name
      value: "goods-price-comparison-service"
    - name: podman-registry
      value: "$PODMAN_REGISTRY"
    - name: image-tag
      value: "$IMAGE_TAG"
EOF
)

log "✅ Pipeline started: $RUN_NAME"
log ""
log "Follow logs:   kubectl logs -n $NAMESPACE -l tekton.dev/pipelineRun=${RUN_NAME#*/} --all-containers -f"
log "Watch status:  kubectl get pipelineruns -n $NAMESPACE -w"
log ""
log "After push, pull locally:"
log "  podman pull localhost:5000/goods-price-comparison-service:$IMAGE_TAG --tls-verify=false"
