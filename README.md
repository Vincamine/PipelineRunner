# 25Spring CS6510 Team 1 - CI/CD System
- **Title:** Readme file
- **Date:** Jan 31, 2025
- **Author:** Yiwen Wang
- **Version:** 1.0

**Revision History**
|Date|Version|Description|Author|
|:----:|:----:|:----:|:----:|
|Jan 31, 2025|1.0|Initial release| Yiwen Wang|


# Introduction: Local CI/CD Pipeline Runner
* Welcome to the repository for our CI/CD system for a small/medium size company. 

A command-line tool designed to run and manage CI/CD pipelines both on company data centers and locally on developer machines. This tool allows developers to execute, debug, and validate entire or partial pipelines without the need for external modifications, ensuring that all CI/CD configurations reside within the repository and can be tracked via version control.

---

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

---

## Key Features

- **Local and Remote Execution:** Seamlessly run pipelines in both company data centers and local environments.
- **In-Repo Configuration:** All pipeline configurations reside in a `.pipelines` folder within the repository, ensuring they are versioned and trackable.
- **Strict Git Repository Integration:** Only committed files are considered; uncommitted local changes are ignored.
- **CLI-Driven Workflow:** A rich command-line interface (CLI) for running, checking, and reporting pipeline executions.
- **Flexible Pipeline Definitions:** Pipelines are defined in YAML v1.2 with clear specifications for stages, jobs, dependencies, and scripts.
- **Detailed Error Reporting:** Errors include file name, line, column, and a descriptive message to facilitate quick debugging.
- **Robust Execution Management:** Handles job dependencies, parallel execution for independent jobs, sequential stages, and duplicate request detection.

---

## Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/CS6510-SEA-SP25/t1-cicd.git
   cd t1-cicd
   ```

2. **Install Dependencies:**

   Ensure you have the necessary runtime environment and dependencies installed. Refer to the `requirements.txt` or project documentation for specifics. For example:

   ```bash
   pip install -r requirements.txt
   ```

3. **Build/Compile (if needed):**

   If the project requires compilation or build steps, run:

   ```bash
   make build
   ```

---

## Usage

### Command Line Interface (CLI)

The CLI must be executed from the root of a Git repository. It provides sub-commands and options to validate, simulate, run, and report on pipeline executions.

#### General Usage

```bash
pipeline-runner [sub-command] [options]
```

### Sub-Commands and Options

#### 1. **Checking a Pipeline Configuration**

Use the `--check` flag to validate your pipeline configuration file without running the pipeline:

```bash
pipeline-runner --check -f <relative-path-to-config>
```

#### 2. **Dry Run**

Perform a dry run to check the configuration validity and print the execution order (stages and jobs) in valid YAML format:

```bash
pipeline-runner --dry-run -f <relative-path-to-config>
```

#### 3. **Running a Pipeline**

Execute a pipeline based on the provided configuration file:

```bash
pipeline-runner run -f <relative-path-to-config> [--repo <path>] [--branch <branch-name>] [--commit <commit-hash>]
```

#### 4. **Reporting**

Retrieve reports on past pipeline executions with the `reports` sub-command:

```bash
pipeline-runner reports [pipeline-name] [execution-id] [stage-name] [job-name]
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

## Error Reporting

Errors are reported in the following format:

```
<pipeline-config-filename>:<line-number>:<column-number>: <error-message>
```

Example:

```
pipeline.yaml:10:22: syntax error, wrong type for value `3` in key `name`, expected a String value.
```

---

## Reporting on Past Executions

Reports can be generated at various levels:

- **Pipeline Level:** List all available pipelines.
- **Execution Level:** Summary of all executions for a specified pipeline.
- **Stage Level:** Detailed report for a particular stage within an execution.
- **Job Level:** Detailed report for a specific job.

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

# Design Documents

## Tech Stack

Describe the technologies used in the project, including programming languages, frameworks, libraries, and infrastructure components.
[Tech Steck Design](dev-docs/reports/weeklies/design/tech-stack.md)

## High-Level Design

Provide an architectural overview of the system. Include diagrams if possible, explaining the interaction between different components and the overall flow of the application.
[High Level Archetecture Design](dev-docs/reports/weeklies/design/high-level-arch.md)

## Low-Level Design

Detail the implementation of various components, including algorithms, data structures, database schemas, API endpoints, and business logic.



