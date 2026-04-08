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
BLUE='\033[0;34m'
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

# Copy command to clipboard
copy_to_clipboard() {
    if command -v pbcopy &> /dev/null; then
        echo -n "$1" | pbcopy
        echo -e "${GREEN}✓ Copied${NC}"
    elif command -v xclip &> /dev/null; then
        echo -n "$1" | xclip -selection clipboard
        echo -e "${GREEN}✓ Copied${NC}"
    elif command -v xsel &> /dev/null; then
        echo -n "$1" | xsel --clipboard --input
        echo -e "${GREEN}✓ Copied${NC}"
    else
        warn "Clipboard not available"
    fi
}

# Display command with copy button
show_command() {
    local description="$1"
    local command="$2"
    echo ""
    echo -e "${BLUE}${description}${NC}"
    echo -e "${GREEN}${command}${NC}"
    echo -n "  [Copy] "
    copy_to_clipboard "$command"
}

show_help() {
    echo "Pipeline Runner Script"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  run              Apply pipeline, delete old runs, and create new run (default)"
    echo "  start            Start pipeline with tkn CLI and workspaces (interactive)"
    echo "  apply            Apply pipeline configuration only"
    echo "  delete           Delete all PipelineRuns and TaskRuns"
    echo "  logs             Follow logs of the latest PipelineRun"
    echo "  status           Show pipeline and run status"
    echo "  pods             Show pod status with errors highlighted"
    echo "  diagnose         Diagnose deployment stuck issue (RECOMMENDED)"
    echo "  fix              Quick fix for stuck deployment"
    echo "  pods-clean       Clean up error/failed/stuck pods (shows commands with copy)"
    echo "  auto-cleanup     Automatically clean up stuck/unused pods"
    echo "  delete-pod       Delete specific pod with confirmation"
    echo "  cleanup-summary  Show pod cleanup summary and quick actions"
    echo "  force-cleanup    Force delete ALL stuck pods (nuclear option)"
    echo "  restart-deploy   Restart goods-price-service deployment"
    echo "  scale-deploy     Scale goods-price-service deployment"
    echo "  resources        Show cluster resource usage"
    echo "  logs-deploy      Show logs for all goods-price-service pods"
    echo "  maven-verify     Verify Maven GitHub credentials setup"
    echo "  maven-setup      Interactive setup for Maven GitHub credentials"
    echo "  setup            Apply supporting resources (ConfigMaps, PVCs, Secrets)"
    echo "  check            Run pre-flight checks only"
    echo "  help             Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  PIPELINE_FILE       Path to pipeline YAML (default: tekton/pipeline.yaml)"
    echo "  PIPELINE_RUN_FILE   Path to pipeline-run YAML (default: tekton/pipeline-run.yaml)"
    echo ""
    echo "Examples:"
    echo "  $0 run                          # Full reset and run"
    echo "  $0 diagnose                     # Check deployment issues"
    echo "  $0 auto-cleanup                 # Auto clean stuck pods"
    echo "  $0 delete-pod my-pod-name       # Delete specific pod"
    echo "  $0 cleanup-summary              # View cleanup summary"
    echo "  $0 get-pod goods-price-service-abc123  # Get pod details"
    echo "  $0 search-pod clone             # Search pods with 'clone'"
    echo "  $0 pods-clean                   # Show cleanup commands"
    echo "  $0 restart-deploy               # Restart deployment"
    echo "  $0 scale-deploy 3               # Scale to 3 replicas"
    echo "  $0 resources                    # Show resource usage"
    echo "  $0 logs-deploy 100              # Show last 100 lines of logs"
    echo "  $0 maven-verify                 # Verify Maven credentials"
    echo "  $0 maven-setup                  # Setup Maven credentials"
}

