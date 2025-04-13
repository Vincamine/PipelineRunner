# Data Storage Design Document

**Author**: Wenxue Fang, Team 1, CS6510 Spring 2025
**Date**: April 13, 2025

## Overview

This document describes the data storage architecture for the CI/CD Pipeline System. The system uses PostgreSQL as its primary data store to manage pipeline definitions, execution history, and logs.

## Database Configuration

- **Database Engine**: PostgreSQL
- **Configuration Location**: `backend/src/main/resources/application.yml`
- **Migration Scripts**: `backend/src/main/resources/db/migration/V1__init.sql`
- **Connection URL**: `jdbc:postgresql://postgres:5432/cicd_db`

## Entity Relationship Diagram

```
Pipeline 1──┐
  │         │
  │         ↓
  ├───► Stage 1───┐
  │      │        │
  │      │        ↓
  │      ├───► Job 1 ◄─┐
  │      │        │    │
  │      │        │    │ dependencies
  │      │        ↓    │
  ↓      ↓      Job 2 ─┘
PipelineExecution
  │
  ├───► StageExecution
  │       │
  │       ├───► JobExecution
  │       │        │
  │       │        ↓
  │       │     ExecutionLog
  ↓       ↓
ExecutionLog ExecutionLog
```

## Database Schema

### Pipeline Management

#### Pipeline
```sql
CREATE TABLE pipelines (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    repository_url VARCHAR(1024) NOT NULL,
    branch VARCHAR(255) DEFAULT 'main',
    commit_hash VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_pipeline_name ON pipelines(name);
CREATE INDEX idx_pipeline_repo ON pipelines(repository_url);
```

#### Stage
```sql
CREATE TABLE stages (
    id UUID PRIMARY KEY,
    pipeline_id UUID NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    execution_order INT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_pipeline_stages ON stages(pipeline_id);
```

