#!/bin/bash
# Podman Registry Integration Helper
# Manages images between Kind registry and local Podman

REGISTRY_URL="10.89.0.2:32242"
NAMESPACE="goods-price-ci"

show_help() {
    cat << 'EOF'
Podman Registry Helper

Usage: ./podman-registry.sh [command] [options]

Commands:
  list                    List images in local Podman
  list-remote             List images in Kind registry
  pull <image-name>       Pull image from Kind registry to Podman
  push <image-name>       Push image from Podman to Kind registry
  delete <image-name>     Delete image from local Podman
  delete-remote <name>    Delete image from Kind registry
  sync                    Pull all images from registry to Podman
  cleanup                 Remove unused/dangling images from Podman

Examples:
  ./podman-registry.sh list
  ./podman-registry.sh pull goods-price-comparison-service:latest
  ./podman-registry.sh delete goods-price-comparison-service:latest
  ./podman-registry.sh cleanup
EOF
}

list_local() {
    echo "📦 Local Podman images:"
    podman images --format "table {{.Repository}}:{{.Tag}}\t{{.Size}}\t{{.Created}}"
}

list_remote() {
    echo "🌐 Kind Registry images at ${REGISTRY_URL}:"
    curl -s "http://${REGISTRY_URL}/v2/_catalog" | jq -r '.repositories[]' 2>/dev/null || echo "Registry not accessible"
}

pull_image() {
    local image="$1"
    if [ -z "$image" ]; then
        echo "❌ Usage: pull <image-name>"
        exit 1
    fi
    
    echo "⬇️  Pulling ${REGISTRY_URL}/${image} to Podman..."
    podman pull "${REGISTRY_URL}/${image}" --tls-verify=false
    
    # Tag without registry prefix for easier use
    local short_name=$(echo "$image" | sed "s|${REGISTRY_URL}/||")
    podman tag "${REGISTRY_URL}/${image}" "localhost/${short_name}"
    
    echo "✅ Available as: localhost/${short_name}"
}

push_image() {
    local image="$1"
    if [ -z "$image" ]; then
        echo "❌ Usage: push <image-name>"
        exit 1
    fi
    
    echo "⬆️  Pushing ${image} to Kind registry..."
    podman push "${image}" "${REGISTRY_URL}/${image}" --tls-verify=false
}

delete_local() {
    local image="$1"
    if [ -z "$image" ]; then
        echo "❌ Usage: delete <image-name>"
        exit 1
    fi
    
    echo "🗑️  Removing ${image} from Podman..."
    podman rmi "${image}" -f
}

delete_remote() {
    local image="$1"
    if [ -z "$image" ]; then
        echo "❌ Usage: delete-remote <image-name>"
        exit 1
    fi
    
    echo "🗑️  Removing ${image} from Kind registry..."
    # Registry doesn't support direct delete via API easily
    # Need to exec into registry pod
    kubectl exec -n "${NAMESPACE}" deployment/registry -- rm -rf "/var/lib/registry/docker/registry/v2/repositories/${image}" 2>/dev/null || \
        echo "Manual delete: kubectl exec -n ${NAMESPACE} deployment/registry -- rm -rf /var/lib/registry/docker/registry/v2/repositories/${image}"
}

sync_all() {
    echo "🔄 Syncing all images from Kind registry to Podman..."
    local repos=$(curl -s "http://${REGISTRY_URL}/v2/_catalog" | jq -r '.repositories[]' 2>/dev/null)
    
    for repo in $repos; do
        local tags=$(curl -s "http://${REGISTRY_URL}/v2/${repo}/tags/list" | jq -r '.tags[]' 2>/dev/null)
        for tag in $tags; do
            echo "  Pulling: ${repo}:${tag}"
            podman pull "${REGISTRY_URL}/${repo}:${tag}" --tls-verify=false 2>/dev/null || echo "    Skipped"
        done
    done
    
    echo "✅ Sync complete"
}

cleanup() {
    echo "🧹 Cleaning up Podman..."
    echo "Removing dangling images..."
    podman image prune -f
    echo ""
    echo "Current disk usage:"
    podman system df
}

case "${1:-help}" in
    list) list_local ;;
    list-remote) list_remote ;;
    pull) pull_image "$2" ;;
    push) push_image "$2" ;;
    delete) delete_local "$2" ;;
    delete-remote) delete_remote "$2" ;;
    sync) sync_all ;;
    cleanup) cleanup ;;
    help|--help|-h) show_help ;;
    *) echo "Unknown command: $1"; show_help ; exit 1 ;;
esac
