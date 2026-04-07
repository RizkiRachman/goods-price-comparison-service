# Local CI/CD Stack

This directory contains a complete local CI/CD stack that simulates production tools:

| Production Tool | Local Alternative | Purpose | URL |
|----------------|------------------|---------|-----|
| **Quay** | Docker Registry + Harbor | Container Registry | http://localhost:5000, http://localhost:8081 |
| **Harness** | Jenkins | CI/CD Pipeline | http://localhost:8082 |
| **OpenShift** | Docker Compose | Container Platform | Local Docker |
| **Gravitee** | Kong | API Gateway | http://localhost:8000, http://localhost:8001 (Admin) |
| **Monitoring** | Prometheus + Grafana | Observability | http://localhost:9090, http://localhost:3000 |

## Quick Start

### Prerequisites

```bash
# Install Docker Desktop
# https://www.docker.com/products/docker-desktop

# Verify Docker is running
docker --version
docker-compose --version
```

### Start the Stack

```bash
# Navigate to CI directory
cd ci/local

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps
```

### Access Services

| Service | URL | Credentials |
|---------|-----|-------------|
| **Application** | http://localhost:8080 | - |
| **Docker Registry** | http://localhost:5000 | - |
| **Harbor UI** | http://localhost:8081 | admin/Harbor12345 |
| **Jenkins** | http://localhost:8082 | admin/admin123 |
| **Kong Admin** | http://localhost:8001 | - |
| **Kong Proxy** | http://localhost:8000 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **PostgreSQL** | localhost:5432 | postgres/postgres |

## Jenkins CI/CD Pipeline

### Setup

1. Access Jenkins at http://localhost:8082
2. Login with: `admin` / `admin123`
3. The pipeline job `goods-price-service-build` is pre-configured

### Pipeline Stages

```
1. Checkout → Pull latest code
2. Build & Test → Compile + Unit tests (parallel)
3. Code Quality → SpotBugs + Checkstyle
4. Package → Create JAR artifact
5. Build Docker Image → Build + Push to local registry
6. Security Scan → Trivy vulnerability scan
7. Deploy to Local → Deploy via docker-compose
8. Smoke Tests → Health check endpoints
```

### Manual Build

```bash
# Trigger build via Jenkins CLI or UI
# Or use the Jenkinsfile directly:
cd ../..
docker run -v $(pwd):/workspace -w /workspace \
  -v /var/run/docker.sock:/var/run/docker.sock \
  maven:3.9-eclipse-temurin-17 \
  mvn clean package
```

## Kong API Gateway

### Configuration

Kong is configured in DB-less mode with declarative configuration (`kong/kong.yml`):

- **Rate Limiting**: 60 requests/minute per client
- **Authentication**: API Key required
- **CORS**: Enabled for all origins
- **Logging**: Access logs to `/tmp/kong-access.log`

### API Keys

| Client | API Key |
|--------|---------|
| Mobile App | `mobile-app-api-key-12345` |
| Web Dashboard | `web-dashboard-api-key-67890` |
| Admin Portal | `admin-api-key-abcde` |

### Example Usage

```bash
# Call API through Kong (requires API key)
curl -H "api-key: mobile-app-api-key-12345" \
  http://localhost:8000/v1/version

# Call without key (should fail)
curl http://localhost:8000/v1/version
# → 401 Unauthorized
```

### Kong Admin API

```bash
# View routes
curl http://localhost:8001/routes

# View services
curl http://localhost:8001/services

# View consumers
curl http://localhost:8001/consumers
```

## Docker Registry

### Push Image

```bash
# Tag image
docker tag goods-price-service:latest localhost:5000/goods-price-service:latest

# Push to local registry
docker push localhost:5000/goods-price-service:latest

# Verify
curl http://localhost:5000/v2/_catalog
curl http://localhost:5000/v2/goods-price-service/tags/list
```

### Pull Image

```bash
# Pull from local registry
docker pull localhost:5000/goods-price-service:latest
```

## Monitoring

### Prometheus Metrics

Application exposes metrics at:
- http://localhost:8080/actuator/prometheus

Available metrics:
- JVM metrics (memory, threads, GC)
- HTTP request duration/count
- Custom business metrics

### Grafana Dashboards

1. Access Grafana: http://localhost:3000 (admin/admin)
2. Prometheus datasource is pre-configured
3. Import dashboards or create custom ones

### Useful Queries

```promql
# Request rate
rate(http_server_requests_seconds_count[5m])

# Average response time
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])

# JVM memory usage
jvm_memory_used_bytes / jvm_memory_max_bytes
```

## Troubleshooting

### Services Not Starting

```bash
# Check logs
docker-compose logs [service-name]

# Restart specific service
docker-compose restart jenkins

# Full reset (WARNING: deletes data)
docker-compose down -v
docker-compose up -d
```

### Port Conflicts

If ports are already in use:

```bash
# Find what's using port 8080
lsof -i :8080

# Edit docker-compose.yml to change ports
# Example: change "8080:8080" to "8081:8080"
```

### Jenkins Build Fails

```bash
# Check Jenkins logs
docker-compose logs jenkins

# Enter Jenkins container
docker-compose exec jenkins bash

# Check Maven in container
which mvn
mvn -v
```

### Registry Access Denied

```bash
# Docker requires insecure registry for localhost:5000
# Add to Docker Desktop settings → Daemon → Insecure registries:
# localhost:5000

# Or configure in /etc/docker/daemon.json:
{
  "insecure-registries": ["localhost:5000"]
}
```

## Cleanup

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose down -v

# Remove all images
docker-compose down --rmi all

# Clean up Docker system
docker system prune -a
```

## Advanced Usage

### Scaling Services

```bash
# Scale app to 3 instances
docker-compose up -d --scale app=3
```

### Custom Environment Variables

Create `.env` file in `ci/local/`:

```bash
REGISTRY_URL=localhost:5000
IMAGE_NAME=my-custom-name
GEMINI_API_KEY=your-key-here
```

### Integration with IDE

**IntelliJ IDEA:**
1. Install "Docker" plugin
2. View → Tool Windows → Docker
3. Connect to local Docker daemon
4. Manage containers from IDE

**VS Code:**
1. Install "Docker" extension
2. Use Docker panel to manage containers

## Production Comparison

| Aspect | Local Stack | Production |
|--------|------------|------------|
| Registry | Docker Registry / Harbor | Quay Enterprise |
| CI/CD | Jenkins | Harness / Tekton |
| Platform | Docker Compose | OpenShift / Kubernetes |
| Gateway | Kong | Gravitee / Kong Enterprise |
| Database | PostgreSQL single | PostgreSQL HA |
| Monitoring | Prometheus + Grafana | Datadog / Dynatrace |

## Next Steps

1. ✅ Set up local stack
2. ✅ Run Jenkins pipeline
3. 🔄 Configure production-like environments
4. 🔄 Add more monitoring/alerting
5. 🔄 Implement GitOps with ArgoCD

## Support

For issues or questions:
- Check service logs: `docker-compose logs [service]`
- Review [main README](../../README.md)
- Check [API documentation](../../docs/API.md)
