#!/bin/bash

# Maven GitHub Credentials Setup Checklist
# Use this file to track your progress through the setup

echo "================================"
echo "Maven GitHub Credentials Setup"
echo "Checklist"
echo "================================"
echo ""

# Check 1: Read documentation
echo "□ Step 1: Read the documentation"
echo "  Files to review:"
echo "  - ci/local/tekton/QUICK_START.md (2 minutes)"
echo "  - ci/local/tekton/MAVEN_CREDENTIALS.md (comprehensive)"
echo ""

# Check 2: Create GitHub PAT
echo "□ Step 2: Create GitHub Personal Access Token"
echo "  - Go to: https://github.com/settings/tokens/new"
echo "  - Name: 'Maven Package Access'"
echo "  - Scope: Select 'read:packages' ONLY"
echo "  - Click 'Generate token'"
echo "  - Copy the token (you won't see it again!)"
echo ""

# Check 3: Navigate to tekton directory
echo "□ Step 3: Navigate to tekton directory"
echo "  $ cd ci/local/tekton"
echo ""

# Check 4: Run setup script
echo "□ Step 4: Run the setup script"
echo "  $ ./setup-maven-credentials.sh"
echo "  Enter your GitHub username when prompted"
echo "  Paste your GitHub PAT when prompted"
echo ""

# Check 5: Verify credentials
echo "□ Step 5: Verify credentials were created"
echo "  $ ./setup-maven-credentials.sh verify"
echo "  OR"
echo "  $ ./run-pipeline.sh maven-verify"
echo ""

# Check 6: Run pipeline
echo "□ Step 6: Run the pipeline"
echo "  $ kubectl create -f pipeline-run.yaml -n goods-price-ci"
echo ""

# Check 7: Monitor build
echo "□ Step 7: Monitor the build"
echo "  Option A (CLI):"
echo "    $ tkn pipelinerun logs -f -n goods-price-ci"
echo "  Option B (Dashboard):"
echo "    $ ./install-dashboard.sh proxy"
echo "    Then visit: http://localhost:9097"
echo ""

# Check 8: Verify success
echo "□ Step 8: Verify build succeeded"
echo "  Look for:"
echo "  - Clone step: ✓ Cloned successfully"
echo "  - Maven build: ✓ Build success (or 'BUILD SUCCESS')"
echo "  - Docker build: ✓ Image built"
echo "  - Deploy: ✓ Deployment successful"
echo ""

echo "================================"
echo "Troubleshooting"
echo "================================"
echo ""

echo "If you encounter issues:"
echo ""
echo "1. Check if secret exists:"
echo "   $ kubectl get secret github-maven-credentials -n goods-price-ci"
echo ""
echo "2. Verify PAT has correct scope:"
echo "   Visit: https://github.com/settings/tokens"
echo "   Ensure 'read:packages' is selected"
echo ""
echo "3. View build logs:"
echo "   $ kubectl get pods -n goods-price-ci"
echo "   $ kubectl logs <maven-pod-name> -n goods-price-ci"
echo ""
echo "4. Check pipeline status:"
echo "   $ kubectl get pipelinerun -n goods-price-ci"
echo "   $ kubectl describe pipelinerun <name> -n goods-price-ci"
echo ""
echo "5. For more help:"
echo "   $ ./setup-maven-credentials.sh help"
echo "   $ ./run-pipeline.sh help"
echo "   Read: MAVEN_CREDENTIALS.md"
echo ""

echo "================================"
echo "Quick Commands Reference"
echo "================================"
echo ""
echo "Setup & Verify:"
echo "  ./setup-maven-credentials.sh              # Interactive setup"
echo "  ./setup-maven-credentials.sh verify       # Check credentials"
echo "  ./run-pipeline.sh maven-verify            # Verify via runner"
echo ""
echo "Pipeline Control:"
echo "  ./run-pipeline.sh run                     # Full setup and run"
echo "  ./run-pipeline.sh logs                    # View logs"
echo "  ./run-pipeline.sh status                  # Check status"
echo "  ./run-pipeline.sh diagnose                # Troubleshoot issues"
echo ""
echo "Cleanup (if needed):"
echo "  ./setup-maven-credentials.sh delete       # Delete credentials"
echo "  ./run-pipeline.sh delete                  # Delete old runs"
echo "  ./run-pipeline.sh force-cleanup           # Clean stuck pods"
echo ""

echo "================================"
echo "Support & Documentation"
echo "================================"
echo ""
echo "Quick Reference: ci/local/tekton/QUICK_START.md"
echo "Full Guide: ci/local/tekton/MAVEN_CREDENTIALS.md"
echo "Summary: MAVEN_GITHUB_FIX.md"
echo ""
echo "Script Help:"
echo "  ./setup-maven-credentials.sh help"
echo "  ./run-pipeline.sh help"
echo ""

