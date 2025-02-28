plugins {
    java
    application
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

dependencies {
    implementation(project(":common"))

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")

    // Google Guava (Utility library)
    implementation("com.google.guava:guava:32.1.2-jre")

    // YAML parser
    implementation("org.yaml:snakeyaml:2.0")

    // gRPC dependencies
    implementation("io.grpc:grpc-netty-shaded:1.60.0")
    implementation("io.grpc:grpc-protobuf:1.60.0")
    implementation("io.grpc:grpc-stub:1.60.0")

    // Logging with SLF4J and Logback
    implementation("org.slf4j:slf4j-api:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter-context:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.2")

    // Spring annotations and dependency injection
    implementation("org.springframework:spring-context:6.1.2")

    // Testing dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
 
}