# Preflight checks - verify and auto-apply missing resources
preflight_checks() {
    log "Running pre-flight checks..."
    local script_dir
    script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    local missing_resources=0

    # Check namespace exists
    if ! kubectl get namespace "${NAMESPACE}" &>/dev/null; then
        warn "Namespace '${NAMESPACE}' not found. Creating..."
        kubectl create namespace "${NAMESPACE}"
        log "✓ Namespace created"
    fi

    # Check maven-settings ConfigMap
    if ! kubectl get configmap maven-settings -n "${NAMESPACE}" &>/dev/null; then
        warn "ConfigMap 'maven-settings' not found"
        if [ -f "${script_dir}/maven-settings-configmap.yaml" ]; then
            log "Auto-applying maven-settings-configmap.yaml..."
            kubectl apply -f "${script_dir}/maven-settings-configmap.yaml"
            log "✓ maven-settings ConfigMap applied"
        else
            error "maven-settings-configmap.yaml not found in ${script_dir}"
            missing_resources=$((missing_resources + 1))
        fi
    else
        log "✓ maven-settings ConfigMap exists"
    fi

    # Check maven-cache PVC
    if ! kubectl get pvc maven-cache -n "${NAMESPACE}" &>/dev/null; then
        warn "PVC 'maven-cache' not found"
        if [ -f "${script_dir}/maven-cache-pvc.yaml" ]; then
            log "Auto-applying maven-cache-pvc.yaml..."
            kubectl apply -f "${script_dir}/maven-cache-pvc.yaml"
            log "✓ maven-cache PVC applied"
        else
            warn "maven-cache-pvc.yaml not found, pipeline may use emptyDir for cache"
        fi
    else
        log "✓ maven-cache PVC exists"
    fi

    # Check github-maven-credentials secret
    if ! kubectl get secret github-maven-credentials -n "${NAMESPACE}" &>/dev/null; then
        warn "Secret 'github-maven-credentials' not found"
        if [ -f "${script_dir}/setup-maven-credentials.sh" ]; then
            log "Run: ${script_dir}/setup-maven-credentials.sh"
        fi
        warn "Pipeline may fail without GitHub credentials for private repos"
    else
        log "✓ github-maven-credentials secret exists"
    fi

    # Check and auto-deploy registry
    if ! kubectl get svc registry -n "${NAMESPACE}" &>/dev/null; then
        warn "Registry service not found"
        if [ -f "${script_dir}/registry.yaml" ]; then
            log "Auto-applying registry.yaml..."
            kubectl apply -f "${script_dir}/registry.yaml"
            log "Waiting for registry to be ready..."
            kubectl wait --for=condition=ready pod -l app=registry -n "${NAMESPACE}" --timeout=60s 2>/dev/null || sleep 5
            log "✓ Registry deployed"
        else
            warn "registry.yaml not found - docker-build may fail"
            missing_resources=$((missing_resources + 1))
        fi
    else
        log "✓ Registry service exists"
    fi

    # Check and auto-apply RBAC for deploy task
    if ! kubectl get serviceaccount tekton-deployer -n "${NAMESPACE}" &>/dev/null; then
        warn "ServiceAccount 'tekton-deployer' not found"
        if [ -f "${script_dir}/rbac.yaml" ]; then
            log "Auto-applying rbac.yaml..."
            kubectl apply -f "${script_dir}/rbac.yaml"
            log "✓ RBAC resources applied"
        else
            warn "rbac.yaml not found - deploy task may fail with permission errors"
            missing_resources=$((missing_resources + 1))
        fi
    else
        log "✓ RBAC ServiceAccount exists"
    fi

    # Check for stuck pods from previous runs
    local stuck_pods
    stuck_pods=$(kubectl get pods -n "${NAMESPACE}" --field-selector=status.phase!=Running,status.phase!=Succeeded --no-headers 2>/dev/null | wc -l | tr -d ' ')
    if [ "$stuck_pods" -gt 0 ]; then
        warn "Found ${stuck_pods} stuck pods from previous runs"
        log "Auto-cleaning stuck pods..."
        kubectl delete pods -n "${NAMESPACE}" \
            --field-selector=status.phase!=Running,status.phase!=Succeeded \
            --grace-period=0 --force --ignore-not-found=true 2>/dev/null || true
        log "✓ Stuck pods cleaned up"
        sleep 2
    fi

    if [ $missing_resources -gt 0 ]; then
        error "Missing required resources. Please fix before running pipeline."
        exit 1
    fi

    log "✅ Pre-flight checks passed!"
    echo ""
}

