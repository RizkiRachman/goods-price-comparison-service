#!/bin/bash

# Maven GitHub Credentials Setup Script
# This script helps you configure GitHub credentials for Maven to access GitHub Packages
#
# Usage:
#   ./setup-maven-credentials.sh [USERNAME] [TOKEN]
#   ./setup-maven-credentials.sh                    # Interactive mode
#   ./setup-maven-credentials.sh verify              # Verify existing credentials
#   ./setup-maven-credentials.sh delete              # Delete existing credentials

set -e

NAMESPACE="goods-price-ci"
SECRET_NAME="github-maven-credentials"
SETTINGS_CONFIGMAP="maven-settings"

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
    exit 1
}

success() {
    echo -e "${GREEN}✓${NC} $1"
}

show_help() {
    cat << 'EOF'
Maven GitHub Credentials Setup

Usage:
  ./setup-maven-credentials.sh [USERNAME] [TOKEN]
  ./setup-maven-credentials.sh                      # Interactive mode
  ./setup-maven-credentials.sh verify               # Verify credentials
  ./setup-maven-credentials.sh delete               # Delete credentials
  ./setup-maven-credentials.sh help                 # Show this help

Examples:
  ./setup-maven-credentials.sh myusername mytoken123
  ./setup-maven-credentials.sh
  ./setup-maven-credentials.sh verify

Prerequisites:
  1. GitHub Personal Access Token (PAT) with 'read:packages' scope
     Create at: https://github.com/settings/tokens/new

  2. kubectl configured to access your cluster

  3. Kubernetes namespace 'goods-price-ci' must exist

Steps to Create a GitHub PAT:
  1. Go to https://github.com/settings/tokens/new
  2. Give it a name (e.g., "Maven Package Access")
  3. Select scope: read:packages
  4. Click "Generate token"
  5. Copy the token (you won't see it again!)
  6. Use the token in this script

Important Notes:
  - The token should have 'read:packages' scope
  - Keep your PAT secure - never commit to version control
  - For CI/CD, use GitHub Actions secrets instead
  - The secret is stored in Kubernetes as 'github-maven-credentials'
EOF
}

check_prerequisites() {
    log "Checking prerequisites..."

    if ! command -v kubectl &> /dev/null; then
        error "kubectl not found. Please install kubectl first."
    fi

    if ! kubectl get namespace "$NAMESPACE" &> /dev/null; then
        error "Namespace '$NAMESPACE' not found. Please create it first with: kubectl create namespace $NAMESPACE"
    fi

    success "Prerequisites check passed"
}

verify_credentials() {
    log "Verifying GitHub credentials..."

    if ! kubectl get secret "$SECRET_NAME" -n "$NAMESPACE" &> /dev/null; then
        warn "Secret '$SECRET_NAME' does not exist in namespace '$NAMESPACE'"
        echo ""
        echo "To create it, run:"
        echo "  ./setup-maven-credentials.sh"
        return 1
    fi

    success "Secret '$SECRET_NAME' exists"

    if ! kubectl get configmap "$SETTINGS_CONFIGMAP" -n "$NAMESPACE" &> /dev/null; then
        warn "ConfigMap '$SETTINGS_CONFIGMAP' does not exist in namespace '$NAMESPACE'"
        echo ""
        echo "Applying ConfigMap from maven-settings-configmap.yaml..."
        if [ -f "maven-settings-configmap.yaml" ]; then
            kubectl apply -f maven-settings-configmap.yaml
            success "ConfigMap created"
        else
            error "maven-settings-configmap.yaml not found"
        fi
    else
        success "ConfigMap '$SETTINGS_CONFIGMAP' exists"
    fi

    # Try to fetch a dependency to verify credentials work
    log "Testing Maven access..."

    username=$(kubectl get secret "$SECRET_NAME" -n "$NAMESPACE" -o jsonpath='{.data.username}' | base64 -d)
    token=$(kubectl get secret "$SECRET_NAME" -n "$NAMESPACE" -o jsonpath='{.data.token}' | base64 -d)

    # Test credentials with GitHub API
    if curl -s -f -H "Authorization: token $token" https://api.github.com/user &> /dev/null; then
        success "GitHub credentials are valid"

        # Show GitHub user info
        user_info=$(curl -s -H "Authorization: token $token" https://api.github.com/user | grep '"login"' | head -1)
        log "Authenticated as: $user_info"
    else
        warn "Could not verify GitHub credentials with API"
        warn "This might be due to network issues or token permissions"
    fi

    echo ""
    success "Credentials verification complete!"
}

delete_credentials() {
    echo -e "${RED}⚠  WARNING: This will delete the GitHub credentials secret!${NC}"
    echo -n "Type 'DELETE' to confirm: "
    read -r confirmation

    if [ "$confirmation" != "DELETE" ]; then
        log "Deletion cancelled"
        return
    fi

    log "Deleting secret..."
    kubectl delete secret "$SECRET_NAME" -n "$NAMESPACE" || warn "Secret not found"

    log "Deleting ConfigMap..."
    kubectl delete configmap "$SETTINGS_CONFIGMAP" -n "$NAMESPACE" || warn "ConfigMap not found"

    success "Credentials deleted"
}

create_credentials_interactive() {
    echo -e "${BLUE}=== Maven GitHub Credentials Setup ===${NC}"
    echo ""
    echo "This script will configure Maven to access GitHub Packages."
    echo ""

    # Check prerequisites
    check_prerequisites
    echo ""

    # Get GitHub username
    echo "Step 1: GitHub Username"
    echo "--------"
    echo -n "Enter your GitHub username: "
    read -r github_username

    if [ -z "$github_username" ]; then
        error "GitHub username cannot be empty"
    fi

    echo ""

    # Get GitHub PAT
    echo "Step 2: GitHub Personal Access Token (PAT)"
    echo "--------"
    echo "You need a GitHub PAT with 'read:packages' scope."
    echo ""
    echo "To create one:"
    echo "  1. Go to https://github.com/settings/tokens/new"
    echo "  2. Give it a name (e.g., 'Maven Package Access')"
    echo "  3. Select scope: read:packages"
    echo "  4. Click 'Generate token'"
    echo "  5. Copy the token"
    echo ""
    echo -n "Paste your GitHub PAT (hidden): "
    read -rs github_token
    echo ""

    if [ -z "$github_token" ]; then
        error "GitHub token cannot be empty"
    fi

    echo ""

    # Confirmation
    echo "Step 3: Confirmation"
    echo "--------"
    echo "Username: $github_username"
    echo "Token: ****${github_token: -10}"
    echo ""
    echo "This will create:"
    echo "  - Kubernetes Secret: $SECRET_NAME"
    echo "  - ConfigMap: $SETTINGS_CONFIGMAP"
    echo "  - In namespace: $NAMESPACE"
    echo ""

    echo -n "Continue? (yes/no): "
    read -r confirm

    if [ "$confirm" != "yes" ]; then
        log "Setup cancelled"
        return
    fi

    echo ""
    create_credentials "$github_username" "$github_token"
}

create_credentials() {
    local username="$1"
    local token="$2"

    if [ -z "$username" ] || [ -z "$token" ]; then
        error "Username and token are required"
    fi

    log "Creating Kubernetes secret..."

    # Delete existing secret if it exists
    kubectl delete secret "$SECRET_NAME" -n "$NAMESPACE" --ignore-not-found=true

    # Create new secret
    kubectl create secret generic "$SECRET_NAME" \
        --from-literal=username="$username" \
        --from-literal=token="$token" \
        -n "$NAMESPACE"

    success "Secret created: $SECRET_NAME"

    # Create or update ConfigMap
    log "Creating Maven settings ConfigMap..."

    if [ -f "maven-settings-configmap.yaml" ]; then
        kubectl apply -f maven-settings-configmap.yaml
        success "ConfigMap applied from file"
    else
        warn "maven-settings-configmap.yaml not found, ConfigMap may not be created"
    fi

    echo ""
    success "Setup complete!"

    # Verify credentials
    echo ""
    echo "Verifying credentials..."
    verify_credentials
}

# Main script
case "${1:-}" in
    help|--help|-h)
        show_help
        ;;
    verify|check)
        check_prerequisites
        verify_credentials
        ;;
    delete|remove)
        check_prerequisites
        delete_credentials
        ;;
    "")
        # Interactive mode
        create_credentials_interactive
        ;;
    *)
        # Non-interactive mode
        check_prerequisites
        create_credentials "$1" "$2"
        ;;
esac

echo ""
echo "Next steps:"
echo "  1. Run pipeline: kubectl create -f tekton/pipeline-run.yaml"
echo "  2. Watch logs: tkn pipelinerun logs -f -n $NAMESPACE"
echo "  3. View dashboard: ./tekton/install-dashboard.sh proxy"

