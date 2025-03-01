# **CI/CD Pipeline Design Logic**

- **Version 1.0**
- According to the Whole project requirements by Feb 28, 2025

## **1. Overview**
This document outlines the design logic for the CI/CD pipeline system. The system is designed to allow developers to execute CI/CD workflows both **locally** and **on remote servers**, integrating with Git repositories for configuration management.

## **2. Pipeline Identification**
- Each **pipeline** is uniquely identified by its **name** within a repository. 
- The **pipeline name** is stored in `.pipelines/pipeline.yaml`.
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
4. **Stages execute in sequence**, jobs within a stage execute in parallel (unless dependencies exist).

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
- If **a job fails** (`allowFailure=false`), pipeline **stops immediately**.
- If **a job fails** (`allowFailure=true`), the pipeline **continues execution**.

### **Step 4: Fetching Execution History**
Developers can retrieve past execution records:
```bash
xx report --pipeline build-and-test
```
Backend returns a list of previous executions for that pipeline name.

## **5. Execution Components**
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

## **6. Summary of Design Changes**
✅ **Pipeline ID Removed**: Only the pipeline **name** is used for identification.
✅ **Execution Tracking by Name**: All execution-related tracking uses `pipelineName`.
✅ **REST API Updates**: Routes now reference `/{pipelineName}/status` instead of `/{id}/status`.
✅ **CLI Adjustments**: Commands reference pipelines by name rather than ID.

## **7. Future Enhancements**
- Implement a **database-backed execution history** instead of in-memory storage.
- Introduce **user-defined pipeline execution metadata** (e.g., triggered by PRs, scheduled runs).

This design ensures a **clear, simple, and scalable** CI/CD system that aligns with project requirements.

