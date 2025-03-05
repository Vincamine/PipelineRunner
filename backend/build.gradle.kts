plugins {
    java
    application
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common")) // Shared code

    // Spring Boot Core Dependencies
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.4.3")

    // OpenAPI (Swagger) for API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    // Database and JPA (PostgreSQL + Hibernate)
    implementation("org.hibernate.orm:hibernate-core:6.4.2.Final")
    runtimeOnly("org.postgresql:postgresql:42.7.5")

    // Jakarta Persistence (JPA Annotations)
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.17")

    // ✅ JUnit 5 (Testing)
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")

    // ✅ Mockito (For Unit Testing & Mocks)
    testImplementation("org.mockito:mockito-core:5.16.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.16.0")

    // ✅ Spring Boot Test (For MockMvc & Integration Tests)
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.backend.BackendApp")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
