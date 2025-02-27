# 25Spring CS6510 Team 1 - CI/CD System
- **Title:** Low-Level Design Document: Custom CI/CD System (Monorepo)
- **Date:** Feb 27, 2025
- **Author:** Yiwen Wang
- **Version:** 1.0

**Revision History**
|Date|Version|Description|Author|
|:----:|:----:|:----:|:----:|
|Feb 27, 2025|1.1|Structure change to mono repo| Yiwen Wang|

# Low-Level Design Document: Custom CI/CD System (Monorepo)

## 1. Repository Structure

The system will be structured as a Gradle multi-module project within a single monorepo:

```bash
cicd-system/
├── build.gradle                # Root build file with common configurations
├── settings.gradle             # Module definitions
├── gradle.properties           # Global properties
├── gradlew                     # Gradle wrapper script
├── gradlew.bat                 # Gradle wrapper script for Windows
├── cli/                        # CLI Application module
│   ├── build.gradle            # CLI-specific dependencies and build config
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── edu/neu/cs6510/sp25/t1/cli/
│       │   │       ├── commands/          # Command implementations
│       │   │       ├── api/               # Backend API client
│       │   │       ├── model/             # CLI-specific models
│       │   │       ├── util/              # CLI-specific utilities
│       │   │       └── App.java           # Entry point
│       │   └── resources/
│       └── test/
├── backend/                    # Backend Service module
│   ├── build.gradle            # Backend-specific dependencies and build config
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── edu/neu/cs6510/sp25/t1/backend/
│       │   │       ├── api/               # API controllers
│       │   │       ├── service/           # Business logic
│       │   │       ├── repository/        # Data access
│       │   │       ├── worker/            # Worker communication
│       │   │       └── BackendApplication.java # Entry point
│       │   └── resources/
│       │       ├── application.yml        # Application config
│       │       └── db/migration/          # Flyway migrations
│       └── test/
├── worker/                     # Worker Service module
│   ├── build.gradle            # Worker-specific dependencies and build config
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── edu/neu/cs6510/sp25/t1/worker/
│       │   │       ├── executor/          # Job execution
│       │   │       ├── docker/            # Docker integration
│       │   │       ├── artifact/          # Artifact handling
│       │   │       ├── backend/           # Backend communication
│       │   │       └── WorkerApplication.java # Entry point
│       │   └── resources/
│       └── test/
├── common/                     # Shared code module
│   ├── build.gradle            # Common dependencies and build config
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── edu/neu/cs6510/sp25/t1/common/
│       │   │       ├── model/             # Shared domain models
│       │   │       ├── util/              # Shared utilities
│       │   │       ├── validation/        # Shared validation logic
│       │   │       └── config/            # Configuration models
│       │   ├── proto/                     # Protocol definitions for gRPC
│       │   │   └── worker.proto           # Worker service protocol
│       │   └── resources/
│       └── test/
└── docs/                       # Project documentation
    ├── architecture.md         # Architecture documentation
    ├── user-guide.md           # User documentation
    └── development.md          # Developer documentation
```

## 2. Gradle Configuration

### 2.1 Root build.gradle

```gradle
plugins {
    id 'java-library' apply false
    id 'org.springframework.boot' version '3.2.2' apply false
    id 'io.spring.dependency-management' version '1.1.4' apply false
    id 'com.google.protobuf' version '0.9.4' apply false
    id 'com.github.johnrengelman.shadow' version '8.1.1' apply false
}

allprojects {
    group = 'edu.neu.cs6510.sp25.t1'
    version = '0.1.0'
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply plugin: 'java-library'
    
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    dependencies {
        // Common dependencies for all modules
        implementation 'org.slf4j:slf4j-api:2.0.11'
        testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
        testImplementation 'org.mockito:mockito-core:5.8.0'
    }
    
    test {
        useJUnitPlatform()
    }
}

// Define common dependency versions to ensure consistency
ext {
    springBootVersion = '3.2.2'
    jacksonVersion = '2.16.1'
    grpcVersion = '1.60.0'
    snakeYamlVersion = '2.2'
    logbackVersion = '1.4.14'
    jgitVersion = '6.7.0.202309050840-r'
    dockerJavaVersion = '3.3.4'
}
```

