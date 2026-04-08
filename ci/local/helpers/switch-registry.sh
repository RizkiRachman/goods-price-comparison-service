#!/bin/bash
# Switch between different registry options
# Usage: ./switch-registry.sh [local|podman|quay]

set -e

LOCAL_REGISTRY="10.89.0.2:32242"
PODMAN_REGISTRY="host.docker.internal:5000"
QUAY_REGISTRY="host.docker.internal:8080"

show_help() {
    cat << 'EOF'
Registry Switcher

Usage: ./switch-registry.sh [registry-type]

Options:
  local       Use built-in Kind registry (10.89.0.2:32242)
  podman      Use Podman local registry (localhost:5000)
  quay        Use Project Quay (localhost:8080)

Current: Shows current registry configuration

Examples:
  ./switch-registry.sh local    # Default Kind registry
  ./switch-registry.sh podman  # Podman registry
  ./switch-registry.sh quay    # Quay registry

EOF
}

get_current() {
    local current=$(grep -o 'value: "[^"]*:5000\|value: "[^"]*:32242\|value: "[^"]*:8080"' "$(dirname "$0")/../tekton/pipeline-run.yaml" 2>/dev/null | head -1 | sed 's/value: "//' | sed 's/"$//')
    echo "Current registry: ${current:-unknown}"
}

switch_to() {
    local registry_type="$1"
    local registry_url
    local registry_name
    
    case "$registry_type" in
        local)
            registry_url="${LOCAL_REGISTRY}"
            registry_name="Kind Local Registry"
            ;;
        podman)
            registry_url="${PODMAN_REGISTRY}"
            registry_name="Podman Registry"
            info "Make sure Podman registry is running: ./setup-podman-registry.sh status"
            ;;
        quay)
            registry_url="${QUAY_REGISTRY}"
            registry_name="Quay"
            info "Make sure Quay is running: ./setup-quay.sh status"
            ;;
        *)
            echo "Unknown registry: $registry_type"
            show_help
            exit 1
            ;;
    esac
    
    echo "Switching to ${registry_name} (${registry_url})..."
    
    # Update pipeline-run.yaml
    local tekton_dir="$(dirname "$0")/../tekton"
    
    if [ -f "${tekton_dir}/pipeline-run.yaml" ]; then
        sed -i.bak "s|value: \"10.89.0.2:32242\"|value: \"${registry_url}\"|g" "${tekton_dir}/pipeline-run.yaml" 2>/dev/null || \
        sed -i.bak "s|value: \"host.docker.internal:5000\"|value: \"${registry_url}\"|g" "${tekton_dir}/pipeline-run.yaml" 2>/dev/null || \
        sed -i.bak "s|value: \"host.docker.internal:8080\"|value: \"${registry_url}\"|g" "${tekton_dir}/pipeline-run.yaml" 2>/dev/null || true
        rm -f "${tekton_dir}/pipeline-run.yaml.bak"
    fi
    
    # Update helper scripts
    local helpers_dir="$(dirname "$0")"
    for script in "${helpers_dir}"/*.sh; do
        if [ -f "$script" ]; then
            sed -i.bak "s|REGISTRY_URL=\"10.89.0.2:32242\"|REGISTRY_URL=\"${registry_url}\"|g" "$script" 2>/dev/null || true
            sed -i.bak "s|REGISTRY_URL=\"host.docker.internal:5000\"|REGISTRY_URL=\"${registry_url}\"|g" "$script" 2>/dev/null || true
            sed -i.bak "s|REGISTRY_URL=\"host.docker.internal:8080\"|REGISTRY_URL=\"${registry_url}\"|g" "$script" 2>/dev/null || true
            rm -f "${script}.bak"
        fi
    done
    
    echo "✅ Switched to ${registry_name}"
    echo ""
    echo "Registry URL: ${registry_url}"
    echo ""
    echo "Next steps:"
    echo "  ./setup.sh tekton   # Re-apply pipeline"
    echo "  ./setup.sh run      # Run pipeline"
}

info() { echo -e "\033[0;34m[INFO]\033[0m $1"; }

# Main
case "${1:-current}" in
    local|podman|quay)
        switch_to "$1"
        ;;
    current|status)
        get_current
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo "Unknown option: $1"
        show_help
        exit 1
        ;;
esac
