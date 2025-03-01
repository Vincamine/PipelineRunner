# 25Spring CS6510 Team 1 - CI/CD System

- **Title:** Phase 1 Journey
- **Date:** February 28, 2025
- **Author:** Yiwen Wang, Mingtianfang Li (Updated version)
- **Version:** 1.2

**Revision History**

|     Date     | Version |                  Description                   |           Author            |
| :----------: | :-----: | :--------------------------------------------: | :-------------------------: |
| Feb 28, 2025 |   1.0   |                Initial release                 | Yiwen Wang |


## Phase 1: Core Infrastructure - Minimal Viable Product (MVP)

1. CLI Module (completed)
   - Implement a simple command-line interface.
   - Support commands to run pipelines locally.
   - Provide basic logs for execution.
2. Backend Module (completed)
   - Implement basic API endpoints for triggering pipeline execution.
   - Provide local execution support without external dependencies.
3. Worker Module(completed)
   - Implement job execution logic.
   - Enable job isolation using Docker.
4. Reporting(completed)
   - Store basic execution logs. 
   - Generate simple reports for pipeline execution.

* **Key Validation Step:** (End to End integration is not completed, other completed)
      - Ensure a **basic CI/CD pipeline runs successfully** end-to-end using **CLI + Backend + Worker.**
      - Use unit and integration tests to validate.