**Technology Choices:**

- **Gradle**: Selected as the build system for its flexibility, performance, and excellent support for multi-module projects. Gradle offers incremental builds, parallel execution, and a powerful Groovy-based DSL.
- **Java 21**: Chosen for its modern language features, long-term support, and virtual threads capability which significantly improves concurrent application performance.
- **JUnit 5**: Selected for testing because it offers parameterized tests, improved assertions, and better extension model compared to JUnit 4.
- **Mockito**: Used for mocking in tests due to its intuitive API and comprehensive mocking capabilities.
- **SLF4J**: Implemented as a logging facade to enable consistent logging patterns while allowing different logging implementations.

### 2.2 settings.gradle

```gradle
rootProject.name = 'cicd-system'

include 'cli'
include 'backend'
include 'worker'
include 'common'
```

### 2.3 Individual Module build.gradle Files

#### 2.3.1 common/build.gradle

```gradle
plugins {
    id 'java-library'
    id 'com.google.protobuf'
}

dependencies {
    // JSON handling
    api "com.fasterxml.jackson.core:jackson-databind:${rootProject.jacksonVersion}"
    api "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${rootProject.jacksonVersion}"
    
    // YAML parsing
    api "org.yaml:snakeyaml:${rootProject.snakeYamlVersion}"
    
    // Protocol buffers and gRPC
    implementation "io.grpc:grpc-protobuf:${rootProject.grpcVersion}"
    implementation "io.grpc:grpc-stub:${rootProject.grpcVersion}"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        grpc {
            artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.grpcVersion}"
        }
    }
    generateProtoTasks {
        all()*.plugins {
            grpc {}
        }
    }
}
```

**Technology Choices:**

- **Protocol Buffers**: Chosen for its efficient binary serialization format, strong typing, and cross-language compatibility. It provides a compact, fast, and forward-compatible mechanism for serializing structured data.
- **gRPC**: Selected for high-performance RPC communication between Backend and Worker components. gRPC uses HTTP/2 for transport, supports streaming, and works well in microservices architectures.
- **Jackson**: Used for JSON processing due to its performance, flexibility, and excellent integration with Java objects. Jackson provides robust data binding and offers specialized modules for date/time handling.
- **SnakeYAML**: Chosen for YAML parsing due to its comprehensive YAML support, permissive license, and good performance. It's used for reading pipeline configuration files.

#### 2.3.2 cli/build.gradle

```gradle
plugins {
    id 'application'
    id 'com.github.johnrengelman.shadow'
}

dependencies {
    implementation project(':common')
    
    // Command line parsing
    implementation 'info.picocli:picocli:4.7.5'
    annotationProcessor 'info.picocli:picocli-codegen:4.7.5'
    
    // HTTP client
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    
    // Logging
    implementation "ch.qos.logback:logback-classic:${rootProject.logbackVersion}"
}

application {
    mainClass = 'edu.neu.cs6510.sp25.t1.cli.App'
}

shadowJar {
    archiveBaseName.set('cicd-cli')
    archiveClassifier.set('')
    archiveVersion.set('')
}

// Task to create executable distribution
task createDistribution(type: Copy, dependsOn: shadowJar) {
    from('scripts') {
        include 'cicd'
        include 'cicd.bat'
        fileMode = 0755
    }
    from shadowJar.archiveFile
    into "${buildDir}/dist"
}

build.dependsOn createDistribution
```

**Technology Choices:**

- **Picocli**: Selected for command-line parsing because it offers annotation-based command definition, built-in help generation, autocompletion, and type conversion. Picocli is specifically designed for modern Java applications and supports nested commands.
- **OkHttp**: Chosen as the HTTP client for its efficient connection pooling, easy-to-use API, and robust handling of HTTP/2. OkHttp provides better performance and more features than standard Java HTTP clients.
- **Shadow Plugin**: Used to create a fat JAR that includes all dependencies, making the CLI easy to distribute and run without requiring additional installation steps.
- **Logback**: Implemented as the logging implementation for its flexible configuration, high performance, and advanced features like automatic log rotation.

