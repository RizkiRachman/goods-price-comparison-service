#!/bin/bash
# Run Tekton pipeline: Build → Push to Quay → Deploy from Quay
# All in Tekton with Kaniko build

set -e

NAMESPACE="goods-price-ci"
QUAY_URL="${QUAY_URL:-localhost:8080}"
QUAY_USER="${QUAY_USER:-admin}"

show_help() {
    cat << 'EOF'
Run Tekton Quay Pipeline

Usage: ./run-quay-pipeline.sh [command]

Commands:
  run              Run the full pipeline (build → Quay → deploy)
  status           Check pipeline status
  logs             Follow pipeline logs
  verify           Verify Quay is accessible

Prerequisites:
  1. Quay running: ./setup-quay.sh status
  2. Pipeline applied: kubectl apply -f tekton/pipeline-quay.yaml

Workflow:
  git-clone → maven-build → docker-build (to Quay) → deploy (from Quay)

EOF
}

verify_quay() {
    echo "🔍 Verifying Quay at ${QUAY_URL}..."
    if curl -s "http://${QUAY_URL}/health/instance" > /dev/null 2>&1; then
        echo "✅ Quay is ready"
    else
        echo "❌ Quay not accessible"
        echo "Start with: ./setup-quay.sh setup"
        exit 1
    fi
}

run_pipeline() {
    verify_quay
    
    echo "🚀 Running Quay pipeline..."
    echo "   Quay: ${QUAY_URL}"
    echo "   User: ${QUAY_USER}"
    echo ""
    
    # Apply pipeline if not exists
    kubectl apply -f "$(dirname "$0")/../tekton/pipeline-quay.yaml" -n "${NAMESPACE}" 2>/dev/null || true
    kubectl apply -f "$(dirname "$0")/../tekton/pipeline-run-quay.yaml" -n "${NAMESPACE}"
    
    echo ""
    echo "✅ PipelineRun created"
    echo ""
    echo "Monitor:"
    echo "  ./helpers/run-pipeline.sh logs"
    echo "  kubectl get pipelinerun -n ${NAMESPACE} -w"
    echo ""
    echo "View in Quay:"
    echo "  http://${QUAY_URL}/repository/${QUAY_USER}/goods-price-comparison-service"
}

show_status() {
    echo "📊 Pipeline Status:"
    kubectl get pipelinerun -n "${NAMESPACE}" -l tekton.dev/pipeline=goods-price-service-quay-pipeline --sort-by=.metadata.creationTimestamp
    echo ""
    echo "Quay Images:"
    curl -s "http://${QUAY_URL}/api/v1/repository?last_modified=true" 2>/dev/null | \
        jq -r '.repositories[] | "  " + .name' 2>/dev/null || echo "  Check Quay UI: http://${QUAY_URL}"
}

follow_logs() {
    echo "📋 Following logs..."
    "$(dirname "$0")/run-pipeline.sh" logs
}

# Main
case "${1:-run}" in
    run|start)
        run_pipeline
        ;;
    status)
        show_status
        ;;
    logs)
        follow_logs
        ;;
    verify)
        verify_quay
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo "Unknown: $1"
        show_help
        exit 1
        ;;
esac