#### Job
```sql
CREATE TABLE jobs (
    id UUID PRIMARY KEY,
    stage_id UUID NOT NULL REFERENCES stages(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    docker_image VARCHAR(255) NOT NULL,
    allow_failure BOOLEAN DEFAULT FALSE,
    working_dir VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### Job Dependencies
```sql
CREATE TABLE job_dependencies (
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    depends_on_job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    PRIMARY KEY (job_id, depends_on_job_id)
);
```

#### Job Scripts
```sql
CREATE TABLE job_scripts (
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    script TEXT NOT NULL,
    script_order INT NOT NULL,
    PRIMARY KEY (job_id, script_order)
);
```

#### Job Artifacts
```sql
CREATE TABLE job_artifacts (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    artifact_path VARCHAR(1024) NOT NULL
);
```

### Execution Tracking

#### Pipeline Execution
```sql
CREATE TABLE pipeline_executions (
    id UUID PRIMARY KEY,
    pipeline_id UUID NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
    run_number INT NOT NULL,
    commit_hash VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    start_time TIMESTAMP,
    completion_time TIMESTAMP,
    is_local BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP
);
```

#### Stage Execution
```sql
CREATE TABLE stage_executions (
    id UUID PRIMARY KEY,
    pipeline_execution_id UUID NOT NULL REFERENCES pipeline_executions(id) ON DELETE CASCADE,
    stage_id UUID NOT NULL REFERENCES stages(id) ON DELETE CASCADE,
    execution_order INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_time TIMESTAMP,
    completion_time TIMESTAMP,
    is_local BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP
);
```

#### Job Execution
```sql
CREATE TABLE job_executions (
    id UUID PRIMARY KEY,
    stage_execution_id UUID NOT NULL REFERENCES stage_executions(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    start_time TIMESTAMP,
    completion_time TIMESTAMP,
    allows_failure BOOLEAN DEFAULT FALSE,
    is_local BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP
);
```

#### Execution Logs
```sql
CREATE TABLE execution_logs (
    id UUID PRIMARY KEY,
    pipeline_execution_id UUID REFERENCES pipeline_executions(id) ON DELETE CASCADE,
    stage_execution_id UUID REFERENCES stage_executions(id) ON DELETE CASCADE,
    job_execution_id UUID REFERENCES job_executions(id) ON DELETE CASCADE,
    log_text TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## JPA Entities

### Entity Classes

1. **PipelineEntity**: Represents a pipeline definition
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/PipelineEntity.java`
   - Maps to `pipelines` table

2. **StageEntity**: Represents a stage within a pipeline
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/StageEntity.java`
   - Maps to `stages` table

3. **JobEntity**: Represents a job within a stage
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/JobEntity.java`
   - Maps to `jobs` table

4. **JobDependencyEntity**: Represents a dependency between jobs
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/JobDependencyEntity.java`
   - Maps to `job_dependencies` table

5. **PipelineExecutionEntity**: Tracks pipeline execution instance
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/PipelineExecutionEntity.java`
   - Maps to `pipeline_executions` table

6. **StageExecutionEntity**: Tracks stage execution instance
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/StageExecutionEntity.java`
   - Maps to `stage_executions` table

7. **JobExecutionEntity**: Tracks job execution instance
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/JobExecutionEntity.java`
   - Maps to `job_executions` table

8. **ExecutionLogEntity**: Stores execution logs
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/entity/ExecutionLogEntity.java`
   - Maps to `execution_logs` table

### Repository Interfaces

1. **PipelineRepository**: Repository for pipeline operations
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/repository/PipelineRepository.java`

2. **StageRepository**: Repository for stage operations
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/repository/StageRepository.java`

3. **JobRepository**: Repository for job operations
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/repository/JobRepository.java`

4. **JobScriptRepository**: Repository for job script operations
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/repository/JobScriptRepository.java`

5. **PipelineExecutionRepository**: Repository for pipeline execution operations
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/repository/PipelineExecutionRepository.java`

6. **StageExecutionRepository**: Repository for stage execution operations
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/repository/StageExecutionRepository.java`

7. **JobExecutionRepository**: Repository for job execution operations
   - Location: `backend/src/main/java/edu/neu/cs6510/sp25/t1/backend/database/repository/JobExecutionRepository.java`

## Data Flow

1. **Pipeline Creation**:
   - Pipeline YAML file is parsed into a Pipeline model
   - Models are mapped to entities and persisted to database
   - Stages, jobs, and their relationships are stored in respective tables

2. **Pipeline Execution**:
   - When a pipeline is executed, a new PipelineExecution record is created
   - As stages and jobs run, their execution records are created and updated
   - Logs are continuously written to the ExecutionLog table
   - Status updates flow from JobExecution to StageExecution to PipelineExecution

3. **Report Generation**:
   - Reports are generated by querying execution records
   - JOIN operations link pipeline definitions with execution history
   - Execution logs are retrieved for detailed reporting

## Data Persistence Strategy

1. **Transactional Integrity**:
   - All database operations are wrapped in transactions
   - Uses Spring's `@Transactional` annotation for consistency

2. **Optimistic Locking**:
   - Uses versioning to prevent concurrent modification issues
   - JPA's `@Version` annotation on appropriate entities

3. **Data Validation**:
   - Enforces constraints at both application and database levels
   - Validation annotations on entity fields

4. **Audit Tracking**:
   - All entities track creation and update timestamps
   - Allows for historical analysis and troubleshooting

## Data Migration Strategy

1. **Schema Versioning**:
   - Uses Flyway for database migrations
   - Migration scripts in `backend/src/main/resources/db/migration/`
   - Versioned with V1__, V2__, etc. prefix

2. **Backward Compatibility**:
   - Migrations designed to be non-destructive
   - New features add columns rather than modifying existing ones when possible

3. **Rollback Support**:
   - Critical migrations include rollback scripts
   - Enables recovery from failed migrations

## Scaling Considerations

1. **Index Optimization**:
   - Primary keys and foreign keys are indexed
   - Additional indexes on frequently queried columns

2. **Query Performance**:
   - Uses pagination for large result sets
   - Optimized JPQL queries for complex operations

3. **Connection Pooling**:
   - HikariCP for efficient connection management
   - Configurable pool size based on deployment environment

4. **Data Archiving**:
   - Long-term strategy includes archiving old execution records
   - Maintains performance as system usage grows