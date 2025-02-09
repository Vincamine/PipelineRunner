# 25Spring CS6510 Team 1 - CI/CD System

- **Title:** Readme file
- **Date:** Feb 9, 2025
- **Author:** Yiwen Wang
- **Version:** 1.2

**Revision History**

|     Date     | Version | Description                            | Author     |
| :----------: | :-----: | :------------------------------------- | :--------- |
| Jan 31, 2025 |   1.0   | Initial release                        | Yiwen Wang |
| Feb 3, 2025  |   1.1   | Added CLI usage details                | Yiwen Wang |
| Feb 9, 2025  |   1.2   | Added Git repository requirement (#38) | Yiwen Wang |

---

# Introduction: Local CI/CD Pipeline Runner

Welcome to the repository for our CI/CD system for small/medium-sized companies.

A command-line tool designed to run and manage CI/CD pipelines both on company data centers and locally on developer machines. This tool allows developers to execute, debug, and validate entire or partial pipelines without the need for external modifications, ensuring that all CI/CD configurations reside within the repository and can be tracked via version control.

---

## Table of Contents

- [Overview](#overview)
- [Git Repository Requirement](#git-repository-requirement)
- [Key Features](#key-features)
- [Installation](#installation)
- [Usage](#usage)
  - [Command Line Interface (CLI)](#command-line-interface-cli)
  - [Sub-Commands and Options](#sub-commands-and-options)
- [Pipeline Configuration File](#pipeline-configuration-file)
- [Error Reporting](#error-reporting)
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

## 🚨 Git Repository Requirement

This CLI **must** be executed from the root of a **Git repository**. If not, the tool will fail with the following error:

```
❌ Error: This CLI must be run from the root of a Git repository.
```

### **How to Check if You Are in a Git Repository?**

Run the following command:

```bash
git rev-parse --is-inside-work-tree
```

If the output is `true`, you are inside a Git repository.

### **How to Initialize a Git Repository?**

If the repository is not initialized, use:

```bash
git init
```

Then commit the necessary files:

```bash
git add .
git commit -m "Initial commit"
```

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

The CLI **must** be executed from the root of a Git repository. It provides sub-commands and options to validate, simulate, run, and report on pipeline executions.

#### General Usage

```bash
# Note: This CLI must be executed from the root of a Git repository.
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

---

## Error Reporting

Errors are reported in the following format:

```
<pipeline-config-filename>:<line-number>:<column-number>: <error-message>
```

### **Common Errors:**

#### **1. Not inside a Git repository**

```
❌ Error: This CLI must be run from the root of a Git repository.
```

#### **2. Invalid Pipeline Configuration**

```
pipeline.yaml:10:22: syntax error, wrong type for value `3` in key `name`, expected a String value.
```

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