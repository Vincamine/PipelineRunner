-- Enable pgcrypto extension (Required for UUID generation)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create pipelines table
CREATE TABLE pipelines (
                           name VARCHAR(255) PRIMARY KEY NOT NULL
);

-- Create stages table
CREATE TABLE stages (
                        id BIGSERIAL PRIMARY KEY,  -- Changed SERIAL to BIGSERIAL
                        name VARCHAR(255) NOT NULL,
                        pipeline_name VARCHAR(255) NOT NULL REFERENCES pipelines(name) ON DELETE CASCADE
);

-- Create jobs table
CREATE TABLE jobs (
                      id BIGSERIAL PRIMARY KEY,  -- Changed SERIAL to BIGSERIAL
                      name VARCHAR(255) NOT NULL,
                      image VARCHAR(255) NOT NULL,
                      stage_id BIGINT NOT NULL REFERENCES stages(id) ON DELETE CASCADE,
                      allow_failure BOOLEAN NOT NULL,
                      start_time TIMESTAMP,
                      completion_time TIMESTAMP
);

-- Job scripts table
CREATE TABLE job_scripts (
                             id BIGSERIAL PRIMARY KEY,  -- Changed SERIAL to BIGSERIAL
                             job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
                             script TEXT NOT NULL
);

-- Pipeline Execution Table
CREATE TABLE pipeline_execution (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  -- Ensure pgcrypto is enabled
                                    pipeline_name VARCHAR(255) NOT NULL REFERENCES pipelines(name) ON DELETE CASCADE,
                                    repository_url TEXT NOT NULL,
                                    branch VARCHAR(255) NOT NULL,
                                    commit_hash VARCHAR(255) NOT NULL,
                                    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED')),
                                    created_at TIMESTAMP DEFAULT NOW(),
                                    updated_at TIMESTAMP DEFAULT NOW()
);

-- Job Execution Table
CREATE TABLE job_execution (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  -- Ensure pgcrypto is enabled
                               pipeline_execution_id UUID NOT NULL REFERENCES pipeline_execution(id) ON DELETE CASCADE,
                               job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
                               status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED')),
                               logs TEXT,
                               started_at TIMESTAMP DEFAULT NOW(),
                               completed_at TIMESTAMP
);

-- Indexes for faster queries
CREATE INDEX idx_pipeline_execution_status ON pipeline_execution(status);
CREATE INDEX idx_job_execution_status ON job_execution(status);
CREATE INDEX idx_job_execution_pipeline ON job_execution(pipeline_execution_id);
CREATE INDEX idx_pipeline_execution_pipeline ON pipeline_execution(pipeline_name);


