plugins {
    java
    application
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.flywaydb.flyway") version "9.22.3"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":worker"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.2"))
    
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.2")

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")

    // Logging with SLF4J and Logback
    implementation("org.slf4j:slf4j-api:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // **Spring Boot Testing Dependencies**
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.2")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux:3.2.2") // For MockMvc testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")

    // **Spring Web Test Support**
    testImplementation("org.springframework.boot:spring-boot-starter-web:3.2.2")

    // **JPA (Jakarta Persistence API)**
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.2")
    implementation("org.hibernate.orm:hibernate-core:6.4.0.Final")
    runtimeOnly("org.postgresql:postgresql:42.6.0")

    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")

    // **Spring Data Redis**
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.2.2")
    implementation("io.lettuce:lettuce-core:6.5.4.RELEASE")

    // **Jakarta Persistence API (JPA Annotations)**
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "edu.neu.cs6510.sp25.t1.BackendApp"
    }
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.backend.BackendApp")
}