#### 2.3.3 backend/build.gradle

```gradle
plugins {
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}

dependencies {
    implementation project(':common')
    
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Database
    implementation 'org.postgresql:postgresql'
    implementation 'org.flywaydb:flyway-core'
    
    // Redis
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    
    // Git operations
    implementation "org.eclipse.jgit:org.eclipse.jgit:${rootProject.jgitVersion}"
    
    // gRPC for worker communication
    implementation "io.grpc:grpc-netty-shaded:${rootProject.grpcVersion}"
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.testcontainers:postgresql'
    testImplementation 'org.testcontainers:junit-jupiter'
}

bootJar {
    archiveBaseName.set('cicd-backend')
    mainClass = 'edu.neu.cs6510.sp25.t1.backend.BackendApplication'
}
```

**Technology Choices:**

- **Spring Boot**: Chosen for its comprehensive feature set, opinionated defaults, and excellent integration with other technologies. Spring Boot reduces boilerplate code and provides a production-ready application framework.
- **Spring Data JPA**: Selected for database access because it simplifies data access layer implementation with repository interfaces and reduces boilerplate code while providing powerful query capabilities.
- **Spring Validation**: Used for input validation due to its declarative approach and integration with REST controllers, enabling robust API input validation.
- **Spring Actuator**: Implemented for production-ready features like health checks, metrics, and monitoring endpoints that are essential for operational management.
- **PostgreSQL**: Chosen as the database for its reliability, advanced features (JSON support, complex queries), and excellent performance for relational data.
- **Flyway**: Selected for database migration management to ensure database schema consistency across environments and track schema changes over time.
- **Redis**: Used for caching and distributed locks because of its high performance, versatile data structures, and ability to improve application response times.
- **JGit**: Implemented for Git operations as a pure Java implementation that doesn't require external git installations, making deployment simpler.
- **TestContainers**: Chosen for integration testing to provide real, isolated instances of databases and other dependencies in tests, improving test reliability and realism.

#### 2.3.4 worker/build.gradle

```gradle
plugins {
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}

dependencies {
    implementation project(':common')
    
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Docker Java API
    implementation "com.github.docker-java:docker-java-core:${rootProject.dockerJavaVersion}"
    implementation "com.github.docker-java:docker-java-transport-httpclient5:${rootProject.dockerJavaVersion}"
    
    // gRPC for backend communication
    implementation "io.grpc:grpc-netty-shaded:${rootProject.grpcVersion}"
    
    // Git operations
    implementation "org.eclipse.jgit:org.eclipse.jgit:${rootProject.jgitVersion}"
    
    // Metrics
    implementation 'io.micrometer:micrometer-registry-prometheus'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.testcontainers:junit-jupiter:1.19.3'
}

bootJar {
    archiveBaseName.set('cicd-worker')
    mainClass = 'edu.neu.cs6510.sp25.t1.worker.WorkerApplication'
}
```

**Technology Choices:**

- **Docker Java API**: Selected for Docker container management because it provides comprehensive control over Docker operations directly from Java. This eliminates the need for shell commands and improves security and reliability.
- **Spring Boot (Core)**: Used for the Worker component, but with minimal web dependencies. The core Spring Boot functionality provides dependency injection, configuration management, and robust application lifecycle handling.
- **Micrometer with Prometheus**: Chosen for metrics collection because it provides a vendor-neutral metrics facade with dimensional metrics that work well with Prometheus. This enables comprehensive monitoring of worker performance and health.
- **gRPC Netty**: Implemented for network communication with high performance, leveraging the Netty non-blocking I/O framework for optimal throughput and concurrency.

## 3. Class Diagrams

### 3.1 Common Module Classes

```
+-------------------+       +-------------------+       +-------------------+
|   PipelineModel   |       |     StageModel    |       |     JobModel      |
+-------------------+       +-------------------+       +-------------------+
| - name: String    |<>-----| - name: String    |<>-----| - name: String    |
| - stages: List    |       | - jobs: List      |       | - stage: String   |
| - globals: Map    |       +-------------------+       | - image: String   |
+-------------------+                                   | - script: List    |
                                                        | - needs: List     |
                                                        | - allowFailure    |
                                                        +-------------------+
```

