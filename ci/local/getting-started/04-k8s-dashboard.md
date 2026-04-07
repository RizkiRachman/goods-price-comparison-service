# 04 - Kubernetes Dashboard Setup & Login

The Kubernetes Dashboard is a web-based UI for managing your Kubernetes cluster resources (pods, services, deployments, etc.).

## What's the Difference?

| Dashboard | Purpose | Runs On |
|-----------|---------|---------|
| **Kubernetes Dashboard** | Manage K8s cluster (pods, nodes, etc.) | Any Kubernetes cluster |
| **Tekton Dashboard** | View CI/CD pipelines | Tekton Pipelines installed |

## Installation

### Step 1: Install Kubernetes Dashboard

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
```

This creates:
- Dashboard pods in `kubernetes-dashboard` namespace
- A service account for authentication
- Required RBAC roles

**Wait for it to be ready:**
```bash
kubectl wait --for=condition=ready pod -l k8s-app=kubernetes-dashboard -n kubernetes-dashboard --timeout=120s
```

### Step 2: Create Admin User (for Login Token)

The dashboard requires authentication. Create an admin user:

```bash
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user-binding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
EOF
```

### Step 3: Get Login Token

**For the kind cluster (local):**
```bash
kubectl -n kubernetes-dashboard create token admin-user
```

**Copy the output** - this is your login token (a long string of letters and numbers).

### Step 4: Start kubectl proxy

```bash
kubectl proxy
```

Keep this terminal window open. You'll see:
```
Starting to serve on 127.0.0.1:8001
```

### Step 5: Open Dashboard in Browser

Go to:
```
http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
```

### Step 6: Login

1. You'll see a login screen with two options:
   - **Token** (recommended) ← Choose this
   - Kubeconfig

2. **Paste your token** from Step 3 into the "Enter token" field

3. Click **"Sign in"**

## You're In! What's Next?

### Navigate the Dashboard

**Left sidebar shows:**
- **Cluster** → Nodes, Namespaces, Storage
- **Workloads** → Pods, Deployments, Replica Sets
- **Discovery and Load Balancing** → Services, Ingresses
- **Config and Storage** → ConfigMaps, Secrets

### Useful Actions

**View Pods:**
1. Click "Workloads" → "Pods"
2. Select namespace from top dropdown (try "tekton-pipelines" or "goods-price-ci")

**View Pod Logs:**
1. Click on a pod name
2. Click "Logs" button (top right)

**Delete a Resource:**
1. Click the ⋮ menu next to any resource
2. Select "Delete"

## Creating a Persistent Token (Optional)

The token from `create token` expires after 1 hour. For a longer-lived token:

```bash
# Create a secret for the service account
kubectl apply -f - <<EOF
apiVersion: v1
kind: Secret
metadata:
  name: admin-user-token
  namespace: kubernetes-dashboard
  annotations:
    kubernetes.io/service-account.name: admin-user
type: kubernetes.io/service-account-token
EOF

# Get the token (won't expire)
kubectl get secret admin-user-token -n kubernetes-dashboard -o jsonpath='{.data.token}' | base64 -d
```

## Troubleshooting

### "Forbidden (403)" error
You don't have permissions. Make sure you created the ClusterRoleBinding in Step 2.

### "Unauthorized (401)" error
Your token expired. Generate a new one:
```bash
kubectl -n kubernetes-dashboard create token admin-user
```

### Dashboard won't load
1. Check if proxy is running: `kubectl proxy`
2. Check if dashboard is ready:
```bash
kubectl get pods -n kubernetes-dashboard
```
3. Try direct pod port-forward:
```bash
POD=$(kubectl get pod -n kubernetes-dashboard -l k8s-app=kubernetes-dashboard -o jsonpath='{.items[0].metadata.name}')
kubectl port-forward -n kubernetes-dashboard $POD 8443:8443
```
Then visit: `https://localhost:8443` (accept the certificate warning)

## Quick Commands Reference

```bash
# Start proxy
kubectl proxy

# Get new token
kubectl -n kubernetes-dashboard create token admin-user

# Check dashboard status
kubectl get pods -n kubernetes-dashboard

# View dashboard logs
kubectl logs -n kubernetes-dashboard -l k8s-app=kubernetes-dashboard
```

## Next Steps

- [Access Tekton Dashboard →](05-tekton-dashboard.md) (if using Tekton)
- [Jenkins Login →](06-jenkins-login.md) (if using Jenkins)
- [Troubleshooting →](99-troubleshooting.md)

---

**Note:** The Kubernetes Dashboard is different from the Tekton Dashboard. Tekton Dashboard only shows pipelines, while Kubernetes Dashboard shows all cluster resources.
