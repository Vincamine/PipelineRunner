plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
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


    // Use JUnit BOM (Bill of Materials) to ensure version compatibility
    testImplementation(platform("org.junit:junit-bom:5.12.0"))

    // Then specify JUnit dependencies without versions - they'll use versions from the BOM
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.platform:junit-platform-launcher")

    // ✅ Mockito (For Unit Testing & Mocks)
    testImplementation("org.mockito:mockito-core:5.16.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.16.0")


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
    mainClass.set("edu.neu.cs6510.sp25.t1.worker.WorkerApp")
}
