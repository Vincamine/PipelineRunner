# 25Spring CS6510 Team 1 - CI/CD System
- **Title:** High Level Architecture design file
- **Date:** Jan 31, 2025
- **Author:** Yiwen Wang
- **Version:** 1.0

**Revision History**
|Date|Version|Description|Author|
|:----:|:----:|:----:|:----:|
|Jan 31, 2025|1.0|Initial release| Yiwen Wang|

```mermaid
graph TD;
    A[Developer Machine] -->|Triggers Execution| B[CI/CD Pipeline Runner (CLI)];
    B -->|Fetches Pipeline Config| C[Git Repository (Local/Remote)];
    B -->|Reads Config| D[Configuration & YAML Parser];
    D -->|Validates YAML| E[Execution Engine];
    E -->|Processes Jobs| F[Error Reporting Module];
    E -->|Logs Execution| G[Logging & Reporting];
    F -->|Stores Errors| G;
```