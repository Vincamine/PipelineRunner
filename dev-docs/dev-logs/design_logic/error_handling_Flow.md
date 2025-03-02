# Error Handling Flow
- Version: 1.0
- Updated with the Project requirements by Feb 28, 2025
- Author: Yiwen Wang
This document describes the structured error handling process in the YAML-based pipeline validation system. The goal is to provide accurate and meaningful error reporting with exact line and column numbers for debugging.

## 1. Error Sources

Errors can occur in the following stages:

1. **YAML Parsing** (handled in `YamlParser`)
2. **Pipeline Structure Validation** (handled in `PipelineValidator`)
3. **Job Configuration Validation** (handled in `JobValidator`)
4. **Dependency Validation** (handled in `PipelineValidator`)
5. **Git Repository Validation** (handled in `GitValidator`)

## 2. Error Handling Strategy

- **Use `ValidationException`** for all validation-related errors.

- **Track error locations** using `YamlParser` and `ErrorHandler.Location`.

- **Report errors with filename, line number, and message** in the format:

  ```
  filename.yaml:12: Error message here
  ```

## 3. Error Handling in YAML Parsing

### Component: `YamlParser`

### Handled Errors:

1. **File Not Found** → Throws `ValidationException`
2. **Invalid YAML Syntax** → Throws `ValidationException`
3. **Parsing Error** (Malformed YAML) → Throws `ValidationException`
4. **Empty YAML File** → Throws `ValidationException`

### Flow:

```plaintext
YamlParser.parseYaml(yamlFile)
│
├── Try loading YAML content
│   ├── If file not found → throw ValidationException(fileName, 0, "File not found")
│   ├── If empty YAML → throw ValidationException(fileName, 1, "Empty YAML file")
│   ├── Parse YAML into a Map
│   ├── Extract line/column locations using SnakeYAML
│   ├── If YAMLException:
│   │   ├── Get exact line from exception (if available)
│   │   ├── throw ValidationException(fileName, line, "YAML parsing error: <message>")
│   ├── Convert YAML data to PipelineConfig
└── Return parsed PipelineConfig
```

## 4. Error Handling in Pipeline Structure Validation

### Component: `PipelineValidator`

### Handled Errors:

1. **Missing Required Fields** → Throws `ValidationException`
2. **Empty Pipeline Name** → Throws `ValidationException`
3. **Missing Stages Section** → Throws `ValidationException`
4. **Duplicate Job Names** → Throws `ValidationException`
5. **Cyclic Dependencies** → Throws `ValidationException`

### Flow:

```plaintext
PipelineValidator.validate(pipelineConfig, fileName)
│
├── Check pipeline name
│   ├── If missing → throw ValidationException(fileName, getFieldLineNumber("name"), "Pipeline name is required")
│
├── Check at least one stage exists
│   ├── If missing → throw ValidationException(fileName, getFieldLineNumber("stages"), "At least one stage is required")
│
├── Check job uniqueness
│   ├── If duplicate found → throw ValidationException(fileName, getFieldLineNumber(jobName), "Duplicate job name found")
│
├── Validate dependencies
│   ├── If job depends on non-existent job → throw ValidationException(fileName, getFieldLineNumber(jobName), "Job '<job>' depends on non-existent job")
│
├── Detect cyclic dependencies
│   ├── If cycle found → throw ValidationException(fileName, getFieldLineNumber("needs"), "Cyclic dependency detected: <cycle>")
└── If no errors, validation passes
```

## 5. Error Handling in Job Validation

### Component: `JobValidator`

### Handled Errors:

1. **Missing Required Fields** (`name`, `stage`, `image`, `script`) → Throws `ValidationException`
2. **Job References Non-existent Stage** → Throws `ValidationException`
3. **Duplicate Job Names in Pipeline** → Throws `ValidationException`
4. **Empty Script Section** → Throws `ValidationException`

### Flow:

```plaintext
JobValidator.validateJobs(stages, fileName)
│
├── Iterate over stages
│   ├── Iterate over jobs
│   │   ├── Validate `name`
│   │   │   ├── If missing → throw ValidationException(fileName, getFieldLineNumber("name"), "Job must have a name")
│   │   ├── Validate `stage`
│   │   │   ├── If missing → throw ValidationException(fileName, getFieldLineNumber("stage"), "Job must specify a stage")
│   │   │   ├── If stage does not exist → throw ValidationException(fileName, getFieldLineNumber("stage"), "Job references non-existent stage")
│   │   ├── Validate `image`
│   │   │   ├── If missing → throw ValidationException(fileName, getFieldLineNumber("image"), "Job must specify an image")
│   │   ├── Validate `script`
│   │   │   ├── If empty → throw ValidationException(fileName, getFieldLineNumber("script"), "Job must have at least one script command")
│   │   ├── Validate job uniqueness
│   │   │   ├── If duplicate → throw ValidationException(fileName, getFieldLineNumber(jobName), "Duplicate job name found")
└── If no errors, validation passes
```

## 6. Error Reporting Format

### All errors follow the format:

```
<filename>:<line>:<error-message>
```

**Example Output:**

```
pipeline.yaml:12: Job 'deploy' references a non-existent stage 'production'
pipeline.yaml:20: Duplicate job name found: 'test'
pipeline.yaml:35: Cyclic dependency detected: build -> test -> build
```

## 7. Summary

| Component           | Handled Errors                       | Exception Thrown        |
| ------------------- | ------------------------------------ | ----------------------- |
| `YamlParser`        | Invalid YAML, Parsing Issues         | `ValidationException`   |
| `PipelineValidator` | Missing Fields, Cyclic Dependencies  | `ValidationException`   |
| `JobValidator`      | Missing Job Fields, Stage References | `ValidationException`   |
| `GitValidator`      | Not in a Git Repository              | `IllegalStateException` |

This structured approach ensures:
✔️ **Errors are detected early.**  
✔️ **Errors are clearly reported with line numbers.**  
✔️ **Users can quickly debug YAML configuration issues.**  