# Apply supporting resources (ConfigMaps, PVCs, Secrets)
apply_supporting_resources() {
    local script_dir
    script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

    log "Applying supporting resources..."

    # Apply maven-settings ConfigMap
    if [ -f "${script_dir}/maven-settings-configmap.yaml" ]; then
        kubectl apply -f "${script_dir}/maven-settings-configmap.yaml"
        log "✓ maven-settings ConfigMap applied"
    fi

    # Apply maven-cache PVC
    if [ -f "${script_dir}/maven-cache-pvc.yaml" ]; then
        kubectl apply -f "${script_dir}/maven-cache-pvc.yaml"
        log "✓ maven-cache PVC applied"
    fi

    # Apply maven-credentials secret if exists
    if [ -f "${script_dir}/maven-credentials-secret.yaml" ]; then
        kubectl apply -f "${script_dir}/maven-credentials-secret.yaml"
        log "✓ maven-credentials secret applied"
    fi

    # Apply registry
    if [ -f "${script_dir}/registry.yaml" ]; then
        kubectl apply -f "${script_dir}/registry.yaml"
        log "✓ Registry applied"
    fi

    # Apply RBAC for deploy task
    if [ -f "${script_dir}/rbac.yaml" ]; then
        kubectl apply -f "${script_dir}/rbac.yaml"
        log "✓ RBAC applied"
    fi

    log "Supporting resources applied"
}

run_all() {
    preflight_checks
    apply_pipeline
    delete_runs
    sleep 2
    create_run
    echo ""
    log "Pipeline started!"
    show_command "Follow logs:" "$0 logs"
    show_command "Diagnose issues:" "$0 diagnose"
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

# Get registry URL for pods to access (node IP + node port)
get_registry_url() {
    local node_ip
    node_ip=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' 2>/dev/null)
    local node_port
    node_port=$(kubectl get svc registry -n "${NAMESPACE}" -o jsonpath='{.spec.ports[0].nodePort}' 2>/dev/null)
    if [ -n "$node_ip" ] && [ -n "$node_port" ]; then
        echo "${node_ip}:${node_port}"
    else
        echo "localhost:5000"
    fi
}

create_run() {
    log "Creating new PipelineRun from ${PIPELINE_RUN_FILE}..."
    
    # Get correct registry URL for in-cluster access
    local registry_url
    registry_url=$(get_registry_url)
    log "Using registry: ${registry_url}"
    
    # Apply pipeline run with correct registry URL
    sed "s|value: \"10.89.0.2:32242\"|value: \"${registry_url}\"|" "${PIPELINE_RUN_FILE}" | kubectl create -f - -n "${NAMESPACE}"
    
    log "PipelineRun created"
    
    sleep 2
    local run_name
    run_name=$(kubectl get pipelinerun -n "${NAMESPACE}" --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1].metadata.name}' 2>/dev/null)
    if [ -n "$run_name" ]; then
        log "PipelineRun name: ${run_name}"
        show_command "Watch logs:" "tkn pipelinerun logs -f ${run_name} -n ${NAMESPACE}"
    fi
}

