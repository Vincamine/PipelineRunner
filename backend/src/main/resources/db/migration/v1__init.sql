-- Create pipelines table
CREATE TABLE pipelines (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Create stages table
CREATE TABLE stages (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    pipeline_id BIGINT NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE
);

-- Create jobs table
CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL,
    stage_id BIGINT NOT NULL REFERENCES stages(id) ON DELETE CASCADE,
    allow_failure BOOLEAN NOT NULL,
    start_time TIMESTAMP,
    completion_time TIMESTAMP
);

-- Create job scripts table
CREATE TABLE job_scripts (
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    script TEXT NOT NULL
);