### 3.2 CLI Module Classes

```
+---------------+       +----------------+       +----------------+
|     Main      |       |  CommandLine   |       |  BaseCommand   |
+---------------+       +----------------+       +----------------+
| + main()      |------>| + parse()      |<------| + execute()    |
+---------------+       | + execute()    |       | + validate()   |
                        +----------------+       +----------------+
                                                        ^
                                                        |
                +-------------------------------------+------------+
                |                                     |            |
        +----------------+                +----------------+       +----------------+
        |  RunCommand    |                | ReportCommand  |       | CheckCommand   |
        +----------------+                +----------------+       +----------------+
        | + execute()    |                | + execute()    |       | + execute()    |
        +----------------+                +----------------+       +----------------+

+----------------+       +----------------+       +----------------+
|   ApiClient    |       | OutputFormatter|       |  ConfigManager |
+----------------+       +----------------+       +----------------+
| + sendRequest()|       | + format()     |       | + load()       |
| + getResponse()|       | + display()    |       | + save()       |
+----------------+       +----------------+       +----------------+
```

**Design Patterns:**

- **Command Pattern**: Used in the CLI module where each command is encapsulated as an object, allowing for modular command implementation and extension.
- **Template Method Pattern**: Applied in BaseCommand to define the skeleton of operations with specific steps deferred to subclasses.
- **Builder Pattern**: Utilized in the configuration classes to create complex objects step by step.
- **Factory Method Pattern**: Implemented for creating appropriate command instances based on user input.

### 3.3 Backend Module Classes

```
+----------------+       +----------------+       +----------------+
|    Controller  |       |    Service     |       |   Repository   |
+----------------+       +----------------+       +----------------+
| + handleRequest|------>| + processData()|------>| + findData()   |
+----------------+       +----------------+       | + saveData()   |
                                |                 +----------------+
                                |
                                v
+----------------+       +----------------+       +----------------+
| WorkerManager  |       |  JobScheduler  |       | PipelineService|
+----------------+       +----------------+       +----------------+
| + assignJob()  |<------| + schedule()   |<------| + execute()    |
| + trackWorker()|       | + prioritize() |       | + validate()   |
+----------------+       +----------------+       +----------------+
        |
        v
+----------------+
| WorkerClient   |
+----------------+
| + sendJob()    |
| + getStatus()  |
+----------------+
```

### 3.4 Worker Module Classes

```
+----------------+       +----------------+       +----------------+
| WorkerServer   |       | JobExecutor    |       | DockerManager  |
+----------------+       +----------------+       +----------------+
| + receiveJob() |------>| + execute()    |------>| + createContainer() |
| + reportStatus()|      | + handleError()|       | + runCommand()      |
+----------------+       +----------------+       | + cleanupContainer()|
                                |                 +----------------+
                                |
                                v
+----------------+       +----------------+       +----------------+
| ArtifactHandler|       | LogCollector   |       | BackendClient  |
+----------------+       +----------------+       +----------------+
| + collectArtifacts() | | + collectLogs()|       | + reportStatus()|
| + uploadArtifacts()  | | + streamLogs() |       | + getConfig()   |
+----------------+       +----------------+       +----------------+
```

## 4. Interface Definitions

### 4.1 CLI to Backend Interface (common/src/main/java/edu/neu/cs6510/sp25/t1/common/api)

```java
// Common API request/response models
package edu.neu.cs6510.sp25.t1.common.api;

public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;
    
    // Constructor, getters, and setters omitted for brevity
}

public class RunPipelineRequest {
    private String branch;
    private String commit;
    private Map<String, String> overrides;
    
    // Constructor, getters, and setters omitted for brevity
}

public class PipelineRunResponse {
    private String id;
    private String repositoryUrl;
    private String pipelineName;
    private int runNumber;
    private String status;
    private Instant startTime;
    private Instant endTime;
    
    // Constructor, getters, and setters omitted for brevity
}
```

### 4.2 Backend to Worker Interface (common/src/main/proto/worker.proto)

