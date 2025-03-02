# **CI/CD Pipeline Design Logic**
- Version: 1.1
- Updated with the Project requirements by March 2, 2025
- Author: Yiwen Wang
- **Version 1.2**
- Updated with Stage, Job, Execution, and Validation Logic as of March 2, 2025

## **1. Overview**
This document outlines the design logic for the CI/CD pipeline system. The system is designed to allow developers to execute CI/CD workflows both **locally** and **on remote servers**, integrating with Git repositories for configuration management.

It includes updates on **validation logic, execution tracking, error reporting, and dependency handling** to ensure robust and scalable CI/CD processes.

## **2. Pipeline Identification**
- Each **pipeline** is uniquely identified by its **name** within a repository.
- Multiple pipelines are allowed per repository.
- Each pipeline must have a unique name inside the .pipelines/ directory.
- If a duplicate name is found, validation fails.
- **No separate Pipeline ID is required** since the name ensures uniqueness.

## **3. Pipeline Configuration (YAML Format)**
Pipelines are defined using a YAML file located in `.pipelines/` at the root of the repository.

### **Example Configuration:**
```yaml
pipeline:
  name: "build-and-test"
  stages:
    - build
    - test
    - deploy
```

## **4. Execution Flow**
### **Step 1: Running a Pipeline**
A developer can trigger a pipeline execution using the CLI:
```bash
xx run --repo https://github.com/company/project --pipeline build-and-test
```
**Execution Steps:**
1. **CLI sends a request** to the backend with the pipeline name.
2. **Backend fetches the pipeline configuration** from the `.pipelines/pipeline.yaml` file.
3. **PipelineExecutionService starts execution** of the pipeline using the `PipelineExecution` class.
4. If a duplicate pipeline name exists, execution fails immediately.
5**Stages execute in sequence**, jobs within a stage execute in parallel (unless dependencies exist).

### **Step 2: Fetching Execution Status**
Developers can check the current status of a pipeline:
```bash
xx status --pipeline build-and-test
```
This calls:
```
GET /api/v1/pipelines/build-and-test/status
```
Response:
```json
{
  "name": "build-and-test",
  "status": "RUNNING"
}
```

### **Step 3: Handling Failures**
- If **a job fails** (`allowFailure=false`), the pipeline **stops immediately**.
- If **a job fails** (`allowFailure=true`), the pipeline **continues execution**.
- If a **dependency cycle** is detected, execution **fails immediately** with an error message.

### **Step 4: Fetching Execution History**
Developers can retrieve past execution records:
```bash
xx report --pipeline build-and-test
```
Backend returns a list of previous executions for that pipeline name.

## **5. Stage and Job Logic**
### **📌 Stage Logic**
- **Stages execute sequentially**, following the order defined in the YAML.
- A **stage must contain at least one job**.
- **A stage starts only after the previous stage completes successfully**.

### **📌 Job Logic**
- **Jobs are the smallest execution units** inside a stage.
- Jobs **execute inside Docker containers**, requiring an `image`.
- Jobs **run in parallel unless they define dependencies (`needs`)**.
- Jobs must define at least **one script command** to execute.
- **Job failure handling:**
  - If `allowFailure=false`, the pipeline **stops on failure**.
  - If `allowFailure=true`, the pipeline **continues execution**.

### **📌 Example Execution Order**
#### **YAML Configuration:**
```yaml
pipeline:
  name: "build-and-test"
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
    needs:
      - compile  # Must wait for compile job

  - name: deploy
    stage: deploy
    image: ubuntu:latest
    script:
      - echo "Deploying..."
    needs:
      - unittests  # Must wait for unittests to complete
```

### **📌 Execution Order**
| Stage  | Jobs (Parallel) | Dependencies |
|--------|---------------|-------------|
| **build** | compile | - |
| **test** | unittests | compile must complete |
| **deploy** | deploy | unittests must complete |

## **6. Execution Components**
### **Pipeline Execution Tracking**
- **PipelineExecution** tracks the current execution state.
- **PipelineState Enum** manages execution states:
  - `PENDING`: Waiting to start.
  - `RUNNING`: Currently executing.
  - `SUCCESS`: Completed successfully.
  - `FAILED`: Stopped due to job failure.
  - `CANCELED`: Manually stopped.

### **Pipeline Execution Service**
- `PipelineExecutionService` starts and tracks executions.
- Stores executions in a **ConcurrentHashMap** with the **pipeline name** as the key.

### **Pipeline REST API**
#### **Start a Pipeline Execution**
```
POST /api/v1/pipelines/run
```
Request Body:
```json
{
  "repo": "https://github.com/company/project",
  "branch": "main",
  "commit": "abcd1234",
  "pipeline": "build-and-test",
  "local": true
}
```
#### **Get Pipeline Status**
```
GET /api/v1/pipelines/{pipelineName}/status
```
Response:
```json
{
  "name": "build-and-test",
  "status": "SUCCESS"
}
```

## **7. Summary of Design Changes**
✅ **Pipeline ID Removed**: Only the pipeline **name** is used for identification.  
✅ **Execution Tracking by Name**: All execution-related tracking uses `pipelineName`.  
✅ **REST API Updates**: Routes now reference `/{pipelineName}/status` instead of `/{id}/status`.  
✅ **CLI Adjustments**: Commands reference pipelines by name rather than ID.  
✅ **Stage and Job Logic Added**: Stages run sequentially, jobs run in parallel (unless dependencies exist).  
✅ **Validation Errors Now Include Line Numbers**.  
✅ **Cycle Detection Added to Prevent Infinite Job Loops**.

## **8. Future Enhancements**
- Implement a **database-backed execution history** instead of in-memory storage.
- Introduce **user-defined pipeline execution metadata** (e.g., triggered by PRs, scheduled runs).
- Add **real-time logging and monitoring** for pipeline execution insights.

This design ensures a **clear, simple, and scalable** CI/CD system that aligns with project requirements.

