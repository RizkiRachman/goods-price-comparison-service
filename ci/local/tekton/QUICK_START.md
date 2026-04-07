# Quick Start: Maven GitHub Credentials

## TL;DR (2 minutes)

### 1. Create GitHub Token
Go to https://github.com/settings/tokens/new
- Name: "Maven Package Access"
- Scope: Select `read:packages` only
- Copy the generated token

### 2. Setup Credentials
```bash
cd ci/local/tekton
./setup-maven-credentials.sh
# Enter: username, PAT
# Verify when prompted
```

### 3. Run Pipeline
```bash
kubectl create -f pipeline-run.yaml -n goods-price-ci
```

### 4. Watch Logs
```bash
tkn pipelinerun logs -f -n goods-price-ci
```

---

## Detailed Steps

### Step 1: GitHub Personal Access Token (PAT)

1. Open https://github.com/settings/tokens/new in your browser
2. Fill in:
   - **Note**: "Maven Package Access" (or similar name)
3. Select scopes:
   - ✓ `read:packages` (required)
   - Other scopes are NOT needed
4. Click "Generate token"
5. **COPY THE TOKEN** (won't show again!)
   - Save it temporarily in a text editor

### Step 2: Configure Kubernetes Credentials

Navigate to the tekton directory:
```bash
cd ci/local/tekton
```

Run the setup script:
```bash
./setup-maven-credentials.sh
```

When prompted:
```
Enter your GitHub username: <YOUR_USERNAME>
Paste your GitHub PAT (hidden): <PASTE_YOUR_TOKEN>
Continue? (yes/no): yes
```

Verify it worked:
```bash
./setup-maven-credentials.sh verify

# Should show:
# ✓ Secret 'github-maven-credentials' exists
# ✓ ConfigMap 'maven-settings' exists
# ✓ GitHub credentials are valid
# Authenticated as: ...
```

### Step 3: Start Your Pipeline

Option A: Using kubectl
```bash
kubectl create -f pipeline-run.yaml -n goods-price-ci
```

Option B: Using tkn CLI
```bash
tkn pipeline start goods-price-service-pipeline \
  --showlog \
  -n goods-price-ci \
  -w name=shared-workspace,volumeClaimTemplateFile=workspace-template.yaml \
  -w name=maven-settings,config=maven-settings
```

### Step 4: Monitor Build Progress

Real-time logs:
```bash
tkn pipelinerun logs -f -n goods-price-ci
```

List all runs:
```bash
tkn pipelinerun list -n goods-price-ci
```

View specific run:
```bash
tkn pipelinerun describe <pipelinerun-name> -n goods-price-ci
```

Web dashboard:
```bash
./install-dashboard.sh proxy
# Open: http://localhost:9097
```

---

## Common Issues

### ❌ "401 Unauthorized" Error
**Problem**: Token doesn't work
**Solution**:
```bash
# Delete and recreate
./setup-maven-credentials.sh delete
./setup-maven-credentials.sh
./setup-maven-credentials.sh verify
```

Check your PAT has `read:packages` scope:
- Go to https://github.com/settings/tokens
- Click on the token
- Verify scope includes `read:packages`

### ❌ "Secret not found"
**Problem**: Credentials not configured
**Solution**:
```bash
./setup-maven-credentials.sh verify
# Should tell you what's missing
```

### ❌ "ConfigMap not found"
**Problem**: Maven settings not applied
**Solution**:
```bash
kubectl apply -f maven-settings-configmap.yaml -n goods-price-ci
kubectl get configmap maven-settings -n goods-price-ci
```

### ❌ Maven build still failing after credentials
**Problem**: Look at the build logs
**Solution**:
```bash
# Get the maven pod name
MAVEN_POD=$(kubectl get pods -n goods-price-ci -l tekton.dev/task=maven-build \
  --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1].metadata.name}')

# View full build logs
kubectl logs $MAVEN_POD -n goods-price-ci

# Look for these lines (should appear early):
# [INFO] Using custom Maven settings from workspace
# [INFO] Downloading from github: https://maven.pkg.github.com/...
```

---

## What Gets Created

When you run the setup, you get:

| Resource | Type | Purpose |
|----------|------|---------|
| `github-maven-credentials` | Secret | Stores GitHub username & token |
| `maven-settings` | ConfigMap | Contains settings.xml for Maven |
| Workspace binding | PipelineRun | Mounts settings.xml into build pod |

---

## Cleanup

If you need to start over:

```bash
# Delete credentials
./setup-maven-credentials.sh delete

# Or manually
kubectl delete secret github-maven-credentials -n goods-price-ci
kubectl delete configmap maven-settings -n goods-price-ci
```

---

## More Help

- Full documentation: `MAVEN_CREDENTIALS.md`
- Setup script help: `./setup-maven-credentials.sh help`
- Troubleshooting: See MAVEN_CREDENTIALS.md → Troubleshooting section

