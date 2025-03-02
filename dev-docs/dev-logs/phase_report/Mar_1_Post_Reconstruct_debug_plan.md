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
