# 25Spring CS6510 Team 1 - CI/CD System
- **Title:** High Level Architecture design file
- **Date:** Jan 31, 2025
- **Author:** Yiwen Wang, Mingtianfang Li
- **Version:** 1.0

**Revision History**
|Date|Version|Description|Author|
|:----:|:----:|:----:|:----:|
|Jan 31, 2025|1.0|Initial release| Yiwen Wang, Mingtianfang Li|

# Design Proposal

## System Components

### Developer Machines  
- Workstations where developers trigger the pipeline execution via the CLI tool.

### Git Repository (Local & Remote)  
- Stores and manages pipeline configurations in `.pipelines/`.  
- The CLI interacts with these repositories to fetch the necessary configuration files.

### CI/CD Pipeline Runner (CLI)  
- The primary interface for developers to execute and manage CI/CD pipelines.  
- Sends pipeline execution requests to the backend.

### Configuration Validation Service  
- Reads, validates, and ensures correctness of `.yaml` pipeline configurations.  
- Runs as a service to provide real-time validation feedback.

### Execution Engine  
- Orchestrates and runs jobs based on defined dependencies.  
- Supports both parallel and sequential execution.

### Error Monitoring Service  
- Captures syntax errors, missing dependencies, and execution failures.  
- Provides error logs and notifications to the CLI.

### Logging & Reporting Service  
- Stores execution logs and historical pipeline runs.  
- Provides reports and analytics on past executions.


## Workflow

1. Developers trigger the pipeline using the CLI.
2. The system reads pipeline configurations stored in the `.pipelines/` directory.
3. The YAML parser validates the configuration.
4. The execution engine runs jobs in parallel or sequentially based on dependencies.
5. Error handling and logging modules provide feedback.
6. Reports are stored and accessible via the CLI.

## Diagram

### Explanation:
- The **developer** triggers the pipeline using the **CLI**.
- The **CLI** fetches pipeline configurations from the **Git repository**.
- The **Backend API** stores requests in a **Database** and queues execution via a **Message Queue**.
- A **Worker** picks up the job, updates its status, and reports progress.
- The **Backend API** updates the **UI** and **CLI** with logs and final job completion status.


### High Level Diagram
```mermaid
graph TD
    A[Developer Machine] -->|Triggers pipeline| B[CLI]
    B -->|Fetch pipeline configuration| C[Git Repository]
    B -->|Send pipeline request| D[Backend API]
    D -->|Store request| E[Database]
    D -->|Queue job execution| F[Message Queue]
    F -->|Assign job execution| G[Worker]
    G -->|Update status to 'running'| E
    G -->|Send progress updates| D
    D -->|Update UI with job status| H[Client UI]
    D -->|Return logs/status| B
    G -->|Update status to 'completed'| E
    D -->|Notify user job is done| H
    D -->|Notify CLI user job is done| B
```

### Sequence Diagram
```mermaid
sequenceDiagram
    participant UI as Client UI
    participant CLI as CLI
    participant API as Backend API
    participant DB as Database
    participant MQ as Message Queue
    participant Worker as Worker

    UI ->> API: User triggers pipeline
    CLI ->> API: CLI user triggers pipeline
    API ->> DB: Store pipeline request
    API ->> MQ: Queue job execution
    MQ ->> API: Acknowledge job submission
    MQ ->> Worker: Assign CI/CD job execution
    Worker ->> DB: Update job status to 'running'
    Worker ->> API: Send job execution progress
    API ->> UI: Update UI with job status
    API ->> CLI: Return job execution logs/status
    Worker ->> DB: Update job status to 'completed'
    API ->> UI: Notify user job is done
    API ->> CLI: Notify CLI user job is done
```


