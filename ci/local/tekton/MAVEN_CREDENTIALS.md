# Fixing Maven GitHub Packages Authentication

## Problem

When building the project in a Docker container (via Tekton pipeline), Maven fails to download the `goods-price-comparison-api` dependency from GitHub Packages with a **401 Unauthorized** error:

```
[ERROR] Failed to execute goal on project goods-price-comparison-service: Could not collect dependencies
[ERROR] Failed to read artifact descriptor for com.example:goods-price-comparison-api:jar:1.2.3
[ERROR] The following artifacts could not be resolved: com.example:goods-price-comparison-api:pom:1.2.3
[ERROR] from/to github (https://maven.pkg.github.com/RizkiRachman/goods-price-comparison-api)
[ERROR] status code: 401, reason phrase: Unauthorized (401)
```

## Root Cause

- Your project depends on a private GitHub Maven repository
- Maven needs authentication credentials (GitHub username + PAT) to access GitHub Packages
- These credentials were missing in the Tekton pipeline execution environment

## Solution Overview

We've implemented a secure credential management system using:

1. **Kubernetes Secret** - Stores GitHub credentials securely
2. **ConfigMap** - Contains Maven `settings.xml` configuration
3. **Updated Pipeline** - Uses these credentials during the build

## Step-by-Step Setup

### Step 1: Create GitHub Personal Access Token (PAT)

1. Go to https://github.com/settings/tokens/new
2. Give it a descriptive name, e.g., "Maven Package Access"
3. **Important**: Select **only** the `read:packages` scope
4. Click "Generate token"
5. **Copy the token immediately** - you won't see it again!

### Step 2: Create Kubernetes Credentials Secret

Run the automated setup script:

```bash
cd ci/local/tekton
./setup-maven-credentials.sh
```

This will prompt you to enter:
- Your GitHub username
- Your GitHub Personal Access Token (PAT)

**Interactive Mode Example:**
```bash
$ ./setup-maven-credentials.sh
[INFO] Checking prerequisites...
✓ Prerequisites check passed

=== Maven GitHub Credentials Setup ===

Step 1: GitHub Username
--------
Enter your GitHub username: RizkiRachman
✓ Credentials verified!
```

Or **non-interactive mode**:
```bash
./setup-maven-credentials.sh RizkiRachman github_pat_xxxxxxxxxxxxx
```

### Step 3: Verify Setup

Verify the credentials were created correctly:

```bash
./setup-maven-credentials.sh verify
```

This will:
- ✓ Check if the Kubernetes secret exists
- ✓ Check if the ConfigMap exists
- ✓ Test credentials against GitHub API
- ✓ Confirm Maven can access the repository

### Step 4: Run the Pipeline

Once credentials are configured, run the pipeline:

```bash
# Using PipelineRun
kubectl create -f pipeline-run.yaml -n goods-price-ci

# Or using tkn CLI
tkn pipeline start goods-price-service-pipeline \
  --showlog \
  -n goods-price-ci \
  -w name=shared-workspace,volumeClaimTemplateFile=workspace-template.yaml \
  -w name=maven-settings,config=maven-settings
```

Monitor the build:

```bash
# Watch logs in real-time
tkn pipelinerun logs -f -n goods-price-ci

# Or check dashboard
./install-dashboard.sh proxy
# Visit: http://localhost:9097
```

## Files Changed/Created

### New Files:
1. **`maven-credentials-secret.yaml`** - Template for Kubernetes secret
2. **`maven-settings-configmap.yaml`** - Maven settings.xml as ConfigMap
3. **`setup-maven-credentials.sh`** - Automated setup script

### Modified Files:
1. **`pipeline.yaml`** - Updated Maven build task to use workspace bindings
2. **`pipeline-run.yaml`** - Now includes maven-settings workspace binding

## How It Works

### Kubernetes Secret (github-maven-credentials)
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: github-maven-credentials
  namespace: goods-price-ci
type: Opaque
stringData:
  username: "RizkiRachman"
  token: "github_pat_xxxxxxxxxxxxx"
```

**Usage**: Referenced in the pipeline task as environment variables:
```yaml
env:
  - name: GITHUB_USERNAME
    valueFrom:
      secretKeyRef:
        name: github-maven-credentials
        key: username
  - name: GITHUB_TOKEN
    valueFrom:
      secretKeyRef:
        name: github-maven-credentials
        key: token
```

### ConfigMap (maven-settings)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: maven-settings
  namespace: goods-price-ci
data:
  settings.xml: |
    <settings>
      <servers>
        <server>
          <id>github</id>
          <username>${env.GITHUB_USERNAME}</username>
          <password>${env.GITHUB_TOKEN}</password>
        </server>
      </servers>
      ...
    </settings>
```