```protobuf
syntax = "proto3";

package edu.neu.cs6510.sp25.t1.worker;

option java_multiple_files = true;

service WorkerService {
  rpc ExecuteJob(ExecuteJobRequest) returns (stream JobStatusUpdate);
  rpc RegisterWorker(RegisterRequest) returns (RegisterResponse);
  rpc Heartbeat(HeartbeatRequest) returns (HeartbeatResponse);
  rpc CancelJob(CancelJobRequest) returns (CancelJobResponse);
}

message ExecuteJobRequest {
  string job_id = 1;
  string repository_url = 2;
  string commit_hash = 3;
  DockerConfig docker = 4;
  repeated string commands = 5;
  repeated ArtifactPath artifacts = 6;
  bool allow_failure = 7;
  map<string, string> environment = 8;
}

message JobStatusUpdate {
  string job_id = 1;
  JobStatus status = 2;
  string output_chunk = 3;
  repeated ArtifactResult artifacts = 4;
  int32 exit_code = 5;
  string error_message = 6;
}

enum JobStatus {
  PENDING = 0;
  RUNNING = 1;
  SUCCESS = 2;
  FAILED = 3;
  CANCELED = 4;
}

// Other message definitions omitted for brevity
```

## 5. Key Implementation Details

### 5.1 Common Models (Shared between Components)

```java
package edu.neu.cs6510.sp25.t1.common.model;

public class Pipeline {
    private String name;
    private List<Stage> stages;
    private Map<String, Object> globals;
    
    // Constructor, getters, and setters omitted for brevity
}

public class Stage {
    private String name;
    private List<Job> jobs;
    
    // Constructor, getters, and setters omitted for brevity
}

public class Job {
    private String name;
    private String stageName;
    private String image;
    private List<String> script;
    private List<String> needs;
    private boolean allowFailure;
    private Artifact artifacts;
    
    // Constructor, getters, and setters omitted for brevity
}

public class Artifact {
    private List<String> paths;
    
    // Constructor, getters, and setters omitted for brevity
}
```

### 5.2 CLI API Client

```java
package edu.neu.cs6510.sp25.t1.cli.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.neu.cs6510.sp25.t1.common.api.ApiResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BackendClient {
    
    private static final Logger logger = LoggerFactory.getLogger(BackendClient.class);
    
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public BackendClient(String baseUrl) {
        this.baseUrl = baseUrl;
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    }
    
    public <T> ApiResponse<T> get(String path, Class<T> responseType) throws IOException {
        Request request = new Request.Builder()
            .url(baseUrl + path)
            .get()
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            return processResponse(response, responseType);
        }
    }
    
    public <T> ApiResponse<T> post(String path, Object body, Class<T> responseType) throws IOException {
        String json = objectMapper.writeValueAsString(body);
        
        RequestBody requestBody = RequestBody.create(
            json, MediaType.parse("application/json"));
        
        Request request = new Request.Builder()
            .url(baseUrl + path)
            .post(requestBody)
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            return processResponse(response, responseType);
        }
    }
    
    private <T> ApiResponse<T> processResponse(Response response, Class<T> responseType) throws IOException {
        // Implementation omitted for brevity
        return null;
    }
}
```

### 5.3 Backend API Controller

```java
package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.common.api.PipelineRunRequest;
import edu.neu.cs6510.sp25.t1.common.api.PipelineRunResponse;
import edu.neu.cs6510.sp25.t1.backend.service.RunPipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
    
    private final RunPipelineService runPipelineService;
    
    @Autowired
    public PipelineController(RunPipelineService runPipelineService) {
        this.runPipelineService = runPipelineService;
    }
    
    @PostMapping("/{repoId}/{pipelineId}/run")
    public ResponseEntity<PipelineRunResponse> runPipeline(
            @PathVariable String repoId,
            @PathVariable String pipelineId,
            @RequestBody PipelineRunRequest request) {
        
        PipelineRunResponse response = runPipelineService.runPipeline(
            repoId, pipelineId, request.getBranch(), request.getCommit(), request.getOverrides());
        
        return ResponseEntity.ok(response);
    }
    
    // Additional endpoints omitted for brevity
}
```

### 5.4 Worker Job Executor
// to be continued 