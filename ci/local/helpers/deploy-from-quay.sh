#!/bin/bash
# Deploy from Quay using Tekton pipeline
# Trigger deploy-only pipeline after image is pushed to Quay

set -e

NAMESPACE="goods-price-ci"
DEFAULT_QUAY="localhost:8080"

show_help() {
    cat << 'EOF'
Deploy from Quay using Tekton

Usage: ./deploy-from-quay.sh [image-tag]

Examples:
  ./deploy-from-quay.sh latest              # Deploy localhost:8080/admin/app:latest
  ./deploy-from-quay.sh v1.2.3             # Deploy with specific tag
  ./deploy-from-quay.sh                    # Deploy latest

Environment:
  QUAY_URL         Quay URL (default: localhost:8080)
  QUAY_USER        Quay organization/user (default: admin)
  QUAY_REPO        Repository name (default: goods-price-comparison-service)

Prerequisites:
  1. Quay must be running: ./setup-quay.sh status
  2. Image must be pushed to Quay
  3. Pipeline must be applied: ./setup.sh tekton

EOF
}

QUAY_URL="${QUAY_URL:-${DEFAULT_QUAY}}"
QUAY_USER="${QUAY_USER:-admin}"
QUAY_REPO="${QUAY_REPO:-goods-price-comparison-service}"
TAG="${1:-latest}"

# Full image path
FULL_IMAGE="${QUAY_URL}/${QUAY_USER}/${QUAY_REPO}:${TAG}"

echo "🚀 Deploying from Quay: ${FULL_IMAGE}"
echo ""

# Create PipelineRun
cat << EOF | kubectl apply -f -
apiVersion: tekton.dev/v1
kind: PipelineRun
metadata:
  generateName: deploy-from-quay-
  namespace: ${NAMESPACE}
spec:
  pipelineRef:
    name: deploy-from-registry-pipeline
  taskRunTemplate:
    serviceAccountName: tekton-deployer
  workspaces:
    - name: k8s-manifests
      volumeClaimTemplate:
        spec:
          accessModes:
            - ReadWriteOnce
          resources:
            requests:
              storage: 1Gi
  params:
    - name: image
      value: "${FULL_IMAGE}"
    - name: namespace
      value: "${NAMESPACE}"
EOF

echo ""
echo "⏳ PipelineRun created. Monitor with:"
echo "  ./helpers/run-pipeline.sh logs"
echo "  kubectl get pipelinerun -n ${NAMESPACE} -w"
