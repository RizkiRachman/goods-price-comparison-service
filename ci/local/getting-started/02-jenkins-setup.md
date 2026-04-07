# 02 - Jenkins Stack Setup (Docker Compose)

This is the **easier path** for beginners. Jenkins runs in Docker containers, and everything is managed by Docker Compose.

## What Gets Started?

| Service | URL | Purpose |
|---------|-----|---------|
| Jenkins | http://localhost:8082 | CI/CD Server |
| Application | http://localhost:8080 | Your Spring Boot app |
| Docker Registry | http://localhost:5000 | Store Docker images |
| Harbor UI | http://localhost:8081 | Container registry with UI |
| Kong Proxy | http://localhost:8000 | API Gateway |
| Kong Admin | http://localhost:8001 | Configure Kong |
| Prometheus | http://localhost:9090 | Metrics |
| Grafana | http://localhost:3000 | Dashboards |
| PostgreSQL | localhost:5432 | Database |

## Step-by-Step Setup

### Step 1: Navigate to CI Directory

```bash
cd /Users/rizkirachman/IdeaProjects/goods-price-comparison-service/ci/local
```

### Step 2: Start the Stack

```bash
./start.sh jenkins
```

This will:
1. Check Docker is running
2. Start all services with Docker Compose
3. Show you the URLs

**First time startup takes 2-3 minutes** as it downloads images.

### Step 3: Verify Services

```bash
./start.sh status
```

Should show all containers as "Up".

### Step 4: Access Jenkins

**URL:** http://localhost:8082  
**Username:** admin  
**Password:** admin123

### Step 5: Run Your First Build

1. Log into Jenkins
2. You'll see job: "goods-price-service-build"
3. Click on the job name
4. Click "Build Now" (left sidebar)
5. Watch the build progress in real-time

## Common Operations

### View Logs
```bash
./start.sh logs
# Or for specific service:
docker-compose logs -f jenkins
```

### Stop Everything
```bash
./start.sh stop
```

### Restart a Service
```bash
docker-compose restart jenkins
```

### Rebuild After Code Changes
1. Click "Build Now" in Jenkins
2. Wait for completion
3. Your app at http://localhost:8080 is updated!

## Jenkins Pipeline Stages

Your build goes through these stages:

```
1. Checkout → Pull code from GitHub
2. Build & Test → Maven compile + unit tests
3. Code Quality → SpotBugs + Checkstyle
4. Package → Create JAR file
5. Build Docker Image → Build & push to registry
6. Security Scan → Trivy vulnerability scan
7. Deploy to Local → Update running app
8. Smoke Tests → Verify app health
```

## API Gateway (Kong) Usage

### Test API Through Kong
```bash
# With API key (required)
curl -H "api-key: mobile-app-api-key-12345" \
  http://localhost:8000/v1/version

# Direct to app (no auth)
curl http://localhost:8080/v1/version
```

### View Kong Routes
```bash
curl http://localhost:8001/routes
curl http://localhost:8001/services
```

## Docker Registry Usage

### Push an Image
```bash
# Build and tag
docker build -t goods-price-service:latest ../..
docker tag goods-price-service:latest localhost:5000/goods-price-service:latest

# Push
docker push localhost:5000/goods-price-service:latest

# Verify
curl http://localhost:5000/v2/_catalog
```

## Monitoring

### Prometheus
- URL: http://localhost:9090
- Query metrics like: `up`, `http_server_requests_seconds_count`

### Grafana
- URL: http://localhost:3000
- Login: admin / admin
- Datasource: Prometheus (pre-configured)

## Troubleshooting Jenkins

### Build fails
```bash
# Check Jenkins logs
docker-compose logs jenkins

# Enter Jenkins container
docker-compose exec jenkins bash
```

### Cannot push to registry
Add to Docker Desktop → Settings → Docker Engine:
```json
{
  "insecure-registries": ["localhost:5000"]
}
```
Then "Apply & Restart"

## Next Steps

- [Login to Jenkins →](06-jenkins-login.md)
- [Try Kubernetes Dashboard →](04-k8s-dashboard.md) (optional)
- [Troubleshooting →](99-troubleshooting.md)

---

**Want to try Tekton instead?** [Tekton Setup →](03-tekton-setup.md)
