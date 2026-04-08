#!/bin/bash
# Post-Build Sync: Automatically pull images from Kind registry to Podman
# Run this after pipeline completes to sync images

set -e

REGISTRY_URL="10.89.0.2:32242"
NAMESPACE="goods-price-ci"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

show_help() {
    cat << 'EOF'
Post-Build Sync: Sync images from Kind registry to local Podman

Usage: ./post-build-sync.sh [command]

Commands:
  auto              Auto-detect and sync latest pipeline image
  sync <image>      Sync specific image from registry to Podman
  all               Sync all images from registry
  deploy <image>    Load image from Podman into Kind and deploy

Examples:
  ./post-build-sync.sh auto
  ./post-build-sync.sh sync goods-price-comparison-service:latest
  ./post-build-sync.sh deploy goods-price-comparison-service:latest

Workflow:
  1. Pipeline builds → pushes to Kind registry
  2. Run this script → pulls to Podman
  3. Optional: Load from Podman back to Kind for deploy
EOF
}

# Get latest image from latest PipelineRun
get_latest_image() {
    local run_name=$(kubectl get pipelinerun -n "${NAMESPACE}" --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1].metadata.name}' 2>/dev/null)
    if [ -z "$run_name" ]; then
        error "No PipelineRun found"
        exit 1
    fi
    
    # Extract image from pipeline results or params
    local image=$(kubectl get pipelinerun "${run_name}" -n "${NAMESPACE}" -o jsonpath='{.spec.params[?(@.name=="image-name")].value}' 2>/dev/null)
    local registry=$(kubectl get pipelinerun "${run_name}" -n "${NAMESPACE}" -o jsonpath='{.spec.params[?(@.name=="registry")].value}' 2>/dev/null)
    
    if [ -z "$image" ]; then
        image="goods-price-comparison-service"
    fi
    if [ -z "$registry" ]; then
        registry="${REGISTRY_URL}"
    fi
    
    echo "${registry}/${image}:${run_name}"
}

# Pull from Kind registry to Podman
sync_to_podman() {
    local full_image="$1"
    local image_name=$(echo "$full_image" | sed 's|.*/||' | cut -d: -f1)
    local tag=$(echo "$full_image" | grep -o ':[^:]*$' | sed 's/^://')
    
    log "Pulling ${full_image} to Podman..."
    
    # Pull from registry
    podman pull "${full_image}" --tls-verify=false
    
    # Tag with clean local name
    podman tag "${full_image}" "localhost/${image_name}:${tag}"
    podman tag "${full_image}" "localhost/${image_name}:latest"
    
    log "✅ Available in Podman as:"
    log "  localhost/${image_name}:${tag}"
    log "  localhost/${image_name}:latest"
}

# Load from Podman into Kind (for deploy)
deploy_from_podman() {
    local image_name="$1"
    local tag="${2:-latest}"
    
    log "Loading ${image_name}:${tag} from Podman into Kind..."
    
    # Save from Podman and load into Kind
    podman save "localhost/${image_name}:${tag}" -o "/tmp/${image_name}.tar"
    kind load image-archive "/tmp/${image_name}.tar" --name goods-price-ci 2>/dev/null || \
        docker exec goods-price-ci-control-plane ctr -n k8s.io images import "/tmp/${image_name}.tar"
    rm -f "/tmp/${image_name}.tar"
    
    # Deploy with Never pull policy (image already in Kind)
    kubectl set image deployment/goods-price-service app="localhost/${image_name}:${tag}" -n "${NAMESPACE}"
    kubectl patch deployment/goods-price-service -n "${NAMESPACE}" -p '{"spec":{"template":{"spec":{"containers":[{"name":"app","imagePullPolicy":"Never"}]}}}}'
    kubectl rollout restart deployment/goods-price-service -n "${NAMESPACE}"
    
    log "✅ Deployed from Podman with imagePullPolicy: Never"
}

# Sync all images from registry
sync_all() {
    log "Syncing all images from Kind registry to Podman..."
    
    local repos=$(curl -s "http://${REGISTRY_URL}/v2/_catalog" | jq -r '.repositories[]' 2>/dev/null)
    
    for repo in $repos; do
        local tags=$(curl -s "http://${REGISTRY_URL}/v2/${repo}/tags/list" | jq -r '.tags[]' 2>/dev/null)
        for tag in $tags; do
            log "Syncing: ${repo}:${tag}"
            sync_to_podman "${REGISTRY_URL}/${repo}:${tag}"
        done
    done
}

# Auto-sync latest pipeline image
auto_sync() {
    local latest=$(get_latest_image)
    log "Latest pipeline image: ${latest}"
    sync_to_podman "${latest}"
}

# Main
case "${1:-help}" in
    auto) auto_sync ;;
    sync) 
        if [ -z "$2" ]; then
            error "Usage: sync <image-name:tag>"
            exit 1
        fi
        sync_to_podman "$2"
        ;;
    all) sync_all ;;
    deploy)
        if [ -z "$2" ]; then
            error "Usage: deploy <image-name> [tag]"
            exit 1
        fi
        deploy_from_podman "$2" "$3"
        ;;
    help|--help|-h) show_help ;;
    *) error "Unknown command: $1"; show_help; exit 1 ;;
esac
