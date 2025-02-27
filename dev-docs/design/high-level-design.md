# 25Spring CS6510 Team 1 - CI/CD System
- **Title:** High Level Architecture design file
- **Date:** Feb 27, 2025
- **Author:** Yiwen Wang, Mingtianfang Li
- **Version:** 1.1

**Revision History**
|Date|Version|Description|Author|
|:----:|:----:|:----:|:----:|
|Jan 31, 2025|1.0|Initial release| Yiwen Wang, Mingtianfang Li|
|Feb 27, 2025|1.1|Structure change to mono repo| Yiwen Wang|

# High Level Design Document: Custom CI/CD System (Monorepo)

## 1. System Overview

We are designing a custom CI/CD system with a distributed architecture that can run both on company data centers and locally on developer machines. The system consists of three main components that work together to provide a seamless experience across environments.

### 1.1 Core Components

1. **CLI Application** - Command-line interface for user interaction
2. **Backend Service** - Central orchestration and management service
3. **Worker Service** - Pipeline execution engine

### 1.2 System Architecture

```
┌──────────┐         ┌─────────────┐         ┌──────────┐
│    CLI   │ ◄─────► │   Backend   │ ◄─────► │  Worker  │
└──────────┘         └─────────────┘         └──────────┘
                           ▲                       ▲
                           │                       │
                           ▼                       ▼
                     ┌─────────────┐        ┌──────────────┐
                     │  Database   │        │Docker Engine │
                     └─────────────┘        └──────────────┘
```

### 1.3 Monorepo Structure

We will use a single repository with a multi-module Gradle project structure to manage all three components:

```
cicd-system/
├── cli/             # CLI Application module
├── backend/         # Backend Service module
├── worker/          # Worker Service module
├── common/          # Shared code module
├── build.gradle     # Root build file
├── settings.gradle  # Multi-module configuration
└── README.md        # Project documentation
```

## 2. Component Details

### 2.1 CLI Application (Module: cli)

The CLI will be built using Java with Gradle as the build system, providing a cross-platform solution.

#### Key Features:

- Command parsing and validation
- API client for communicating with backend
- Output formatting and display
- Local configuration management

#### Design Considerations:

- Lightweight design with minimal dependencies
- Offline capability for certain commands
- Version compatibility checks with backend
- Secure credential handling

#### Technology Choices:

- **Java 21**: Chosen for modern language features including virtual threads and pattern matching, as well as long-term support and cross-platform compatibility. Java 21 also provides enhanced performance compared to earlier versions.
- **Picocli**: Selected for command-line argument parsing because it offers annotation-based command definition, built-in help generation, and type conversion. It's lightweight and specifically designed for CLI applications.
- **OkHttp**: Used for API communication with the backend due to its efficient connection pooling, simple API, and robust error handling. It provides better performance than the standard HttpURLConnection.
- **Jackson**: Chosen for JSON handling because of its flexibility, performance, and extensive datatype support. It integrates well with both the CLI and Spring Boot backends.

### 2.2 Backend Service (Module: backend)

The Backend Service will manage the overall CI/CD system and orchestrate pipeline executions.

#### Key Features:

- RESTful API for CLI and potential web interfaces
- Repository management and configuration
- Pipeline scheduling and orchestration
- Storage and persistence of pipeline configurations and results
- Worker management and job distribution
- Authentication and authorization

#### Design Considerations:

- Stateless design for horizontal scalability
- API versioning strategy
- Database schema design for multi-repository support
- Caching strategies for performance
- Security and access control

#### Technology Choices:

- **Java 21**: Leveraging virtual threads for improved scalability, which is particularly important for the backend that needs to handle many concurrent connections and operations.
- **Spring Boot 3**: Chosen as the web service framework for its comprehensive feature set, including dependency injection, robust configuration system, and excellent integration with other technologies. Spring Boot 3 is optimized for Java 21 and provides native compilation options.
- **JPA/Hibernate**: Selected for database access because it offers object-relational mapping, reduces boilerplate code, and provides database vendor independence. Hibernate's caching capabilities also improve performance.
- **PostgreSQL**: Chosen for persistent storage due to its reliability, ACID compliance, advanced features (JSON support, complex queries), and ability to handle large datasets. PostgreSQL also offers excellent data integrity and transaction support.
- **Redis**: Used for caching and distributed locks because of its high performance, low latency, and support for complex data structures. Redis significantly improves response times for frequently accessed data.
- **JGit**: Implemented for Git operations because it's a pure Java implementation that doesn't require external dependencies. It provides comprehensive Git functionality and integrates seamlessly with Java applications.
- **Flyway**: Selected for database migrations to manage schema evolution in a versioned, repeatable way. It ensures consistent database structures across environments.

