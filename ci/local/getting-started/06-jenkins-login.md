# 06 - Jenkins Login & First Build

This guide covers logging into Jenkins and running your first CI/CD build.

## Jenkins Login Information

| Field | Value |
|-------|-------|
| **URL** | http://localhost:8082 |
| **Username** | admin |
| **Password** | admin123 |

## Step-by-Step Login

### 1. Open Jenkins

In your browser, go to: **http://localhost:8082**

### 2. Login Screen

You'll see a login page with:
- Username field
- Password field
- "Sign in" button

**Enter:**
- Username: `admin`
- Password: `admin123`

Click **"Sign in"**

### 3. You're In!

The Jenkins dashboard shows:
- **Job list** (left side): "goods-price-service-build"
- **Build queue** (middle): Current and pending builds
- **Build history** (right side): Past builds

## Running Your First Build

### Method 1: Click "Build Now"

1. Click on **"goods-price-service-build"** job name
2. On the job page, look at left sidebar
3. Click **"Build Now"**
4. A new build appears in the "Build History" section (bottom left)

### Method 2: Watch It Run

1. After clicking "Build Now", click the **build number** (e.g., #1)
2. Click **"Console Output"** from the left menu
3. Watch the build progress in real-time!

## Understanding Build Stages

As you watch the console, you'll see these stages:

```
[Pipeline] Start of Pipeline
[Pipeline] Checkout → Pulling code from GitHub
[Pipeline] Build & Test → Maven compiling
[Pipeline] Code Quality → SpotBugs checking
[Pipeline] Package → Creating JAR
[Pipeline] Build Docker Image → Building container
[Pipeline] Security Scan → Trivy scanning
[Pipeline] Deploy to Local → Starting app
[Pipeline] Smoke Tests → Health checks
[Pipeline] End of Pipeline
```

**Green text** = success  
**Red text** = failure

## Build Results

After completion, Jenkins shows:

### Success (Green)
- Checkmark icon ✅
- Status: "SUCCESS"
- Your app is updated at http://localhost:8080

### Failure (Red)
- X icon ❌
- Status: "FAILURE"
- Check console output for error messages

## Viewing Build History

### On Main Dashboard
1. Left sidebar shows "goods-price-service-build"
2. Below it: build numbers (#1, #2, #3...)
3. Weather icon shows recent build health

### On Job Page
1. Click the job name
2. See full build history with:
   - Build number
   - Date/time
   - Duration
   - Status icon

## Exploring the Interface

### Left Sidebar Menu

| Menu Item | What It Does |
|-----------|--------------|
| **Status** | Overview of the job |
| **Changes** | Git commits since last build |
| **Workspace** | Files used by the build |
| **Build Now** | Trigger a new build |
| **Configure** | Edit pipeline settings |
| **Delete Pipeline** | Remove this job |
| **Pipeline Syntax** | Help for writing pipelines |

### Top Navigation

- **Jenkins** logo → Return to main dashboard
- **New Item** → Create new job
- **People** → User management
- **Build History** → All builds across jobs
- **Manage Jenkins** → System settings

## Jenkins Pipeline Stages Explained

### 1. Checkout
- Clones your GitHub repository
- Default: https://github.com/RizkiRachman/goods-price-comparison-service.git

### 2. Build & Test (Parallel)
- **Compile**: Maven compiles Java code
- **Unit Tests**: Runs JUnit tests
- Results published in Jenkins

### 3. Code Quality
- **SpotBugs**: Finds bugs in Java code
- **Checkstyle**: Enforces code style
- Fails build if issues found

### 4. Package
- Creates JAR file
- Archives it in Jenkins (downloadable)

### 5. Build Docker Image
- Builds container image
- Tags with build number and "latest"
- Pushes to local registry (localhost:5000)

### 6. Security Scan
- **Trivy**: Scans for CVEs in image
- Warnings don't fail build

### 7. Deploy to Local
- Updates docker-compose.yml
- Restarts app container
- Waits for health check

### 8. Smoke Tests
- Tests /health endpoint
- Tests /version endpoint
- Verifies deployment worked

## Jenkins Credentials

Pre-configured credentials (no need to add):

| ID | Type | Value |
|----|------|-------|
| docker-registry-credentials | Username/Password | admin/admin123 |
| gemini-api-key | Secret text | From environment |
| github-credentials | Username/Password | From environment |

### View/Edit Credentials

1. Click **"Manage Jenkins"** (top menu)
2. Click **"Credentials"**
3. Click **"System"** → **"Global credentials"**
4. Click any credential to view/edit

## Blue Ocean Interface (Alternative UI)

Jenkins has a modern UI called Blue Ocean:

1. On any job page, click **"Open Blue Ocean"** (left sidebar)
2. Or directly: http://localhost:8082/blue

**Features:**
- Visual pipeline stages
- Modern interface
- Real-time progress
- Better for presentations

## Common Operations

### Stop a Running Build

1. Go to the build page (click build number)
2. Click **"Stop"** button (top left)

### Replay a Build

1. Go to the build page
2. Click **"Replay"** (left sidebar)
3. Modify pipeline script if needed
4. Click "Run"

### View Test Results

1. Go to build page
2. Click **"Test Results"** (left sidebar)
3. See passed/failed tests

### Download Artifacts

1. Go to build page
2. Click **"Artifacts"** (if available)
3. Click file to download

## Troubleshooting Builds

### Build fails at "Checkout"
- Check internet connection
- Verify GitHub repository exists
- Check credentials

### Build fails at "Build & Test"
- Check Maven code for compilation errors
- Run tests locally: `mvn test`

### Build fails at "Build Docker Image"
- Check Dockerfile exists in project root
- Verify Docker is running

### Build fails at "Deploy to Local"
- Check if app container starts: `docker ps`
- Check app logs: `docker-compose logs app`

## Next Steps

- [Troubleshooting →](99-troubleshooting.md)
- [Try Kubernetes Dashboard →](04-k8s-dashboard.md)
- [Try Tekton Instead →](03-tekton-setup.md)

---

**Questions?** Check the [Troubleshooting Guide →](99-troubleshooting.md)
