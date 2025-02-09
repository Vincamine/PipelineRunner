# 25Spring CS6510 Team 1 - CI/CD System

- **Title:** Readme file
- **Date:** Feb 9, 2025
- **Author:** Yiwen Wang
- **Version:** 1.2

**Revision History**

|     Date     | Version | Description                                      | Author     |
| :----------: | :-----: | :----------------------------------------------- | :--------- |
| Jan 31, 2025 |   1.0   | Initial release                                  | Yiwen Wang |
| Feb 3, 2025  |   1.1   | Added CLI usage details                          | Yiwen Wang |
| Feb 9, 2025  |   1.2   | Enhanced Git validation & added status command  | Yiwen Wang |

---

# Introduction: Local CI/CD Pipeline Runner

Welcome to the repository for our CI/CD system for small/medium-sized companies.

A command-line tool designed to run and manage CI/CD pipelines both on company data centers and locally on developer machines. This tool allows developers to execute, debug, and validate entire or partial pipelines without the need for external modifications, ensuring that all CI/CD configurations reside within the repository and can be tracked via version control.

---

# Design Documents

## Tech Stack

Describe the technologies used in the project, including programming languages, frameworks, libraries, and infrastructure components.  
[Tech Stack Design](dev-docs/reports/weeklies/design/tech-stack.md)

## High-Level Design

Provide an architectural overview of the system. Include diagrams if possible, explaining the interaction between different components and the overall flow of the application.  
[High-Level Architecture Design](dev-docs/reports/weeklies/design/high-level-arch.md)

## Low-Level Design

Detail the implementation of various components, including algorithms, data structures, database schemas, API endpoints, and business logic.  
[Low-Level Design](dev-docs/reports/weeklies/design/low-level-design.md)

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Installation](#installation)
- [Usage](#usage)
  - [Command Line Interface (CLI)](#command-line-interface-cli)
  - [Sub-Commands and Options](#sub-commands-and-options)
- [Pipeline Configuration File](#pipeline-configuration-file)
- [Execution Flow](#execution-flow)
- [Error Reporting](#error-reporting)
- [Reporting on Past Executions](#reporting-on-past-executions)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

This project is a CI/CD pipeline runner that:

- **Supports Dual Environments:** Can run on both company data centers and local environments.
- **Local Pipeline Execution:** Enables developers to run pipelines in full or in part locally for debugging and development.
- **Repository-Driven Configuration:** All pipeline configurations are stored and tracked within the repository under a designated folder.
- **Unified CLI:** Provides a command-line client that adheres to best practices, ensuring consistent behavior whether run locally or on server machines.
- **Enhanced Git Validation:** Ensures the CLI only runs from a valid Git repository.

---

## Key Features

- **Local and Remote Execution:** Seamlessly run pipelines in both company data centers and local environments.
- **In-Repo Configuration:** All pipeline configurations reside in a `.pipelines` folder within the repository, ensuring they are versioned and trackable.
- **Strict Git Repository Integration:** Only committed files are considered; uncommitted local changes are ignored.
- **CLI-Driven Workflow:** A rich command-line interface (CLI) for running, checking, and reporting pipeline executions.
- **Flexible Pipeline Definitions:** Pipelines are defined in YAML v1.2 with clear specifications for stages, jobs, dependencies, and scripts.
- **Enhanced Logging & Debugging:** Added verbose mode and structured logging for debugging CLI operations.

---

## Installation

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

## Usage

### Command Line Interface (CLI)

The CLI must be executed from the root of a Git repository. It provides sub-commands and options to validate, simulate, run, and report on pipeline executions.

#### General Usage

```bash
./gradlew run --args="[sub-command] [options]"
```

### Sub-Commands and Options

#### 1. **Display Help Information**

Shows all available commands and options:

```bash
./gradlew run --args="--help"
```

#### 2. **Display Version**

Displays the current version of the CLI tool:

```bash
./gradlew run --args="--version"
```

#### 3. **Logging Messages**

Use the `log` sub-command to log messages:

```bash
./gradlew run --args="log --message 'Deployment started'"
```

If no message is provided, the CLI will display:

```bash
./gradlew run --args="log"
```

Output:

```
No message provided.
```

#### 4. **Checking Pipeline Status**

```bash
./gradlew run --args="status --pipeline-id 12345"
```

If inside a Git repository, the pipeline status is printed. Otherwise, the CLI will return:

```
❌ Error: This CLI must be run from the root of a Git repository.
```

---

## Pipeline Configuration File

All CI/CD configuration files must be located in a `.pipelines` folder in the repository root and follow the YAML v1.2 format.

### Basic Structure

```yaml
pipeline:
  name: "My Pipeline"
  stages:
    - build
    - test
    - docs

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

  - name: javadoc
    stage: docs
    image: gradle:8.12-jdk21
    script:
      - ./gradlew javadoc
```

---

## How to Test

### 1️⃣ Run CLI with Verbose Mode

```bash
./gradlew run --args="--verbose"
```
✅ Expected: Should print `✅ Verbose mode enabled.` before any validation.

### 2️⃣ Test Git Validation with Debug Logging

```bash
./gradlew run --args="status --pipeline-id 12345"
```
✅ Expected:
- If inside a Git repository, it should print pipeline status.
- If not inside a Git repository, it should return:
  
  ```
  ❌ Error: This CLI must be run from the root of a Git repository.
  ```

### 3️⃣ Run Unit Tests

```bash
./gradlew test
```
✅ Expected: All tests should pass.

---

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/my-new-feature`).
3. Commit your changes (`git commit -am 'Add some feature'`).
4. Push to the branch (`git push origin feature/my-new-feature`).
5. Open a pull request detailing your changes.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.


---

