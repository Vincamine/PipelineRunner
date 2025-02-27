# 25Spring CS6510 Team 1 - CI/CD System

- **Title:** Readme file
- **Date:** Feb 27, 2025
- **Author:** Yiwen Wang
- **Version:** 1.4

**Revision History**

|     Date     | Version | Description                                     | Author     |
| :----------: | :-----: | :---------------------------------------------- | :--------- |
| Jan 31, 2025 |   1.0   | Initial release                                 | Yiwen Wang |
| Feb 3, 2025  |   1.1   | Added CLI usage details                         | Yiwen Wang |
| Feb 9, 2025  |   1.2   | Enhanced Git validation & added status command  | Yiwen Wang |
| Feb 9, 2025  |   1.3   | Added troubleshooting & structured error format | Yiwen Wang |
| Feb 27, 2025 |   1.4   | Updated based on refined project requirements   | Yiwen Wang |

---

# Introduction: Local CI/CD Pipeline Runner

Welcome to the repository for our CI/CD system, designed for small to medium-sized companies. This system enables developers to execute, debug, and validate CI/CD pipelines seamlessly across both company data centers and local machines without modifications.

A command-line tool ensures that all CI/CD configurations reside within the repository, making it easier to manage, track, and validate development pipelines with a version-controlled structure.

---

# Features & Capabilities

- **Local-first Approach:** CI/CD pipelines can run on both company servers and local developer machines with no modifications.
- **Git-based Configuration:** All pipeline settings reside in a `.pipelines` directory inside the repository.
- **Independent Pipelines:** Each pipeline configuration is self-contained, with a unique name and a complete execution definition.
- **Flexible Pipeline Structure:**
  - Pipelines consist of sequential **stages**.
  - Stages contain **jobs** that may run in parallel.
  - Jobs define execution environments using Docker images.
  - Jobs support dependencies but cannot form cycles.
- **Robust CLI:**
  - Validate configuration files.
  - Execute dry-run previews of pipeline execution order.
  - Run pipelines locally or on remote servers.
  - Generate detailed reports on past executions.
  - Override configuration parameters at runtime.
- **Job Flexibility:**
  - Jobs can continue execution even after failures (if configured).
  - Jobs can specify artifacts to be uploaded upon completion.
  - Artifacts can be selected using pattern-based paths.
- **Comprehensive Reporting:**
  - Status tracking for pipelines, stages, and jobs.
  - Logs and execution summaries for debugging and analysis.

---

# Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/CS6510-SEA-SP25/t1-cicd.git
   cd t1-cicd
   ```
2. **Build the Project:**
   ```bash
   ./gradlew clean build
   ```

---

# Usage

### Command Line Interface (CLI)

Run the CLI from the root of a Git repository. It provides commands for validating, running, and reporting on pipeline executions.

#### General Usage
```bash
./gradlew run --args="[sub-command] [options]"
```

#### Key CLI Commands

- **Display Help:**
  ```bash
  ./gradlew run --args="--help"
  ```
- **Display Version:**
  ```bash
  ./gradlew run --args="--version"
  ```
- **Check Pipeline Status:**
  ```bash
  ./gradlew run --args="status --pipeline-id 12345"
  ```
  - Returns error if executed outside a Git repository.
- **Run Pipelines Locally:**
  ```bash
  ./gradlew run --args="run --pipeline my_pipeline"
  ```
- **Validate Pipeline Configuration:**
  ```bash
  ./gradlew run --args="validate .pipelines/config.yaml"
  ```

---

# Pipeline Configuration

All CI/CD configurations should be stored in the `.pipelines` directory in the repository root and must follow the YAML v1.2 format.

### Example Configuration:
```yaml
pipeline:
  name: "My Pipeline"
  stages:
    - build
    - test
    - deploy

jobs:
  - name: compile
    stage: build
    image: gradle:8.12-jdk21
    script:
      - ./gradlew classes
  - name: unittests
    stage: test
    image: gradle:8.12-jdk21
    script:
      - ./gradlew test
  - name: deploy
    stage: deploy
    image: gradle:8.12-jdk21
    script:
      - ./gradlew deploy
```

---

# Troubleshooting

### 🛠 Issue: CLI Must Be Run in a Git Repository
#### Cause
GitValidator enforces that the CLI only runs inside a Git repository.
#### Solution
```bash
git rev-parse --is-inside-work-tree
```
If the output is `true`, you're in a Git repository. Otherwise, initialize one:
```bash
git init
```

### 🛠 Issue: Invalid YAML Format in `.pipelines` Configuration
#### Solution
Run:
```bash
yamllint .pipelines/config.yaml
```
Fix any reported issues before re-running the pipeline.

---

# Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/my-new-feature`).
3. Commit your changes (`git commit -am 'Add feature X'`).
4. Push to your branch (`git push origin feature/my-new-feature`).
5. Open a pull request for review.

---

# License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
