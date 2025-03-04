plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common")) // Shared models and utilities

    // Spring Boot for Worker Service API
    implementation("org.springframework.boot:spring-boot-starter-web")

    // WebClient (for sending job status updates)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation ("com.github.docker-java:docker-java:3.2.13")

    // Docker Java API (for managing job execution in Docker)
    implementation("com.github.docker-java:docker-java-core:3.3.4")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.4")

    // Logging (SLF4J + Logback)
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // JSON Parsing (Jackson)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    // Metrics (Prometheus support)
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Unit Testing Dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")

    // Integration Testing (Testcontainers for Docker)
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:testcontainers:1.19.3")


    // Spring Boot Testing (Ensure JUnit 5 is used)
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine") 
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


tasks.test {
    useJUnitPlatform()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("edu.neu.cs6510.sp25.t1.WorkerApp") // Set your actual main class
}
