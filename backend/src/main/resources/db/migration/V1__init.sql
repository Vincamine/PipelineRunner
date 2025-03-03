-- Drop tables in correct dependency order
DROP TABLE IF EXISTS job_executions CASCADE;
DROP TABLE IF EXISTS stage_executions CASCADE;
DROP TABLE IF EXISTS pipeline_executions CASCADE;
DROP TABLE IF EXISTS jobs CASCADE;
DROP TABLE IF EXISTS stages CASCADE;
DROP TABLE IF EXISTS pipelines CASCADE;

-- Table for storing pipeline definitions
CREATE TABLE IF NOT EXISTS pipelines (
                                         name VARCHAR(255) PRIMARY KEY,
                                         repository_url TEXT NOT NULL
);

-- Table for storing stage definitions
CREATE TABLE IF NOT EXISTS stages (
                                      id SERIAL PRIMARY KEY,
                                      pipeline_name VARCHAR(255) NOT NULL,
                                      name VARCHAR(255) NOT NULL,
                                      FOREIGN KEY (pipeline_name) REFERENCES pipelines(name) ON DELETE CASCADE
);

-- Table for storing job definitions
CREATE TABLE IF NOT EXISTS jobs (
                                    id SERIAL PRIMARY KEY,
                                    stage_id INT NOT NULL,
                                    name VARCHAR(255) NOT NULL,
                                    image VARCHAR(255) NOT NULL, -- Docker image
                                    allow_failure BOOLEAN DEFAULT FALSE,
                                    FOREIGN KEY (stage_id) REFERENCES stages(id) ON DELETE CASCADE
);

-- Separate table to store multiple scripts per job
CREATE TABLE IF NOT EXISTS job_scripts (
                                           job_id INT NOT NULL,
                                           script TEXT NOT NULL, -- Stores individual command lines
                                           FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
                                           PRIMARY KEY (job_id, script)  -- Prevent duplicate scripts per job
);


CREATE TABLE IF NOT EXISTS job_dependencies (
                                                job_id INT NOT NULL,
                                                depends_on_job_id INT NOT NULL,
                                                FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
                                                FOREIGN KEY (depends_on_job_id) REFERENCES jobs(id) ON DELETE CASCADE,
                                                PRIMARY KEY (job_id, depends_on_job_id)  -- Prevent duplicate dependencies
);


-- Table for storing pipeline executions (each run has a unique run_id)
CREATE TABLE IF NOT EXISTS pipeline_executions (
                                                   id SERIAL PRIMARY KEY,
                                                   pipeline_name VARCHAR(255) NOT NULL,
                                                   run_id VARCHAR(36) NOT NULL UNIQUE, -- Unique UUID from CLI
                                                   commit_hash VARCHAR(40) NOT NULL,
                                                   status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESSFUL', 'FAILED', 'CANCELED', 'QUEUED', 'SUCCESS')),
                                                   start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                   completion_time TIMESTAMP DEFAULT NULL,
                                                   FOREIGN KEY (pipeline_name) REFERENCES pipelines(name) ON DELETE CASCADE
);

-- Table for storing stage executions (linked to a pipeline execution)
CREATE TABLE IF NOT EXISTS stage_executions (
                                                id SERIAL PRIMARY KEY,
                                                pipeline_execution_id INT NOT NULL,
                                                run_id VARCHAR(36) NOT NULL, -- Matches CLI-generated run ID
                                                stage_name VARCHAR(255) NOT NULL, -- Store stage name directly
                                                status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESSFUL', 'FAILED', 'CANCELED', 'QUEUED', 'SUCCESS')),
                                                start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                completion_time TIMESTAMP DEFAULT NULL,
                                                FOREIGN KEY (pipeline_execution_id) REFERENCES pipeline_executions(id) ON DELETE CASCADE
);

-- Table for storing job executions (linked to a stage execution)
CREATE TABLE IF NOT EXISTS job_executions (
                                              id SERIAL PRIMARY KEY,
                                              stage_execution_id INT NOT NULL,
                                              run_id VARCHAR(36) NOT NULL, -- Matches CLI-generated run ID
                                              job_id INT NOT NULL,
                                              status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESSFUL', 'FAILED', 'CANCELED', 'QUEUED', 'SUCCESS')),
                                              allows_failure BOOLEAN NOT NULL DEFAULT FALSE,
                                              start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              completion_time TIMESTAMP DEFAULT NULL,
                                              FOREIGN KEY (stage_execution_id) REFERENCES stage_executions(id) ON DELETE CASCADE,
                                              FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

-- Indexes for faster lookups
CREATE INDEX idx_pipeline_name ON pipelines(name);
CREATE INDEX idx_pipeline_repo ON pipelines(repository_url);
CREATE INDEX idx_pipeline_executions ON pipeline_executions(pipeline_name, run_id);
CREATE INDEX idx_stage_executions ON stage_executions(pipeline_execution_id, run_id);
CREATE INDEX idx_job_executions ON job_executions(stage_execution_id, run_id);
