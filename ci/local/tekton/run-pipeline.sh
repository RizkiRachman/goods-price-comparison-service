#!/bin/bash

# Pipeline Runner Script
# Applies pipeline config, cleans up old runs, and creates a new PipelineRun

set -e

NAMESPACE="goods-price-ci"
PIPELINE_FILE="${PIPELINE_FILE:-tekton/pipeline.yaml}"
PIPELINE_RUN_FILE="${PIPELINE_RUN_FILE:-tekton/pipeline-run.yaml}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

show_help() {
    echo "Pipeline Runner Script"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  run       Apply pipeline, delete old runs, and create new run (default)"
    echo "  start     Start pipeline with tkn CLI and workspaces (interactive)"
    echo "  apply     Apply pipeline configuration only"
    echo "  delete    Delete all PipelineRuns and TaskRuns"
    echo "  logs      Follow logs of the latest PipelineRun"
    echo "  status    Show pipeline and run status"
    echo "  help      Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  PIPELINE_FILE       Path to pipeline YAML (default: tekton/pipeline.yaml)"
    echo "  PIPELINE_RUN_FILE   Path to pipeline-run YAML (default: tekton/pipeline-run.yaml)"
    echo ""
    echo "Examples:"
    echo "  $0 run              # Full reset and run using YAML file"
    echo "  $0                  # Same as 'run'"
    echo "  $0 start            # Start with tkn CLI (includes maven-settings workspace)"
    echo "  $0 apply            # Just apply pipeline changes"
    echo "  $0 logs             # Watch current run logs"
}

apply_pipeline() {
    log "Applying pipeline configuration from ${PIPELINE_FILE}..."
    kubectl apply -f "${PIPELINE_FILE}"
    log "Pipeline applied successfully"
}

delete_runs() {
    warn "Deleting all PipelineRuns in ${NAMESPACE}..."
    kubectl delete pipelinerun --all -n "${NAMESPACE}" 2>/dev/null || true
    
    warn "Deleting all TaskRuns in ${NAMESPACE}..."
    kubectl delete taskrun --all -n "${NAMESPACE}" 2>/dev/null || true
    
    log "Cleanup complete"
}

create_run() {
    log "Creating new PipelineRun from ${PIPELINE_RUN_FILE}..."
    kubectl create -f "${PIPELINE_RUN_FILE}" -n "${NAMESPACE}"
    log "PipelineRun created"
    
    # Get the name of the created run
    sleep 2
    local run_name
    run_name=$(kubectl get pipelinerun -n "${NAMESPACE}" --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1].metadata.name}' 2>/dev/null)
    if [ -n "$run_name" ]; then
        log "PipelineRun name: ${run_name}"
        echo ""
        echo "Watch with: tkn pipelinerun logs -f ${run_name} -n ${NAMESPACE}"
        echo "Or check dashboard: http://localhost:8081/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/"
    fi
}

start_pipeline() {
    log "Starting pipeline with tkn CLI..."
    
    # Get script directory for workspace template path
    local script_dir
    script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    
    log "Using workspaces: shared-workspace (PVC) and maven-settings (ConfigMap)"
    
    tkn pipeline start goods-price-service-pipeline \
        --namespace="${NAMESPACE}" \
        --workspace name=shared-workspace,volumeClaimTemplateFile="${script_dir}/workspace-template.yaml" \
        --workspace name=maven-settings,config=maven-settings \
        --use-param-defaults \
        --showlog
}

follow_logs() {
    log "Following logs of latest PipelineRun..."
    local run_name
    run_name=$(kubectl get pipelinerun -n "${NAMESPACE}" --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1].metadata.name}' 2>/dev/null)
    
    if [ -z "$run_name" ]; then
        error "No PipelineRuns found in ${NAMESPACE}"
        exit 1
    fi
    
    log "Following logs for: ${run_name}"
    tkn pipelinerun logs -f "${run_name}" -n "${NAMESPACE}"
}

show_status() {
    log "Pipeline status:"
    kubectl get pipeline -n "${NAMESPACE}"
    echo ""
    log "PipelineRun status:"
    kubectl get pipelinerun -n "${NAMESPACE}"
    echo ""
    log "TaskRun status:"
    kubectl get taskrun -n "${NAMESPACE}"
}

run_all() {
    apply_pipeline
    delete_runs
    sleep 2
    create_run
    echo ""
    log "Pipeline started! Check dashboard or run '$0 logs' to follow."
}

# Main
case "${1:-run}" in
    run|start)
        run_all
        ;;
    tkn|cli)
        start_pipeline
        ;;
    apply)
        apply_pipeline
        ;;
    delete|clean|cleanup)
        delete_runs
        ;;
    logs|log|follow)
        follow_logs
        ;;
    status)
        show_status
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
