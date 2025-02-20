#!/bin/bash

# Set script to exit on error
set -e

echo "Starting CI/CD CLI Tool Demo"

# Step 1: Build the CLI Tool
echo "Building the CI/CD tool..."
./gradlew clean build
./gradlew clean shadowJar

# Step 2: Verify CLI Installation
echo "Checking CLI Installation..."
java -jar app/build/libs/ci-tool.jar --help

# Step 3: Validate a Pipeline Configuration
echo "Checking pipeline configuration..."
java -jar app/build/libs/ci-tool.jar check -f .pipelines/00-valid-file.yaml

# Step 4: Perform a Dry Run to Generate Execution Order
echo "Performing a Dry Run..."
java -jar app/build/libs/ci-tool.jar dry-run -f .pipelines/00-valid-file.yaml

# Step 5: Run Pipeline on Local Machine
echo "Running Pipeline Locally..."
java -jar app/build/libs/ci-tool.jar run --local --repo . --file .pipelines/00-valid-file.yaml

# Step 6: Error Handling Scenarios
echo "=============================================="
echo "           TESTING ERROR HANDLING             "
echo "=============================================="

# Missing file argument
echo -e "\n[ERROR] Running check command without specifying a file..."
java -jar app/build/libs/ci-tool.jar check || echo -e "[EXPECTED ERROR] ✔ Command failed as expected\n"

# Non-existent pipeline file
echo -e "\n[ERROR] Checking a non-existent file..."
java -jar app/build/libs/ci-tool.jar check -f .pipelines/non-exist-file.yaml || echo -e "[EXPECTED ERROR] ✔ Command failed as expected\n"

# Invalid pipeline file with missing fields
echo -e "\n[ERROR] Checking an invalid pipeline file (missing name)..."
java -jar app/build/libs/ci-tool.jar check -f .pipelines/01-miss-name.yaml || echo -e "[EXPECTED ERROR] ✔ Command failed as expected\n"

# Invalid pipeline file with wrong field type
echo -e "\n[ERROR] Checking an invalid pipeline file (wrong name type)..."
java -jar app/build/libs/ci-tool.jar check -f .pipelines/02-wrong-name-type.yaml || echo -e "[EXPECTED ERROR] ✔ Command failed as expected\n"

echo "=============================================="
echo "   CI/CD CLI Tool Demo Completed Successfully!"
echo "=============================================="