### 2.3 Worker Service (Module: worker)

The Worker Service will execute pipeline jobs within containers.

#### Key Features:

- Job execution in Docker containers
- Artifact collection and uploading
- Log streaming and capture
- Resource monitoring and management
- Health checking and self-healing

#### Design Considerations:

- Isolation between job executions
- Resource allocation and limits
- Failure recovery strategies
- Efficient artifact handling
- Secure secrets management

#### Technology Choices:

- **Java 21**: Chosen for efficient concurrent execution with virtual threads, which allows the worker to handle multiple job executions simultaneously with minimal resource overhead.
- **Docker Java API**: Selected for container management because it provides comprehensive control over Docker operations directly from Java. This eliminates the need for shell commands and improves security and reliability.
- **gRPC**: Implemented for efficient backend-worker communication due to its high performance, binary protocol, and strong typing. gRPC significantly reduces network overhead compared to REST and supports bidirectional streaming for real-time updates.
- **Prometheus**: Used for metrics collection because it provides a pull-based model that works well in dynamic environments. It offers robust monitoring capabilities with minimal overhead.
- **Spring Boot (Core)**: Chosen for the Worker as well, but with minimal web dependencies. This provides consistent configuration management, dependency injection, and application lifecycle across components.

### 2.4 Common Module (Module: common)

The Common module will contain shared code and models used by multiple components.

#### Key Features:

- Shared data models
- Common utilities
- Protocol definitions
- Configuration models

#### Design Considerations:

- Minimize dependencies to avoid bloat
- Clear API boundaries
- Versioning compatibility
- Proper encapsulation

#### Technology Choices:

- **Protocol Buffers**: Selected for defining the communication protocol between Backend and Worker due to its language-neutral, platform-neutral, extensible mechanism for serializing structured data. It provides strong typing and efficient serialization/deserialization.
- **Jackson Annotations**: Used for JSON model annotations to ensure consistent serialization behavior across components.
- **SLF4J**: Implemented as a logging facade to allow consistent logging patterns while enabling different logging implementations in each component.
- **SnakeYAML**: Chosen for YAML parsing of pipeline configurations because of its comprehensive YAML support and good performance characteristics.

## 3. Communication Protocols

### 3.1 CLI to Backend Communication

- **RESTful HTTP/HTTPS API**: Chosen for CLI-to-Backend communication because it's widely supported, easy to debug, and works well through firewalls and proxies. REST also provides clear resource semantics and straightforward error handling.
- **Authentication**: API tokens or OAuth2 selected for security because they provide stateless authentication that scales well and follows industry best practices.
- **JSON payload format**: Used for its human-readability, widespread support, and efficient parsing in both Java and JavaScript environments.
- **Versioned API endpoints** (e.g., `/api/v1/...`): Implemented to ensure backward compatibility as the API evolves.

### 3.2 Backend to Worker Communication

- **gRPC**: Selected for Backend-to-Worker communication because its high performance and bidirectional streaming capabilities are ideal for long-running job execution and real-time status updates.
- **Bidirectional streaming**: Enables real-time updates during job execution without polling.
- **TLS encryption**: Ensures secure communication between components, protecting sensitive data in transit.
- **Worker registration and heartbeat protocol**: Implemented to ensure system resilience, allowing dynamic worker discovery and health monitoring.

## 4. Data Flow

### 4.1 Pipeline Execution Flow

1. User invokes CLI with `run` command
2. CLI validates arguments and sends request to Backend API
3. Backend validates request and retrieves repository information
4. Backend schedules jobs and assigns them to available Workers
5. Workers execute jobs in containers and stream results back
6. Backend collects results and updates pipeline status
7. CLI polls or receives webhook notification of completion
8. CLI displays results to the user

