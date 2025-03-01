plugins {
    java
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // No Spring Boot here!

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")

    // Snakeyaml
    implementation("org.yaml:snakeyaml:2.0")

    // annotation
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}