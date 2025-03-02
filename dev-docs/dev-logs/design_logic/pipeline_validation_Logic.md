# Pipeline Validation Logic

- **Version:** 2.0
- **Updated:** March 2, 2025
- **Author:** Yiwen Wang
- **Description:** This document outlines the CI/CD pipeline validation process, structured error reporting, and validation constraints.

## **1. Overview**

Pipeline validation ensures that the CI/CD configuration meets structural, dependency, and execution constraints before running. The validation system:

- Ensures correct YAML parsing with **exact line and column tracking**.
- Validates job dependencies and prevents **cyclic dependencies**.
- Uses structured **error reporting** in `<filename>:<line>:<error-message>` format.
- Uses **`ValidationException`** for handling errors across validators.

## **2. Validation Workflow**

1. **YAML Parsing**

  - Parses the pipeline file into `PipelineConfig`.
  - Extracts **line numbers** using `YamlParser` for better error tracking.
  - Throws `ValidationException` if parsing fails.

2. **Pipeline Structure Validation**

  - Ensures required fields (`pipeline`, `stages`, `jobs`) exist.
  - Verifies that each stage contains at least one job.

3. **Job Validation**

  - Ensures **job uniqueness** (no duplicate names).
  - Validates required job attributes: `name`, `stage`, `image`, `script`.
  - Ensures **jobs reference existing stages**.
  - Uses `YamlParser.getFieldLineNumber()` to **track field locations**.

4. **Dependency Validation**

  - Ensures jobs listed in `needs` reference **existing jobs**.

  - Detects **cyclic dependencies** using DFS (Depth-First Search).

  - Reports cycles in the format:

    ```
    pipeline.yaml:12: Cyclic dependency detected: jobA -> jobB -> jobC -> jobA
    ```

5. **Final Validation & Error Handling**

  - Collects all validation errors.
  - Throws `ValidationException` if any issue is found.

## **3. Structured Error Reporting**

All errors are formatted as:

```
<filename>:<line>:<error-message>
```

**Example Errors:**

```
pipeline.yaml:5: Job 'deploy' depends on non-existent job 'build'
pipeline.yaml:10: Job 'test' must specify an image.
pipeline.yaml:12: Cyclic dependency detected: jobA -> jobB -> jobC -> jobA
```

### **Error Handling in Code**

- **YAML Parsing Errors**

  ```java
  catch (YAMLException e) {
    int line = (e instanceof MarkedYAMLException markedE) ? markedE.getProblemMark().getLine() + 1 : 1;
    throw new ValidationException(yamlFile.getName(), line, "YAML parsing error: " + e.getMessage());
  }
  ```

- **Validation Errors**

  ```java
  if (job.getImage() == null || job.getImage().isEmpty()) {
    errors.add(formatError(filename, "image", "Job '" + job.getName() + "' must specify an image."));
  }
  ```

## **4. Key Classes in Validation**

| Class                   | Responsibility                                         |
| ----------------------- | ------------------------------------------------------ |
| `YamlParser`            | Parses YAML and extracts line numbers                  |
| `JobValidator`          | Validates individual jobs and required fields          |
| `PipelineValidator`     | Ensures pipeline structure and detects cycles          |
| `YamlPipelineValidator` | Integrates all validators and runs the full validation |



