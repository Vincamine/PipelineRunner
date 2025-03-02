# **CLI Command Logic**
- **Version:** 1.0
- **Updated:** March 2, 2025
- **Author:** Yiwen Wang

## **1. Overview**
This document describes the logic and implementation details of the CLI commands for the CI/CD system. The CLI allows users to:
- **Validate pipeline configurations**
- **Simulate execution (`dry-run`)**
- **Execute pipelines (`run`)** locally or via backend
- **Retrieve execution reports**

The CLI ensures:
- **Validation of inputs before execution**
- **Execution inside a valid Git repository**
- **YAML parsing and validation before execution**
- **Structured error reporting**

---

## **2. CLI Execution Requirements**
- **The CLI must be executed from the root of a Git repository.**
- **A valid `.pipelines/pipeline.yaml` configuration file is required.**
- **All commands validate inputs before execution.**
- **Errors are reported in the format:**
  ```
  <filename>:<line-number>:<column-number>:<error-message>
  ```

### ✅ **Git Repository Validation**
- Implemented using `GitValidator`
- Ensures `.git` directory exists in the root or parent directories
- Prevents execution outside of a valid Git repository

### ✅ **YAML Pipeline Configuration Validation**
- Enforced using `YamlParser` and `PipelineValidator`
- Ensures required fields:
    - `pipeline.name` (must be unique within repo)
    - `stages` (at least one required)
    - `jobs` (each stage must have at least one job)
    - `needs` dependencies must exist and be **acyclic**
- Supports:
    - **YAML v1.2 format**
    - **Error reporting with line numbers**
    - **Type validation for configuration values**

---

## **3. Command List and Execution Flow**
### **3.1 Common Execution Flow**
**All CLI commands follow this structure:**
1. **Validate inputs (`validateInputs()`)**
    - Ensure `--file` parameter is provided
    - Ensure CLI is running inside a Git repository
2. **Load and parse YAML file (`YamlParser.parseYaml()`)**
3. **Validate pipeline structure (`PipelineValidator.validate()`)**
4. **Execute command-specific logic**
5. **Return appropriate exit codes**
    - `0` → Success
    - `1` → General error
    - `2` → Missing file / wrong directory
    - `3` → Validation failure

---

## **4. CLI Commands**
### **4.1 `check` Command (Validate Pipeline)**
#### **Description**
- Parses and validates the pipeline YAML file **locally**.
- Does NOT interact with the backend.
- Detects syntax errors, missing fields, duplicate job names, and cyclic dependencies.

#### **Usage**
```bash
xx check --file .pipelines/pipeline.yaml
```

---

### **4.2 `dry-run` Command (Simulate Execution)**
#### **Description**
- Parses, validates, and simulates the pipeline **without execution**.
- Verifies execution order and dependencies.
- Returns **expected execution plan**.

#### **Usage**
```bash
xx dry-run --file .pipelines/pipeline.yaml
```

#### **Example Output**
```
Pipeline Execution Plan:
Stage: build
  - compile
Stage: test
  - unittests (needs: compile)
Stage: deploy
  - deploy (needs: unittests)
```

---

### **4.3 `run` Command (Execute Pipeline)**
#### **Description**
- Parses and validates the pipeline.
- **Executes locally (`--local`)** OR **sends execution request to the backend**.
- Ensures jobs obey `needs` dependencies.
- Runs jobs in **Docker containers** if an `image` is specified.

#### **Usage**
```bash
# Execute pipeline using backend
xx run --file .pipelines/pipeline.yaml

# Execute pipeline locally
xx run --file .pipelines/pipeline.yaml --local
```

#### **Example Output**
```
Starting stage: build
✅ Job compile completed successfully

Starting stage: test
✅ Job unittests completed successfully

Starting stage: deploy
✅ Job deploy completed successfully
✅ Local pipeline execution completed successfully.
```

---

### **4.4 `report` Command (Retrieve Execution History)**
#### **Description**
- Fetches past pipeline executions from the backend.
- Supports filtering by:
    - **Pipeline name (`--pipeline`)**
    - **Specific run (`--run`)**
- Supports output in **plaintext, JSON, or YAML**.

#### **Usage**
```bash
# List all pipelines
xx report --list-pipelines

# Get execution history for a specific pipeline
xx report --pipeline build-and-test

# Get details of a specific execution run
xx report --pipeline build-and-test --run 1234
```

---

## **5. Error Handling**
### **5.1 Standardized Error Format**
All errors follow this format:
```
<filename>:<line-number>:<column-number>:<error-message>
```

### **5.2 CLI Exit Codes**
| Code | Meaning |
|------|---------|
| `0`  | Success |
| `1`  | General error |
| `2`  | Missing file / not in Git repo |
| `3`  | Validation error |

---

## **6. Summary**
✅ **Implemented CLI commands: `check`, `dry-run`, `run`, `report`**  
✅ **Validation of YAML pipelines**  
✅ **Ensured CLI is executed from a Git repository**  
✅ **Supports local execution (`--local`) and backend execution**  
✅ **Structured error handling**  
✅ **Execution tracking and reporting**

---

## **Next Steps**
- **Implement parallel job execution within a stage**
- **Add real-time logging for `run --local`**
- **Improve execution reports with job logs and timestamps**