**Usage**: Mounted as a workspace and passed to Maven with `-s` flag:
```bash
mvn clean package -s $(workspaces.maven-settings.path)/settings.xml
```

### Maven Build Task
The pipeline's maven-build task now:
1. Injects GitHub credentials as environment variables
2. Mounts the maven-settings ConfigMap
3. Passes settings.xml to Maven build command
4. Maven uses settings.xml to authenticate with GitHub Packages

## Troubleshooting

### Issue: 401 Unauthorized Still Happening

**Check 1: Verify Secret Exists**
```bash
kubectl get secret github-maven-credentials -n goods-price-ci
kubectl describe secret github-maven-credentials -n goods-price-ci
```

**Check 2: Verify PAT Has Correct Scope**
- Token must have `read:packages` scope
- Go to https://github.com/settings/tokens to verify
- If incorrect, delete and create a new PAT

**Check 3: Check Maven Build Logs**
```bash
# Get the maven build pod
kubectl get pods -n goods-price-ci | grep maven

# View logs
kubectl logs <maven-pod-name> -n goods-price-ci

# Look for lines like:
# [INFO] Using custom Maven settings from workspace
# [DEBUG] Artifact resolution: com.example:goods-price-comparison-api
```

**Check 4: Re-run Setup**
```bash
./setup-maven-credentials.sh delete
./setup-maven-credentials.sh  # Interactive setup
./setup-maven-credentials.sh verify
```

### Issue: ConfigMap Not Found

```bash
# Apply the ConfigMap
kubectl apply -f maven-settings-configmap.yaml -n goods-price-ci

# Verify
kubectl get configmap maven-settings -n goods-price-ci
kubectl get configmap maven-settings -n goods-price-ci -o yaml
```

### Issue: Pipeline Still Can't Access Repository

1. **Check Pod Logs**:
   ```bash
   kubectl logs -n goods-price-ci -f <pipelinerun-pod>
   ```

2. **Check Error Details**:
   ```bash
   kubectl describe pipelinerun -n goods-price-ci <pipelinerun-name>
   kubectl describe taskrun -n goods-price-ci <taskrun-name>
   ```

3. **Test Credentials Manually**:
   ```bash
   # Get the secret values
   kubectl get secret github-maven-credentials -n goods-price-ci -o yaml
   
   # Test with curl
   USERNAME=$(kubectl get secret github-maven-credentials -n goods-price-ci \
     -o jsonpath='{.data.username}' | base64 -d)
   TOKEN=$(kubectl get secret github-maven-credentials -n goods-price-ci \
     -o jsonpath='{.data.token}' | base64 -d)
   
   curl -H "Authorization: token $TOKEN" \
     https://api.github.com/user
   ```

## Security Best Practices

### ✓ DO:
- Use a fine-grained PAT with only `read:packages` scope
- Store PAT in Kubernetes secrets (done automatically by setup script)
- Rotate PAT periodically
- Use GitHub Actions secrets for CI/CD pipelines
- Keep PAT values out of logs and version control

### ✗ DON'T:
- Commit PAT to Git repositories
- Use personal access tokens in development locally
- Share PAT values in Slack/email
- Use overly broad token scopes
- Store credentials in config files

## For Local Development (Outside Kubernetes)

If you need to build locally without using Tekton:

```bash
# Create a local settings.xml
cat > ~/.m2/settings.xml << 'EOF'
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PAT</password>
    </server>
  </servers>
</settings>
EOF

# Build with Maven
mvn clean package
```

Or use environment variables:
```bash
export GITHUB_USERNAME=your_username
export GITHUB_TOKEN=your_pat
mvn clean package -s settings.xml
```

## Additional Resources

- [GitHub Packages Documentation](https://docs.github.com/en/packages)
- [Maven Settings Reference](https://maven.apache.org/settings.html)
- [Tekton Workspaces](https://tekton.dev/docs/pipelines/workspaces/)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)

## Support

If you encounter issues:

1. Run the verify script: `./setup-maven-credentials.sh verify`
2. Check pod logs: `kubectl logs -n goods-price-ci <pod-name>`
3. Check Tekton Dashboard: `./install-dashboard.sh proxy`
4. Review troubleshooting section above

## Next Steps

1. ✅ Create GitHub PAT
2. ✅ Run setup script
3. ✅ Verify credentials
4. ✅ Run pipeline
5. 🔄 Monitor build progress
6. 🔄 Deploy application