start_pipeline() {
    log "Starting pipeline with tkn CLI..."
    
    local script_dir
    script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    
    local node_ip
    node_ip=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' 2>/dev/null)
    local node_port
    node_port=$(kubectl get svc registry -n "${NAMESPACE}" -o jsonpath='{.spec.ports[0].nodePort}' 2>/dev/null)
    local registry_url="${node_ip}:${node_port}"
    
    if [ -z "$node_ip" ] || [ -z "$node_port" ]; then
        warn "Could not auto-detect registry URL, using default"
        registry_url="localhost:32242"
    fi
    
    log "Using registry: ${registry_url}"
    log "Using workspaces: shared-workspace (PVC) and maven-settings (ConfigMap)"
    
    tkn pipeline start goods-price-service-pipeline \
        --namespace="${NAMESPACE}" \
        --workspace name=shared-workspace,volumeClaimTemplateFile="${script_dir}/workspace-template.yaml" \
        --workspace name=maven-settings,config=maven-settings \
        --param registry="${registry_url}" \
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

show_pods() {
    log "Pod status in ${NAMESPACE}:"
    echo ""
    kubectl get pods -n "${NAMESPACE}" -o wide
    echo ""
    log "Pods with errors (if any):"
    kubectl get pods -n "${NAMESPACE}" -o json | \
        jq -r '.items[] | select(.status.phase != "Running" and .status.phase != "Succeeded") | "\(.metadata.name) - \(.status.phase)"' || true
}

get_specific_pod() {
    if [ -z "$1" ]; then
        error "Please provide a pod name"
        echo ""
        echo "Usage: $0 get-pod <POD_NAME>"
        echo ""
        echo "Example: $0 get-pod git-clone-xyz"
        echo ""
        show_pods
        exit 1
    fi

    local pod_name="$1"

    log "Getting details for pod: ${pod_name}"
    echo ""

    # Check if pod exists
    if ! kubectl get pod "${pod_name}" -n "${NAMESPACE}" &>/dev/null; then
        error "Pod '${pod_name}' not found in ${NAMESPACE}"
        echo ""
        show_pods
        exit 1
    fi

    echo -e "${BLUE}=== Pod Status ===${NC}"
    kubectl get pod "${pod_name}" -n "${NAMESPACE}" -o wide
    echo ""

    echo -e "${BLUE}=== Pod Description ===${NC}"
    kubectl describe pod "${pod_name}" -n "${NAMESPACE}"
    echo ""

    show_command "View pod logs:" "kubectl logs ${pod_name} -n ${NAMESPACE}"
    show_command "Follow pod logs:" "kubectl logs -f ${pod_name} -n ${NAMESPACE}"
    show_command "View pod YAML:" "kubectl get pod ${pod_name} -n ${NAMESPACE} -o yaml"
    show_command "View pod events:" "kubectl describe pod ${pod_name} -n ${NAMESPACE} | grep -A 30 Events:"
}

search_pods() {
    if [ -z "$1" ]; then
        error "Please provide a search pattern"
        echo ""
        echo "Usage: $0 search-pod <PATTERN>"
        echo ""
        echo "Example: $0 search-pod clone"
        exit 1
    fi

    local pattern="$1"

    log "Searching for pods matching: ${pattern}"
    echo ""

    kubectl get pods -n "${NAMESPACE}" | grep -E "NAME|${pattern}" || {
        warn "No pods found matching: ${pattern}"
    }

    echo ""
    echo -e "${YELLOW}Tip: Use 'get-pod' command with the full pod name for more details${NC}"
}

diagnose_deployment() {
    log "🔍 Diagnosing Deployment Issues in ${NAMESPACE}"
    echo ""

    echo -e "${BLUE}=== 1. Deployment Status ===${NC}"
    kubectl get deployment -n "${NAMESPACE}" goods-price-service -o wide 2>/dev/null || {
        warn "Deployment 'goods-price-service' not found"
    }
    echo ""

    echo -e "${BLUE}=== 2. Deployment Events ===${NC}"
    kubectl describe deployment goods-price-service -n "${NAMESPACE}" 2>/dev/null | grep -A 50 "Events:" || warn "No events found"
    echo ""

    echo -e "${BLUE}=== 3. Pod Status ===${NC}"
    kubectl get pods -n "${NAMESPACE}" -l app=goods-price-service -o wide
    echo ""

    echo -e "${BLUE}=== 4. Pod Details (Latest) ===${NC}"
    local latest_pod
    latest_pod=$(kubectl get pods -n "${NAMESPACE}" -l app=goods-price-service --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1].metadata.name}' 2>/dev/null)
    if [ -n "$latest_pod" ]; then
        echo "Latest Pod: $latest_pod"
        echo ""
        kubectl describe pod "$latest_pod" -n "${NAMESPACE}" 2>/dev/null | tail -50
    else
        warn "No pods found for app=goods-price-service"
    fi
    echo ""

    echo -e "${BLUE}=== 5. Image Pull Check ===${NC}"
    show_command "Check if image exists in registry:" \
        "kubectl get pods -n ${NAMESPACE} -l app=goods-price-service -o jsonpath='{.items[-1].spec.containers[0].image}'"
    echo ""

    echo -e "${BLUE}=== Common Issues & Solutions ===${NC}"
    echo ""
    echo "✓ Old pod stuck in termination:"
    show_command "  Force delete old pods:" \
        "kubectl delete pods -n ${NAMESPACE} -l app=goods-price-service --grace-period=0 --force"
    echo ""

    echo "✓ Image not available in registry:"
    show_command "  Check registry for images:" \
        "kubectl run --image=registry:latest -it debug --restart=Never -n ${NAMESPACE} -- sh"
    echo ""

    echo "✓ Slow pod startup (waiting for resources):"
    show_command "  Check node resources:" \
        "kubectl top nodes && kubectl top pods -n ${NAMESPACE}"
    echo ""
}

