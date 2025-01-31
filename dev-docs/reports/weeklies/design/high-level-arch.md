# 25Spring CS6510 Team 1 - CI/CD System
- **Title:** High Level Architecture design file
- **Date:** Jan 31, 2025
- **Author:** Yiwen Wang
- **Version:** 1.0

**Revision History**
|Date|Version|Description|Author|
|:----:|:----:|:----:|:----:|
|Jan 31, 2025|1.0|Initial release| Yiwen Wang|

# Design Proposal

## System Components

- **Developer Machines** - A developer triggers the pipeline execution using the CLI.
- **Git Repository (Local & Remote)** - The CLI interacts with the repository to fetch pipeline configurations stored in `.pipelines/`.
- **CI/CD Pipeline Runner (CLI)** - Acts as the main interface for executing and managing pipelines.
- **Configuration & YAML Parser** - Reads and validates the `.yaml` pipeline configurations.
- **Execution Engine** - Runs jobs in parallel or sequentially based on dependencies.
- **Error Reporting Module** - Captures syntax errors, missing dependencies, and execution failures.
- **Logging & Reporting Module** - Stores execution logs and provides reports on past executions.

## Workflow

1. Developers trigger the pipeline using the CLI.
2. The system reads pipeline configurations stored in the `.pipelines/` directory.
3. The YAML parser validates the configuration.
4. The execution engine runs jobs in parallel or sequentially based on dependencies.
5. Error handling and logging modules provide feedback.
6. Reports are stored and accessible via the CLI.

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

