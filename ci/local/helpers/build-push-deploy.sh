#!/bin/bash
# Local Podman Build → Quay → Deploy Workflow
# Build image locally, push to Quay, deploy from Quay

set -e

APP_NAME="goods-price-comparison-service"
QUAY_URL="${QUAY_URL:-localhost:8080}"
NAMESPACE="goods-price-ci"

show_help() {
    cat << 'EOF'
Podman Build → Quay → Deploy Workflow

Usage: ./build-push-deploy.sh [command] [options]

Commands:
  build               Build image with Podman (local)
  push [tag]          Push local image to Quay
  deploy [tag]        Deploy from Quay (skip build)
  all [tag]           Build + Push + Deploy (full workflow)
  verify              Verify Quay is accessible

Examples:
  ./build-push-deploy.sh build                    # Just build locally
  ./build-push-deploy.sh push latest              # Push to Quay
  ./build-push-deploy.sh deploy latest            # Deploy from Quay
  ./build-push-deploy.sh all v1.2.3               # Full workflow

Environment:
  QUAY_URL            Quay registry URL (default: localhost:8080)
  QUAY_USER           Quay username (optional)
  QUAY_PASSWORD       Quay password (optional)

Full Workflow:
  1. Build: podman build -t ${APP_NAME}:${TAG}
  2. Tag:   podman tag ${APP_NAME}:${TAG} ${QUAY_URL}/${USER}/${APP_NAME}:${TAG}
  3. Push: podman push ${QUAY_URL}/${USER}/${APP_NAME}:${TAG}
  4. Deploy: kubectl set image deployment/... ${QUAY_URL}/${USER}/${APP_NAME}:${TAG}

EOF
}

# Verify Quay is running
verify_quay() {
    echo "🔍 Verifying Quay at ${QUAY_URL}..."
    
    if curl -s "http://${QUAY_URL}/health/instance" > /dev/null 2>&1; then
        echo "✅ Quay is healthy"
    else
        echo "❌ Quay not accessible at ${QUAY_URL}"
        echo "Start Quay: ./setup-quay.sh setup"
        exit 1
    fi
}

# Build locally with Podman
build_local() {
    local tag="${1:-latest}"
    
    echo "🔨 Building ${APP_NAME}:${tag} with Podman..."
    
    cd "$(dirname "$0")/../.."
    
    podman build -t "${APP_NAME}:${tag}" -f Dockerfile .
    
    echo "✅ Built: ${APP_NAME}:${tag}"
    podman images | grep "${APP_NAME}"
}

# Push to Quay
push_quay() {
    local tag="${1:-latest}"
    local quay_user="${QUAY_USER:-admin}"
    local quay_repo="${QUAY_REPO:-${APP_NAME}}"
    local full_image="${QUAY_URL}/${quay_user}/${quay_repo}:${tag}"
    
    verify_quay
    
    echo "📤 Pushing to Quay: ${full_image}"
    
    # Tag for Quay
    podman tag "${APP_NAME}:${tag}" "${full_image}"
    
    # Push (insecure for local Quay)
    if [ -n "$QUAY_PASSWORD" ]; then
        # Login if credentials provided
        podman login "${QUAY_URL}" -u "${quay_user}" -p "${QUAY_PASSWORD}" --tls-verify=false 2>/dev/null || true
    fi
    
    podman push "${full_image}" --tls-verify=false
    
    echo "✅ Pushed to Quay"
    echo "🌐 View at: http://${QUAY_URL}/repository/${quay_user}/${quay_repo}"
}

# Deploy from Quay
deploy_from_quay() {
    local tag="${1:-latest}"
    local quay_user="${QUAY_USER:-admin}"
    local quay_repo="${QUAY_REPO:-${APP_NAME}}"
    local full_image="${QUAY_URL}/${quay_user}/${quay_repo}:${tag}"
    
    verify_quay
    
    echo "🚀 Deploying from Quay: ${full_image}"
    
    # Update deployment image
    kubectl set image deployment/goods-price-service \
        app="${full_image}" \
        -n "${NAMESPACE}"
    
    # Set pull policy to Always (for registry)
    kubectl patch deployment/goods-price-service -n "${NAMESPACE}" -p \
        '{"spec":{"template":{"spec":{"containers":[{"name":"app","imagePullPolicy":"Always"}]}}}}'
    
    # Restart deployment
    kubectl rollout restart deployment/goods-price-service -n "${NAMESPACE}"
    
    echo "⏳ Waiting for rollout..."
    kubectl rollout status deployment/goods-price-service -n "${NAMESPACE}" --timeout=300s
    
    echo "✅ Deployed from Quay"
}

# Full workflow
run_all() {
    local tag="${1:-latest}"
    
    echo "🔄 Running full workflow: Build → Push → Deploy"
    echo ""
    
    build_local "${tag}"
    echo ""
    
    push_quay "${tag}"
    echo ""
    
    deploy_from_quay "${tag}"
    
    echo ""
    echo "═══════════════════════════════════════"
    echo "  ✅ Complete!"
    echo "═══════════════════════════════════════"
    echo "Image: ${APP_NAME}:${tag}"
    echo "Quay:  ${QUAY_URL}"
    echo ""
    echo "View logs: ./setup.sh logs"
}

# Check status
show_status() {
    echo "📊 Status:"
    echo ""
    echo "Local Podman images:"
    podman images | grep "${APP_NAME}" || echo "  No local images"
    echo ""
    echo "Quay repository:"
    curl -s "http://${QUAY_URL}/api/v1/repository?last_modified=true" 2>/dev/null | \
        jq -r '.repositories[].name' 2>/dev/null | grep "${APP_NAME}" || echo "  Not in Quay yet"
    echo ""
    echo "Deployment:"
    kubectl get deployment goods-price-service -n "${NAMESPACE}" -o jsonpath='{.spec.template.spec.containers[0].image}' 2>/dev/null || echo "  Not deployed"
}

# Main
case "${1:-help}" in
    build)
        build_local "$2"
        ;;
    push)
        push_quay "$2"
        ;;
    deploy)
        deploy_from_quay "$2"
        ;;
    all|run)
        run_all "$2"
        ;;
    verify)
        verify_quay
        ;;
    status)
        show_status
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