fix_stuck_deployment() {
    warn "🔧 Attempting to fix stuck deployment..."
    echo ""

    # Step 1: Delete old deployment
    log "Step 1: Deleting old deployment..."
    kubectl delete deployment goods-price-service -n "${NAMESPACE}" --ignore-not-found=true
    sleep 2

    # Step 2: Force delete stuck pods
    log "Step 2: Force deleting stuck pods..."
    kubectl delete pods -n "${NAMESPACE}" -l app=goods-price-service --grace-period=0 --force 2>/dev/null || true
    sleep 2

    # Step 3: Verify cleanup
    log "Step 3: Verifying cleanup..."
    local pod_count
    pod_count=$(kubectl get pods -n "${NAMESPACE}" -l app=goods-price-service --no-headers 2>/dev/null | wc -l)

    if [ "$pod_count" -eq 0 ]; then
        log "✓ Cleanup successful! All old pods removed."
        echo ""
        log "Next steps:"
        echo "1. Run: $0 run          (to restart pipeline)"
        echo "2. Or run: $0 diagnose  (to check status)"
    else
        warn "⚠ Some pods still exist. Run diagnose to check details:"
        echo "  $0 diagnose"
    fi
}

cleanup_pods() {
    log "Pod Cleanup Commands for ${NAMESPACE}"
    echo ""

    show_command "1. View all non-running pods:" \
        "kubectl get pods -n ${NAMESPACE} -o wide | grep -v Running"

    show_command "2. View goods-price-service deployment pods:" \
        "kubectl get pods -n ${NAMESPACE} -l app=goods-price-service -o wide"

    show_command "3. Delete goods-price-service deployment:" \
        "kubectl delete deployment goods-price-service -n ${NAMESPACE} --ignore-not-found=true"

    show_command "4. Force delete stuck pods:" \
        "kubectl delete pods -n ${NAMESPACE} --grace-period=0 --force"

    show_command "5. Delete all PipelineRuns and TaskRuns:" \
        "kubectl delete pipelinerun,taskrun --all -n ${NAMESPACE}"

    show_command "6. Nuclear option (delete everything and start fresh):" \
        "kubectl delete all --all -n ${NAMESPACE} --ignore-not-found=true && sleep 3 && $0 run"

    echo ""
    echo -e "${YELLOW}⚠ Copy and execute commands in your terminal${NC}"
}

# Auto-cleanup stuck and unused pods
auto_cleanup_pods() {
    log "🔄 Auto-cleaning up stuck and unused pods in ${NAMESPACE}..."
    echo ""

    # 1. Delete pods in Error/CrashLoopBackOff/ImagePullBackOff states
    log "Step 1: Deleting pods in error states..."
    kubectl delete pods -n "${NAMESPACE}" \
        --field-selector=status.phase=Failed,status.phase=Unknown \
        --ignore-not-found=true 2>/dev/null || true

    # 2. Force delete pods stuck in Terminating state
    log "Step 2: Force deleting pods stuck in Terminating..."
    kubectl delete pods -n "${NAMESPACE}" \
        --grace-period=0 \
        --force \
        --ignore-not-found=true 2>/dev/null || true

    # 3. Delete completed/succeeded pods older than 1 hour
    log "Step 3: Deleting old completed pods..."
    kubectl get pods -n "${NAMESPACE}" \
        --field-selector=status.phase=Succeeded \
        --no-headers \
        -o custom-columns=NAME:.metadata.name,AGE:.metadata.creationTimestamp | \
    while read -r pod_name age; do
        # Check if pod is older than 1 hour (3600 seconds)
        if [ $(($(date +%s) - $(date -d "$age" +%s))) -gt 3600 ]; then
            kubectl delete pod "$pod_name" -n "${NAMESPACE}" --ignore-not-found=true 2>/dev/null || true
        fi
    done

    # 4. Delete pods that are stuck in Pending state for too long
    log "Step 4: Deleting pods stuck in Pending..."
    kubectl get pods -n "${NAMESPACE}" \
        --field-selector=status.phase=Pending \
        --no-headers \
        -o custom-columns=NAME:.metadata.name,AGE:.metadata.creationTimestamp | \
    while read -r pod_name age; do
        # Check if pod has been pending for more than 10 minutes (600 seconds)
        if [ $(($(date +%s) - $(date -d "$age" +%s))) -gt 600 ]; then
            kubectl delete pod "$pod_name" -n "${NAMESPACE}" --ignore-not-found=true 2>/dev/null || true
        fi
    done

    # 5. Clean up orphaned resources
    log "Step 5: Cleaning up orphaned resources..."
    kubectl delete pipelinerun -n "${NAMESPACE}" \
        --field-selector=status.completionTime!=null \
        --ignore-not-found=true 2>/dev/null || true

    kubectl delete taskrun -n "${NAMESPACE}" \
        --field-selector=status.completionTime!=null \
        --ignore-not-found=true 2>/dev/null || true

    log "✅ Auto-cleanup completed!"
    echo ""
    show_pods
}

