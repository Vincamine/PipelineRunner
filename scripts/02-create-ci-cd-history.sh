# Pipeline 1
mkdir -p .ci-cd-history/pipeline1/run-{1,2}/stages/{build,test}/jobs

# Create JSON files for pipeline1
cat > .ci-cd-history/pipeline1/runs.json << 'EOF'
[
  {
    "pipelineId": "pipeline1-run1",
    "level": "SUCCESS",
    "message": "Pipeline completed successfully",
    "timestamp": 1708646400000,
    "status": "SUCCESS",
    "details": [
      "All stages completed successfully",
      "Total execution time: 15m 30s"
    ]
  },
  {
    "pipelineId": "pipeline1-run2",
    "level": "FAILURE",
    "message": "Pipeline failed in test stage",
    "timestamp": 1708732800000,
    "status": "FAILURE",
    "details": [
      "Test failures in unit tests",
      "Build stage completed successfully"
    ]
  }
]
EOF

# Run 1 stages and jobs
cat > .ci-cd-history/pipeline1/run-1/stages/build.json << 'EOF'
[
  {
    "pipelineId": "build-stage",
    "level": "SUCCESS",
    "message": "Build stage completed",
    "timestamp": 1708646100000,
    "status": "SUCCESS",
    "details": [
      "Maven build successful",
      "Artifacts generated: app.jar, app.war"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline1/run-1/stages/build/jobs/compile.json << 'EOF'
[
  {
    "pipelineId": "compile-job",
    "level": "SUCCESS",
    "message": "Compilation successful",
    "timestamp": 1708646000000,
    "status": "SUCCESS",
    "details": [
      "Java compilation completed",
      "0 errors, 2 warnings"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline1/run-1/stages/build/jobs/package.json << 'EOF'
[
  {
    "pipelineId": "package-job",
    "level": "SUCCESS",
    "message": "Package creation successful",
    "timestamp": 1708646050000,
    "status": "SUCCESS",
    "details": [
      "JAR packaging completed",
      "WAR packaging completed",
      "Artifacts stored in repository"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline1/run-1/stages/test.json << 'EOF'
[
  {
    "pipelineId": "test-stage",
    "level": "SUCCESS",
    "message": "Test stage completed",
    "timestamp": 1708646200000,
    "status": "SUCCESS",
    "details": [
      "Unit tests passed: 150/150",
      "Integration tests passed: 45/45",
      "Code coverage: 87%"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline1/run-1/stages/test/jobs/unit-test.json << 'EOF'
[
  {
    "pipelineId": "unit-test-job",
    "level": "SUCCESS",
    "message": "Unit tests passed",
    "timestamp": 1708646150000,
    "status": "SUCCESS",
    "details": [
      "Tests run: 150",
      "Failures: 0",
      "Errors: 0",
      "Skipped: 2"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline1/run-1/stages/test/jobs/integration-test.json << 'EOF'
[
  {
    "pipelineId": "integration-test-job",
    "level": "SUCCESS",
    "message": "Integration tests passed",
    "timestamp": 1708646180000,
    "status": "SUCCESS",
    "details": [
      "Tests run: 45",
      "Failures: 0",
      "Errors: 0",
      "Skipped: 1"
    ]
  }
]
EOF

# Run 2 stages
cat > .ci-cd-history/pipeline1/run-2/stages/build.json << 'EOF'
[
  {
    "pipelineId": "build-stage",
    "level": "SUCCESS",
    "message": "Build stage completed",
    "timestamp": 1708732500000,
    "status": "SUCCESS",
    "details": [
      "Maven build successful",
      "Artifacts generated successfully"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline1/run-2/stages/test.json << 'EOF'
[
  {
    "pipelineId": "test-stage",
    "level": "FAILURE",
    "message": "Test stage failed",
    "timestamp": 1708732600000,
    "status": "FAILURE",
    "details": [
      "Unit tests failed: 148/150",
      "Integration tests skipped",
      "Check test logs for details"
    ]
  }
]
EOF

# Pipeline 2
mkdir -p .ci-cd-history/pipeline2/run-1/stages/deploy/jobs

cat > .ci-cd-history/pipeline2/runs.json << 'EOF'
[
  {
    "pipelineId": "pipeline2-run1",
    "level": "SUCCESS",
    "message": "Deployment pipeline completed",
    "timestamp": 1708819200000,
    "status": "SUCCESS",
    "details": [
      "Successfully deployed to production",
      "All health checks passed"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline2/run-1/stages/deploy.json << 'EOF'
[
  {
    "pipelineId": "deploy-stage",
    "level": "SUCCESS",
    "message": "Deployment completed",
    "timestamp": 1708819100000,
    "status": "SUCCESS",
    "details": [
      "Application deployed to production",
      "Database migration successful",
      "Health checks passed"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline2/run-1/stages/deploy/jobs/db-migration.json << 'EOF'
[
  {
    "pipelineId": "db-migration-job",
    "level": "SUCCESS",
    "message": "Database migration successful",
    "timestamp": 1708819000000,
    "status": "SUCCESS",
    "details": [
      "Applied 3 new migrations",
      "Database schema updated",
      "Backup created"
    ]
  }
]
EOF

cat > .ci-cd-history/pipeline2/run-1/stages/deploy/jobs/k8s-deploy.json << 'EOF'
[
  {
    "pipelineId": "k8s-deploy-job",
    "level": "SUCCESS",
    "message": "Kubernetes deployment successful",
    "timestamp": 1708819050000,
    "status": "SUCCESS",
    "details": [
      "Deployed to production namespace",
      "Pod health checks passed",
      "Service endpoints verified"
    ]
  }
]
EOF

echo "CI/CD history files created successfully"