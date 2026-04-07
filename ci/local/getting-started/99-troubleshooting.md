# 99 - Troubleshooting Guide

Common issues and their solutions for the local CI/CD stack.

## Quick Diagnostic Commands

```bash
# Check if Docker is running
docker ps

# Check all services (Jenkins stack)
cd ci/local && ./start.sh status

# Check Tekton pods
kubectl get pods -n tekton-pipelines

# Check Kubernetes Dashboard pod
kubectl get pods -n kubernetes-dashboard

# Check your app
kubectl get pods -n goods-price-ci
```

---

## Docker Issues

### "Cannot connect to the Docker daemon"
**Cause:** Docker Desktop isn't running

**Fix:**
1. Open Docker Desktop from Applications
2. Wait for whale icon to stop animating
3. Try again

### "Permission denied" on start.sh
**Cause:** Script not executable

**Fix:**
```bash
chmod +x ci/local/start.sh
chmod +x ci/local/tekton/install-dashboard.sh
```

### "Port already in use"
**Cause:** Another service is using the port

**Fix:**
```bash
# Find what's using the port (example: 8080)
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in docker-compose.yml
```

---

## Jenkins Issues

### Jenkins won't start

**Check logs:**
```bash
cd ci/local
docker-compose logs jenkins
```

**Common causes:**
1. Port 8082 in use
2. Docker not running
3. Plugin installation failed

**Fix:**
```bash
# Stop and restart
docker-compose down
docker-compose up -d jenkins

# Or full reset (WARNING: loses data)
docker-compose down -v
docker-compose up -d
```

### Build fails at "Checkout"

**Error:** `Error cloning remote repo`

**Fix:**
1. Check internet connection
2. Verify GitHub repo URL in Jenkinsfile
3. Check if credentials are configured

### Build fails at "Build & Test"

**Error:** Compilation failed or tests failed

**Fix:**
```bash
# Test locally first
cd /Users/rizkirachman/IdeaProjects/goods-price-comparison-service
mvn clean test
```

### Build fails at "Build Docker Image"

**Error:** `Cannot connect to Docker daemon`

**Fix:**
1. Check Dockerfile exists: `ls Dockerfile`
2. Mount Docker socket properly in docker-compose.yml
3. Jenkins container needs: `/var/run/docker.sock:/var/run/docker.sock`

### Cannot push to registry

**Error:** `http: server gave HTTP response to HTTPS client`

**Fix:**

**Option 1: Configure Docker Desktop**
1. Open Docker Desktop
2. Settings → Docker Engine
3. Add to JSON:
```json
{
  "insecure-registries": ["localhost:5000"]
}
```
4. Click "Apply & Restart"

**Option 2: Use host network**
```bash
# Tag with host.docker.internal
docker tag myimage host.docker.internal:5000/myimage
docker push host.docker.internal:5000/myimage
```

---

## Tekton Issues

### "kubectl command not found"
**Fix:**
```bash
brew install kubectl
```

### "kind command not found"
**Fix:**
```bash
brew install kind
```

### kind cluster won't start

**Check:**
```bash
# See existing clusters
kind get clusters

# Check cluster status
docker ps | grep goods-price-ci
```

**Fix:**
```bash
# Delete and recreate
kind delete cluster --name goods-price-ci
./start.sh tekton
```

### Tekton pods not ready

**Check:**
```bash
kubectl get pods -n tekton-pipelines
```

**Fix - Wait longer:**
```bash
kubectl wait --for=condition=ready pod -l app.kubernetes.io/part-of=tekton-pipelines -n tekton-pipelines --timeout=300s
```

**Fix - Restart:**
```bash
kubectl delete pods -n tekton-pipelines -l app.kubernetes.io/part-of=tekton-pipelines
```

### PipelineRun stuck

**Check status:**
```bash
kubectl describe pipelinerun -n goods-price-ci <name>
```

**Common issues:**
1. **Pending** → No worker nodes available
2. **Running** but slow → Resource constraints
3. **Failed** → Check task logs

### git-clone task fails

**Check:**
```bash
# Can you reach GitHub?
curl https://github.com

# Is the repo URL correct?
kubectl get task git-clone -n goods-price-ci -o yaml | grep url
```

### maven-build task fails

**Check:**
1. Maven cache PVC exists:
```bash
kubectl get pvc maven-cache-pvc -n goods-price-ci
```

2. If missing:
```bash
kubectl apply -f ci/local/tekton/maven-cache-pvc.yaml
```