# Delete specific pod with confirmation
delete_pod() {
    if [ -z "$1" ]; then
        error "Please provide a pod name to delete"
        echo ""
        echo "Usage: $0 delete-pod <POD_NAME> [--force]"
        echo ""
        echo "Examples:"
        echo "  $0 delete-pod my-pod-name"
        echo "  $0 delete-pod my-pod-name --force"
        echo ""
        show_pods
        exit 1
    fi

    local pod_name="$1"
    local force_flag="$2"

    # Check if pod exists
    if ! kubectl get pod "${pod_name}" -n "${NAMESPACE}" &>/dev/null; then
        error "Pod '${pod_name}' not found in ${NAMESPACE}"
        echo ""
        show_pods
        exit 1
    fi

    # Get pod status
    local pod_status
    pod_status=$(kubectl get pod "${pod_name}" -n "${NAMESPACE}" -o jsonpath='{.status.phase}')

    echo -e "${YELLOW}Pod Details:${NC}"
    kubectl get pod "${pod_name}" -n "${NAMESPACE}" -o wide
    echo ""

    # Confirm deletion
    echo -e "${RED}⚠  Are you sure you want to delete pod '${pod_name}' (Status: ${pod_status})?${NC}"
    echo -n "Type 'yes' to confirm: "
    read -r confirmation

    if [ "$confirmation" != "yes" ]; then
        log "Deletion cancelled"
        exit 0
    fi

    # Delete the pod
    if [ "$force_flag" = "--force" ]; then
        log "Force deleting pod: ${pod_name}"
        kubectl delete pod "${pod_name}" -n "${NAMESPACE}" --grace-period=0 --force
    else
        log "Deleting pod: ${pod_name}"
        kubectl delete pod "${pod_name}" -n "${NAMESPACE}"
    fi

    log "Pod deletion initiated"
}

# Show pod cleanup summary
pod_cleanup_summary() {
    log "📊 Pod Cleanup Summary for ${NAMESPACE}"
    echo ""

    echo -e "${BLUE}=== Current Pod Status ===${NC}"
    kubectl get pods -n "${NAMESPACE}" -o wide
    echo ""

    echo -e "${BLUE}=== Pods by Status ===${NC}"
    echo "Running pods:"
    kubectl get pods -n "${NAMESPACE}" --field-selector=status.phase=Running --no-headers | wc -l | xargs echo "  Count:"
    echo ""

    echo "Failed/Error pods:"
    kubectl get pods -n "${NAMESPACE}" --field-selector=status.phase=Failed,status.phase=Unknown --no-headers | wc -l | xargs echo "  Count:"
    echo ""

    echo "Pending pods:"
    kubectl get pods -n "${NAMESPACE}" --field-selector=status.phase=Pending --no-headers | wc -l | xargs echo "  Count:"
    echo ""

    echo "Terminating pods:"
    kubectl get pods -n "${NAMESPACE}" -o json | \
        jq -r '.items[] | select(.metadata.deletionTimestamp != null) | .metadata.name' | wc -l | xargs echo "  Count:"
    echo ""

    echo -e "${BLUE}=== Quick Actions ===${NC}"
    show_command "Auto cleanup all issues:" "$0 auto-cleanup"
    show_command "Force delete all stuck pods:" "$0 force-cleanup"
    show_command "View detailed pod status:" "$0 pods"
    show_command "Diagnose deployment issues:" "$0 diagnose"
}