### 4.2 Reporting Flow

1. User invokes CLI with `report` command
2. CLI sends report request to Backend API
3. Backend retrieves data from database
4. Backend formats and returns report data
5. CLI displays formatted report to the user

## 5. Repository Management

### 5.1 Multi-Repository Support

- Central registry in Backend for tracking all repositories
- Repository grouping for bulk operations
- Cross-repository dependency tracking
- Repository-specific configuration settings

### 5.2 Repository Operations

- Cloning and updating repositories
- Branch and commit management
- Pipeline configuration discovery
- Artifact storage organization

## 6. Scalability Considerations

### 6.1 Backend Scalability

- **Stateless design**: Enables horizontal scaling by allowing any backend instance to handle any request without session state.
- **Virtual threads**: Java 21's virtual threads allow for handling thousands of concurrent connections with minimal overhead, significantly improving scalability compared to traditional thread-per-request models.
- **Database connection pooling**: Implemented using HikariCP (included with Spring Boot) to efficiently manage database connections and prevent connection exhaustion under load.
- **Caching**: Redis caching reduces database load for frequently accessed data, improving response times and reducing system load.
- **Load balancing**: Designed to work behind standard load balancers (e.g., Nginx, HAProxy, or cloud load balancers) for distributing traffic across multiple backend instances.

### 6.2 Worker Scalability

- **Dynamic worker pool sizing**: Workers can be added or removed without system reconfiguration, allowing the system to scale based on demand.
- **Resource-based job allocation**: Jobs are assigned to workers based on available resources, ensuring efficient utilization.
- **Worker specialization**: Workers can be tagged with capabilities, allowing specialized workers for specific job types (e.g., high-memory jobs, GPU-accelerated jobs).
- **Containerized deployment**: Workers are designed for containerized deployment in Kubernetes or similar orchestration systems, facilitating scaling and management.

## 7. Security Considerations

### 7.1 Authentication and Authorization

- **API token-based authentication**: Provides stateless, scalable authentication for API access.
- **Role-based access control**: Enables fine-grained permission management based on user roles.
- **Resource-level permissions**: Allows restricting access to specific repositories or pipelines.
- **Audit logging**: Records all security-relevant events for compliance and troubleshooting.

### 7.2 Secure Execution

- **Isolated execution environments**: Jobs run in separate Docker containers to prevent interference and enhance security.
- **Secrets management**: Sensitive data (API tokens, passwords) are securely managed and injected into jobs as needed.
- **Network isolation**: Containers can be configured with limited network access to prevent unauthorized connections.
- **Artifact integrity verification**: Ensures artifacts haven't been tampered with during storage or transmission.

## 8. Monorepo Development Workflow

### 8.1 Module Independence

- Each module (CLI, Backend, Worker) has its own build configuration
- Modules can be built and tested independently
- Shared code is managed through the common module
- Changes to one module don't require rebuilding all modules

### 8.2 Continuous Integration

- CI pipeline builds and tests affected modules only
- Parallel building and testing of modules
- Integration tests verify cross-module compatibility
- Version management is simplified with single repository

### 8.3 Dependency Management

- Direct dependencies between modules are explicitly declared
- Common dependencies are managed at the root level
- Version conflicts are resolved centrally
- Bill of Materials (BOM) approach for dependency versioning

## 9. Development Roadmap

### Phase 1: Core Infrastructure

- Basic CLI, Backend, and Worker implementation
- Local pipeline execution support
- Simple reporting capabilities

### Phase 2: Enhanced Features

- Repository management implementation
- Repository grouping
- Cross-repository operations
- Enhanced reporting

### Phase 3: Enterprise Features

- Role-based access control
- Advanced dependency management
- Performance optimizations
- High availability configuration

## 10. Conclusion

This monorepo-based architecture with CLI, Backend, and Worker components provides a flexible, scalable CI/CD system that meets all the client requirements. By keeping all components in a single repository with a multi-module structure, we achieve better coordination and consistency while still maintaining separation of concerns. The modular design allows us to leverage Java 21's modern features like virtual threads to ensure efficient resource utilization and high concurrency, especially important for the Backend and Worker components.

