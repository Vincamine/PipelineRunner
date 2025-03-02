# CI/CD System Development Plan

## **Objective**
Complete all 5 sprints and prepare a working demo for the CI/CD system, ensuring the CLI correctly reads YAML files, validates configurations, executes pipelines, and provides reporting.

---

## **Phase 1: Fix YAML Parsing & Validation (Sprint 2)**
### **Goal:**
Ensure the CLI correctly reads, validates, and sends YAML pipeline configurations to the backend.

### **Tasks:**
1. **Fix CLI YAML Parsing Issue**
    - Ensure CLI reads the YAML file from `.pipelines/` directory.
    - Parse YAML into a structured format (e.g., Java object or Map).
    - Send the **correctly parsed** pipeline structure (not just `{ pipeline: <command-name> }`) to the backend.

2. **Implement YAML Validation Rules**
    - Check for:
        - Required keys: `pipeline`, `stages`, `jobs`.
        - Unique pipeline names per repository.
        - At least **one stage and one job** per pipeline.
        - No cyclic dependencies between jobs.
    - Implement structured **error reporting**:
        - Format: `<filename>:<line>:<error-message>`.

3. **Update CLI to Accept YAML Configurations**
    - Support `--filename | -f` flag to specify pipeline config file.
    - Implement `--check` flag to validate configuration without execution.

---

## **Phase 2: Pipeline Execution (Sprint 3)**
### **Goal:**
Enable sequential pipeline execution with dependency resolution.

### **Tasks:**
1. **Implement CLI Execution Commands**
    - `dry-run`: Simulate execution without running jobs.
    - `run`: Execute the pipeline locally.

2. **Ensure Sequential Execution of Jobs**
    - No parallel execution for now.
    - Respect **job dependencies** (`needs`).

3. **Validate Execution Order**
    - Ensure pipeline jobs run in the expected sequence.
    - Handle failures gracefully if a job is set to "allow failure".

---

## **Phase 3: Basic Reporting (Sprint 4)**
### **Goal:**
Provide CLI commands for tracking past pipeline runs.

### **Tasks:**
1. **Implement CLI `report` command**
    - Generate summary of pipeline runs.
    - Track execution status per pipeline.

2. **Ignore Advanced Features for Now**
    - No job output storage.
    - No artifact uploads.

---

## **Phase 4: Advanced Reporting & Docker Execution (Sprint 5)**
### **Goal:**
Extend reporting and enable pipeline execution inside Docker containers.

### **Tasks:**
1. **Extend CLI `report` Command**
    - Include **stage-level** and **job-level** summaries.
    - Store execution history persistently.

2. **Enable Docker-based Execution**
    - Replace shell-based execution with Docker containers.
    - Use **official language libraries** or **Docker Service REST API** for interaction.

3. **Implement Persistent Data Storage for Reports**
    - Store execution records in **SQL/NoSQL database**.

---

## **Phase 5: Final Demo & Documentation**
### **Goal:**
Showcase the working CI/CD system in a structured demo.

### **Tasks:**
1. **Prepare a Recorded Video Demo**
    - Cover:
        - **Config validation** (`--check`).
        - **Executing pipelines** (`dry-run` and `run`).
        - **Viewing reports** (`report`).
    - Record screen & voice-over.
    - Store demo in a shared repository.

2. **Write Final Documentation**
    - Update `README.md` with:
        - Installation instructions.
        - Example pipeline configurations.
        - CLI usage guide.

---


### **Resources Needed**
- YAML parsing & validation library for Java.
- Database for storing pipeline execution history.
- Docker setup for running jobs in containers.
- Recording tool for demo video.

---
## CI/CD Pipeline CLI Development Checklist

### **1. Design and Implement CLI Client**
- [ ] Create CLI tool with required functionalities.
- [ ] Ensure it integrates correctly with backend services.

### **2. CLI Processing of Pipeline Configuration Files**
- [ ] CLI must be run from the root of a Git repository.
- [ ] Support configuration files stored in `.pipelines/`.
- [ ] Allow specifying config file via `--filename | -f`.
- [ ] Provide `--check` option to validate config files without execution.

### **3. Validation of Pipeline YAML Definitions**
- [x] YAML v1.2 format support.
- [x] Validate keys:
   - [x] `pipeline` (required, includes `name`, `stages`).
   - [x] `stages` (required, execution order defined by list order).
   - [x] `job` (at least one per stage, including `name`, `stage`, `image`, `script`, `needs`).
- [x] Enforce constraints:
   - [x] Unique pipeline names inside repo.
   - [x] At least one stage defined.
   - [x] Unique job names within a stage.
   - [x] No cyclic dependencies.
   - [x] Valid `needs` references.

### **4. Error Reporting**
- [x] CLI must:
   - [x] Report errors with `<filename>:<line-number>:<column-number>:<error-message>` format.
   - [x] Detect cycles in job dependencies.
   - [x] Identify type mismatches in configuration values.

### **5. CLI Commands for Dry-Run and Local Execution**
- [x] Implement:
   - [x] `dry-run`: Simulates execution without running jobs.
   - [x] `run`: cli command for run for a local repo to be executed on the local machine

### **6. Execution Flow**
- [x] No parallel execution (sequential processing only).
- [x] Ensure `needs` dependencies are obeyed.

### **7. CLI Command for Reporting Past Pipeline Runs**
- [ ] Implement CLI `report` command.
- [ ] Generate:
   - [ ] Summary of pipeline run for repository (L4.1, L4.2).
- [ ] Show past pipeline runs:
   - [ ] CLI retrieves past runs of any pipeline of a repository.
   - [ ] Show summary of all past pipeline runs.
   - [ ] Support querying specific pipeline runs by name or number.

### **8. Extended Reporting for Stage/Job-Level Summaries**
- [ ] Extend CLI `report` command to include:
   - [ ] Stage-level pipeline run summary (L4.3).
   - [ ] Job-level pipeline run summary (L4.4).
- [ ] Implement:
   - [ ] Retrieval of stage summary from a pipeline.
   - [ ] Retrieval of job summary within a stage.

### **9. Pipeline Execution Inside Docker Containers**
- [ ] Replace mocked/shell escape Docker calls with:
   - [ ] Official language libraries (e.g., Go/Python).
   - [ ] Direct interaction with Docker Service REST API.

### **10. Reports Stored in a Persistent Data Store**
- [ ] Implement reports with:
   - [ ] SQL/NoSQL storage for historical access.

### **Out of Scope**
- [x] Ignore:
   - `allow failures` feature.
   - Multiple configuration files per repo.
   - Job output storage & artifact uploads.