# Force cleanup all stuck pods (nuclear option)
force_cleanup_pods() {
    warn "💥 Force cleaning up ALL stuck pods in ${NAMESPACE}..."
    echo ""

    # Show current status before cleanup
    log "Current pod status:"
    kubectl get pods -n "${NAMESPACE}" -o wide
    echo ""

    # Confirm destructive action
    echo -e "${RED}⚠  This will force delete ALL pods that are not Running or Succeeded!${NC}"
    echo -e "${RED}⚠  This includes pods that might be important for your pipeline!${NC}"
    echo -n "Type 'FORCE' to confirm: "
    read -r confirmation

    if [ "$confirmation" != "FORCE" ]; then
        log "Force cleanup cancelled"
        exit 0
    fi

    # Force delete all non-running pods
    log "Force deleting all non-running pods..."
    kubectl delete pods -n "${NAMESPACE}" \
        --field-selector=status.phase!=Running,status.phase!=Succeeded \
        --grace-period=0 \
        --force \
        --ignore-not-found=true 2>/dev/null || true

    # Wait a moment
    sleep 3

    # Show status after cleanup
    log "Cleanup completed. New pod status:"
    kubectl get pods -n "${NAMESPACE}" -o wide
}

# Restart goods-price-service deployment
restart_deployment() {
    log "🔄 Restarting goods-price-service deployment..."
    echo ""

    # Check if deployment exists
    if ! kubectl get deployment goods-price-service -n "${NAMESPACE}" &>/dev/null; then
        error "Deployment 'goods-price-service' not found in ${NAMESPACE}"
        exit 1
    fi

    # Show current status
    log "Current deployment status:"
    kubectl get deployment goods-price-service -n "${NAMESPACE}"
    echo ""

    # Restart by updating annotation (triggers rolling update)
    log "Triggering rolling restart..."
    kubectl patch deployment goods-price-service -n "${NAMESPACE}" \
        -p "{\"spec\":{\"template\":{\"metadata\":{\"annotations\":{\"kubectl.kubernetes.io/restartedAt\":\"$(date +%Y-%m-%dT%H:%M:%S%Z)\"}}}}}"

    # Wait for rollout
    log "Waiting for rollout to complete..."
    kubectl rollout status deployment/goods-price-service -n "${NAMESPACE}" --timeout=300s

    log "✅ Deployment restarted successfully!"
    kubectl get deployment goods-price-service -n "${NAMESPACE}"
}

# Scale goods-price-service deployment
scale_deployment() {
    local replicas="${1:-1}"

    if ! [[ "$replicas" =~ ^[0-9]+$ ]]; then
        error "Invalid number of replicas: $replicas"
        echo "Usage: $0 scale-deploy <REPLICAS> (default: 1)"
        exit 1
    fi

    log "📏 Scaling goods-price-service deployment to $replicas replicas..."
    echo ""

    # Check if deployment exists
    if ! kubectl get deployment goods-price-service -n "${NAMESPACE}" &>/dev/null; then
        error "Deployment 'goods-price-service' not found in ${NAMESPACE}"
        exit 1
    fi

    # Show current status
    log "Current deployment status:"
    kubectl get deployment goods-price-service -n "${NAMESPACE}"
    echo ""

    # Scale deployment
    kubectl scale deployment goods-price-service -n "${NAMESPACE}" --replicas="$replicas"

    # Wait for scaling to complete
    log "Waiting for scaling to complete..."
    kubectl rollout status deployment/goods-price-service -n "${NAMESPACE}" --timeout=300s

    log "✅ Deployment scaled to $replicas replicas!"
    kubectl get deployment goods-price-service -n "${NAMESPACE}"
    kubectl get pods -n "${NAMESPACE}" -l app=goods-price-service
}

# Show cluster resource usage
show_resources() {
    log "📊 Cluster Resource Usage"
    echo ""

    echo -e "${BLUE}=== Node Resources ===${NC}"
    kubectl top nodes 2>/dev/null || warn "kubectl top nodes not available (metrics-server may not be installed)"
    echo ""

    echo -e "${BLUE}=== Pod Resources in ${NAMESPACE} ===${NC}"
    kubectl top pods -n "${NAMESPACE}" 2>/dev/null || warn "kubectl top pods not available (metrics-server may not be installed)"
    echo ""

    echo -e "${BLUE}=== Pod Resource Requests/Limits ===${NC}"
    kubectl get pods -n "${NAMESPACE}" -o custom-columns="NAME:.metadata.name,CPU_REQ:.spec.containers[*].resources.requests.cpu,CPU_LIM:.spec.containers[*].resources.limits.cpu,MEM_REQ:.spec.containers[*].resources.requests.memory,MEM_LIM:.spec.containers[*].resources.limits.memory" | head -10
    echo ""

    echo -e "${BLUE}=== PVC Usage ===${NC}"
    kubectl get pvc -n "${NAMESPACE}" -o wide 2>/dev/null || warn "No PVCs found"
}

