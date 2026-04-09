#!/bin/bash
# Create maven-settings-secret from github-maven-credentials

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOYER_DIR="$(dirname "$SCRIPT_DIR")"

if [ -f "$DEPLOYER_DIR/.env" ]; then
    export $(grep -v '^#' "$DEPLOYER_DIR/.env" | xargs)
fi

PIPELINE_NAMESPACE="${PIPELINE_NAMESPACE:-goods-price-ci}"

# Get credentials from the secret
USERNAME=$(kubectl get secret github-maven-credentials -n "$PIPELINE_NAMESPACE" -o jsonpath='{.data.username}' | base64 -d)
TOKEN=$(kubectl get secret github-maven-credentials -n "$PIPELINE_NAMESPACE" -o jsonpath='{.data.token}' | base64 -d)

# Create settings.xml content
SETTINGS_XML=$(cat <<EOF
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>$USERNAME</username>
      <password>$TOKEN</password>
    </server>
  </servers>
</settings>
EOF
)

# Create the secret
kubectl create secret generic maven-settings-secret \
  --from-literal=settings.xml="$SETTINGS_XML" \
  -n "$PIPELINE_NAMESPACE" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "✅ maven-settings-secret created/updated in namespace: $PIPELINE_NAMESPACE"
