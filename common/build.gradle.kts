plugins {
    java
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Dependencies (Required for RestTemplate)
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.2")

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")

    // Snakeyaml
    implementation("org.yaml:snakeyaml:2.0")

    // Annotations (Java EE)
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.0")
}