# Show logs for all goods-price-service pods
show_deployment_logs() {
    local lines="${1:-50}"
    local follow="${2:-false}"

    log "📋 Showing logs for goods-price-service pods (last $lines lines)..."
    echo ""

    # Get all pods for the deployment
    local pods
    pods=$(kubectl get pods -n "${NAMESPACE}" -l app=goods-price-service -o jsonpath='{.items[*].metadata.name}' 2>/dev/null)

    if [ -z "$pods" ]; then
        error "No goods-price-service pods found"
        exit 1
    fi

    # Show logs for each pod
    for pod in $pods; do
        echo -e "${BLUE}=== Logs for pod: $pod ===${NC}"
        if [ "$follow" = "true" ]; then
            kubectl logs -f "$pod" -n "${NAMESPACE}" --tail="$lines"
        else
            kubectl logs "$pod" -n "${NAMESPACE}" --tail="$lines"
        fi
        echo ""
    done

    if [ "$follow" != "true" ]; then
        show_command "Follow logs for all pods:" "$0 logs-deploy $lines follow"
    fi
}

# Verify Maven GitHub credentials
verify_maven_credentials() {
    log "🔍 Verifying Maven GitHub Credentials..."
    echo ""

    local secret_name="github-maven-credentials"
    local configmap_name="maven-settings"

    # Check if secret exists
    if kubectl get secret "$secret_name" -n "$NAMESPACE" &> /dev/null; then
        success "Secret '$secret_name' exists"
    else
        error "Secret '$secret_name' not found in namespace '$NAMESPACE'"
        echo "Run: cd tekton && ./setup-maven-credentials.sh"
        exit 1
    fi

    # Check if ConfigMap exists
    if kubectl get configmap "$configmap_name" -n "$NAMESPACE" &> /dev/null; then
        success "ConfigMap '$configmap_name' exists"
    else
        error "ConfigMap '$configmap_name' not found in namespace '$NAMESPACE'"
        echo "Run: cd tekton && kubectl apply -f maven-settings-configmap.yaml"
        exit 1
    fi

    echo ""
    success "Maven credentials are configured!"
    echo ""
    echo "Next: Run the pipeline with: $0 run"
}

# Interactive Maven credentials setup
setup_maven_credentials() {
    local script_dir
    script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

    if [ -f "$script_dir/setup-maven-credentials.sh" ]; then
        "$script_dir/setup-maven-credentials.sh"
    else
        error "setup-maven-credentials.sh not found in $script_dir"
        exit 1
    fi
}

# Main
case "${1:-run}" in
    run)
        run_all
        ;;
    start|tkn|cli)
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
    pods)
        show_pods
        ;;
    get-pod|pod|describe-pod)
        get_specific_pod "$2"
        ;;
    search-pod|find-pod)
        search_pods "$2"
        ;;
    diagnose)
        diagnose_deployment
        ;;
    fix)
        fix_stuck_deployment
        ;;
    pods-clean|cleanup-pods)
        cleanup_pods
        ;;
    auto-cleanup)
        auto_cleanup_pods
        ;;
    delete-pod)
        delete_pod "$2" "$3"
        ;;
    cleanup-summary)
        pod_cleanup_summary
        ;;
    force-cleanup)
        force_cleanup_pods
        ;;
    restart-deploy)
        restart_deployment
        ;;
    scale-deploy)
        scale_deployment "$2"
        ;;
    resources)
        show_resources
        ;;
    logs-deploy)
        show_deployment_logs "$2" "$3"
        ;;
    maven-verify)
        verify_maven_credentials
        ;;
    maven-setup)
        setup_maven_credentials
        ;;
    setup|init)
        apply_supporting_resources
        ;;
    check|verify)
        preflight_checks
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
