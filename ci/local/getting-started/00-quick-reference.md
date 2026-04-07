# Quick Reference Card

## Essential Commands

### Jenkins Stack (Docker Compose)

```bash
cd ci/local

# Start
./start.sh jenkins

# Stop
./start.sh stop

# Status
./start.sh status

# Logs
./start.sh logs
```

### Tekton Stack (Kubernetes)

```bash
cd ci/local

# Start
./start.sh tekton

# Stop
./start.sh tekton-stop

# Dashboard proxy
./tekton/install-dashboard.sh proxy

# Dashboard proxy (auto-restart)
./tekton/install-dashboard.sh proxy-always
```

## URLs and Credentials

### Jenkins Stack

| Service | URL | Username | Password |
|---------|-----|----------|----------|
| Jenkins | http://localhost:8082 | admin | admin123 |
| App | http://localhost:8080 | - | - |
| Grafana | http://localhost:3000 | admin | admin |
| Kong Admin | http://localhost:8001 | - | - |
| Registry | http://localhost:5000 | - | - |

### Tekton Stack

| Service | URL | Login Required |
|---------|-----|----------------|
| Tekton Dashboard | http://localhost:9097 | No |
| K8s Dashboard | http://localhost:8001 | Yes (token) |
| App (port-forward) | http://localhost:8080 | - |

## kubectl Commands

```bash
# Get pods
kubectl get pods -n <namespace>

# View logs
kubectl logs -n <namespace> <pod-name>

# Follow logs
kubectl logs -f -n <namespace> <pod-name>

# Port forward
kubectl port-forward -n <namespace> svc/<name> <local>:<remote>

# Proxy (for dashboard access - more stable)
kubectl proxy --port=8081
# Then: http://localhost:8081/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/

# Describe
kubectl describe pod -n <namespace> <pod-name>

# Delete
kubectl delete -n <namespace> <resource> <name>
```

## Tekton Commands

```bash
# List pipelines
tkn pipeline list -n goods-price-ci

# List pipeline runs
tkn pipelinerun list -n goods-price-ci

# Start pipeline
tkn pipeline start goods-price-service-pipeline \
  --showlog -n goods-price-ci \
  -w name=shared-workspace,volumeClaimTemplateFile=tekton/workspace-template.yaml \
  -w name=maven-cache,claimName=maven-cache-pvc

# View logs
tkn pipelinerun logs -f -n goods-price-ci <name>
```

## Docker Commands

```bash
# List containers
docker ps

# List all containers (including stopped)
docker ps -a

# View logs
docker logs <container>

# Follow logs
docker logs -f <container>

# Exec into container
docker exec -it <container> /bin/sh

# Stop container
docker stop <container>

# Remove container
docker rm <container>

# List images
docker images
```

## kind Commands

```bash
# List clusters
kind get clusters

# Delete cluster
kind delete cluster --name goods-price-ci

# Export kubeconfig
kind export kubeconfig --name goods-price-ci

# Load image into cluster
kind load docker-image <image>:<tag> --name goods-price-ci
```

## Files and Locations

```
ci/local/
├── start.sh                    # Main startup script
├── docker-compose.yml          # Jenkins stack
├── getting-started/            # Newbie guides
│   ├── README.md
│   ├── 01-prerequisites.md
│   ├── 02-jenkins-setup.md
│   ├── 03-tekton-setup.md
│   ├── 04-k8s-dashboard.md
│   ├── 05-tekton-dashboard.md
│   ├── 06-jenkins-login.md
│   └── 99-troubleshooting.md
├── tekton/
│   ├── pipeline.yaml
│   ├── pipeline-run.yaml
│   ├── maven-cache-pvc.yaml
│   ├── workspace-template.yaml
│   ├── install-dashboard.sh
│   └── README.md
└── jenkins/
    ├── casc.yml
    ├── Jenkinsfile
    └── plugins.txt
```

## Namespaces

| Stack | Namespace | Purpose |
|-------|-----------|---------|
| Tekton | tekton-pipelines | System components |
| Tekton | goods-price-ci | Your pipeline runs |
| K8s Dashboard | kubernetes-dashboard | Dashboard components |

## Common Issues Quick Fixes

| Issue | Fix |
|-------|-----|
| Permission denied | `chmod +x start.sh` |
| Port in use | `lsof -i :<port>` then `kill -9 <PID>` |
| Docker not running | Open Docker Desktop |
| Kubectl not found | `brew install kubectl` |
| Kind not found | `brew install kind` |
| Registry push fails | Add `localhost:5000` to insecure registries in Docker Desktop |
| Token expired | `kubectl -n kubernetes-dashboard create token admin-user` |

## Need Help?

1. Check the [Troubleshooting Guide](99-troubleshooting.md)
2. Read the full guides in [getting-started/](README.md)
3. View service logs
