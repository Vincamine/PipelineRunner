plugins {
    java
    application
    id("com.github.ben-manes.versions") version "0.52.0"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "11.3.4"
}

repositories {
    mavenCentral()
}

dependencies {
    // Use the common module for shared code
    implementation(project(":common"))

    // Spring Boot dependencies
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.3"))
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.4.3") // Optional for monitoring

    // Database
    implementation("org.hibernate.orm:hibernate-core:6.4.2.Final")
    runtimeOnly("org.postgresql:postgresql:42.7.5")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.17")

    // JSON Processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")

    // OpenAPI Docs (Swagger)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    // Remove XML Binding (Only add if needed)
    // implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    // implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    testImplementation("org.mockito:mockito-core:5.16.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "edu.neu.cs6510.sp25.t1.BackendApp"
    }
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.backend.BackendApp")
}
