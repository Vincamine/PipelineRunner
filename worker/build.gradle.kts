plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenCentral()
}

dependencies {
    // ✅ Spring Boot (Minimal)
    implementation("org.springframework.boot:spring-boot-starter")

    // ✅ Required for @SpringBootApplication, @ComponentScan
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    // ✅ Docker Java API (Managing Job Execution in Docker)
    implementation("com.github.docker-java:docker-java-core:3.3.4")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.4")

    // ✅ Unit Testing (JUnit & Mockito)
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    testImplementation("org.mockito:mockito-core:5.16.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.16.0")

    // ✅ Spring Boot Test (For Integration Tests)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("edu.neu.cs6510.sp25.t1.worker.WorkerApp") // Ensure your main class is correct
}
