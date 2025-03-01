# **Common Module Overview**

- Version: 1.0
- Updated with the Project requirements by Feb 28, 2025
- Author: Yiwen Wang

## **1. Introduction**
The `common` module serves as the **core shared component** of the CI/CD pipeline system. It contains **data models and API contracts** used by other modules, including:
- `backend` (orchestration and execution control)
- `worker` (actual execution of jobs)
- `cli` (user interaction)

The `common` module ensures **consistency** across different components and enables **modular development**.

---

## **2. Overview of Key Components**
### 🔹 **Model (`common.model`)**
This package contains **data structures** that represent core CI/CD concepts, including:

| **Class** | **Description** |
|-----------|---------------|
| `PipelineDefinition` | Defines a pipeline configuration (stages, global variables). |
| `StageDefinition` | Defines a single stage within a pipeline, containing jobs. |
| `JobDefinition` | Represents a **static job configuration** (image, script, dependencies). |
| `PipelineExecution` | Tracks an **active** pipeline execution. |
| `StageExecution` | Tracks an **active** stage execution. |
| `JobExecution` | Tracks an **active** job execution (status, timestamps, etc.). |
| `PipelineState` | Enum representing possible states of a pipeline execution. |
| `JobStatusUpdate` | Used to update the status of a running job. |

### 🔹 **API (`common.api`)**
This package contains **API request/response objects** that facilitate communication between different components.

| **Class** | **Purpose** |
|-----------|------------|
| `RunPipelineRequest` | Sent to the backend to start a pipeline execution. |
| `PipelineCheckResponse` | Response from a pipeline validation check. |
| `JobRequest` | Represents a request to **run a job** (Docker image, commands, etc.). |
| `JobResponse` | Contains execution results of a job (exit code, logs, artifacts). |
| `JobStatusUpdate` | Used to **update job status** (running, failed, completed). |
| `WorkerRegistrationRequest` | Sent when a new worker registers to the system. |
| `WorkerRegistrationResponse` | Response confirming worker registration success/failure. |

---

## **3. Execution Flow & Responsibilities**
### **🔹 How the Common Module Works in CI/CD Execution**

1️⃣ **Pipeline Execution**
- The backend **loads** `PipelineDefinition` from the repository YAML.
- `PipelineExecution` is created to **track execution progress**.
- `StageExecution` and `JobExecution` objects are initialized for each stage/job.

2️⃣ **Job Execution**
- A `JobRequest` is **sent to a worker**.
- The worker **executes the job** using the provided Docker image and commands.
- The worker sends a `JobStatusUpdate` as the job progresses.
- Once completed, the worker sends a `JobResponse` back to the backend.

3️⃣ **Status Updates & Completion**
- `PipelineExecution` and `StageExecution` **update their statuses** based on job results.
- If all jobs complete successfully, the pipeline transitions to `SUCCESS`.
- If a job fails (and `allowFailure=false`), the pipeline transitions to `FAILED`.

---

## **4. Key Design Principles**
✅ **Separation of Concerns** → API models separate from execution models.  
✅ **Standardized Communication** → API files enforce consistency across modules.  
✅ **Scalability** → The design allows adding more job types, execution strategies, and reporting mechanisms.  
✅ **Flexibility** → Job execution is **decoupled** from pipeline orchestration, allowing for future **distributed execution**.

---

## **5. Summary**
- The **common module** is the **foundation** of the CI/CD system.
- It defines **data structures** for pipelines, stages, and jobs.
- It provides **API contracts** for communication between the backend and workers.
- It ensures **consistency and reusability** across the entire CI/CD system.

🚀 **With this structure, the system supports efficient, modular, and scalable CI/CD execution.**