### Kaniko (docker-build) fails

**Error:** `error pushing image`

**Fix:**
- Registry must be accessible from inside kind cluster
- For local registry, use cluster IP or service name
- Check: `kubectl get svc -n goods-price-ci`

---

## Kubernetes Dashboard Issues

### "Token not found" or "Unauthorized"

**Get new token:**
```bash
kubectl -n kubernetes-dashboard create token admin-user
```

### Dashboard won't load

**Check:**
```bash
kubectl get pods -n kubernetes-dashboard
```

**Fix:**
```bash
# Reinstall
kubectl delete -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
```

### "Forbidden (403)" error

**Fix - Create admin user:**
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

---

## Tekton Dashboard Issues

### "Connection lost" / "broken pipe"

**This is normal!** Browser connections time out when using port-forward. Dashboard still works.

**Best Solution: Use kubectl proxy instead (no broken pipe errors)**
```bash
kubectl proxy --port=8081
```
Then open: `http://localhost:8081/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/`

**Alternative: Auto-restart port-forward**
```bash
./tekton/install-dashboard.sh proxy-always
```

This automatically restarts the connection every 2 seconds if it fails.

### Dashboard shows "No pipelines found"

**Check namespace:**
```bash
# Should be goods-price-ci
kubectl get pipelines -n goods-price-ci

# If empty, apply:
kubectl apply -f ci/local/tekton/pipeline.yaml
```

### Dashboard not installed

**Install:**
```bash
./tekton/install-dashboard.sh install
```

### Dashboard blank page

**Fix:**
1. Hard refresh: `Cmd+Shift+R` (Mac) or `Ctrl+F5`
2. Check console for errors (F12 → Console)
3. Try incognito/private window

---

## Kong / API Gateway Issues

### Cannot reach API through Kong

**Check:**
```bash
# Kong running?
docker ps | grep kong

# Routes configured?
curl http://localhost:8001/routes
```

### API key not working

**Check Kong config:**
```bash
curl http://localhost:8001/consumers
curl http://localhost:8001/key-auths
```

**Test with key:**
```bash
curl -H "api-key: mobile-app-api-key-12345" http://localhost:8000/v1/version
```

---

## Registry Issues

### Cannot push to localhost:5000

**Check registry:**
```bash
curl http://localhost:5000/v2/_catalog
```

**Fix insecure registry:**
```bash
# Mac - edit Docker Desktop settings
# Linux - edit /etc/docker/daemon.json
{
  "insecure-registries": ["localhost:5000", "host.docker.internal:5000"]
}
```

---

## Common kubectl Commands

```bash
# Get all resources
kubectl get all -n goods-price-ci

# Describe resource (shows events)
kubectl describe pod <pod-name> -n goods-price-ci

# View logs
kubectl logs -n goods-price-ci <pod-name>

# Follow logs (real-time)
kubectl logs -f -n goods-price-ci <pod-name>

# Previous container logs (if crashed)
kubectl logs -n goods-price-ci <pod-name> --previous

# Exec into container
kubectl exec -it -n goods-price-ci <pod-name> -- /bin/sh

# Delete resource
kubectl delete pod -n goods-price-ci <pod-name>

# Port-forward
kubectl port-forward -n goods-price-ci svc/<service-name> 8080:8080
```

---

## Reset Everything

### Reset Jenkins Stack
```bash
cd ci/local
docker-compose down -v  # Removes volumes too!
docker-compose up -d
```

### Reset Tekton Stack
```bash
kind delete cluster --name goods-price-ci
./start.sh tekton
```

### Full Docker Cleanup (DANGER)
```bash
# Stop all containers
docker stop $(docker ps -q)

# Remove all containers
docker rm $(docker ps -a -q)

# Remove all images
docker rmi $(docker images -q)

# Clean system
docker system prune -a
```

---

## Still Having Issues?

1. Check service logs:
   - Jenkins: `docker-compose logs`
   - Tekton: `kubectl logs -n <namespace>`

2. Verify prerequisites:
   - [Prerequisites Guide →](01-prerequisites.md)

3. Check specific guides:
   - [Jenkins Setup →](02-jenkins-setup.md)
   - [Tekton Setup →](03-tekton-setup.md)
   - [Kubernetes Dashboard →](04-k8s-dashboard.md)
   - [Tekton Dashboard →](05-tekton-dashboard.md)

4. Common solutions:
   - Restart Docker Desktop
   - Delete and recreate cluster
   - Check internet connection
   - Verify port availability
