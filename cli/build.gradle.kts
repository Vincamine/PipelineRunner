plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    // Picocli for CLI
    implementation("info.picocli:picocli:4.7.6")
    annotationProcessor("info.picocli:picocli-codegen:4.7.6")

    // Use JUnit BOM (Bill of Materials) to ensure version compatibility
    testImplementation(platform("org.junit:junit-bom:5.12.0"))

    // Then specify JUnit dependencies without versions - they'll use versions from the BOM
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.platform:junit-platform-launcher")

    // Mockito dependencies
    testImplementation("org.mockito:mockito-core:5.16.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.16.0")

    // ShadowJar dependencies
    implementation(project(":common"))
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.cli.CliApp")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("ci-tool")
    archiveClassifier.set("")
    archiveVersion.set("")

    manifest {
        attributes("Main-Class" to "edu.neu.cs6510.sp25.t1.cli.CliApp")
    }
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

