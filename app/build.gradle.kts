plugins {
    application
    java
    jacoco
    checkstyle
    pmd
    id("com.github.johnrengelman.shadow") version "8.1.1" // Fat JAR plugin
}

repositories {
    mavenCentral()
}

dependencies {
    // FasterXML Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")

    // HTTP Client (Java 11+)
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2")

    // Command Line tool (Picocli)
    implementation("info.picocli:picocli:4.7.4")
    annotationProcessor("info.picocli:picocli-codegen:4.7.4")

    // JUnit 5 for unit testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

    // Mockito for unit testing
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")

    // Google Guava (Utility library)
    implementation("com.google.guava:guava:32.1.2-jre")

    // YAML file parser
    implementation("org.yaml:snakeyaml:2.0")
}

sourceSets {
    main {
        java.srcDirs("src/main/java")
    }
    test {
        java.srcDirs("src/test/java")
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

// Apply Java toolchain (Java 21)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Ensure the application knows the correct main class
application {
    mainClass.set("edu.neu.cs6510.sp25.t1.App")
}

// Configure Fat JAR for including dependencies
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "edu.neu.cs6510.sp25.t1.App"
    }
}

// Build a Fat JAR that includes dependencies
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("ci-tool")
    archiveClassifier.set("")
    archiveVersion.set("")
}

// Ensure Picocli annotation processing works correctly
tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations["annotationProcessor"]
}

// Run tests with JUnit 5
tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// Code Coverage Configuration
jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// Enforce 70% test coverage
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal() // 70% test coverage requirement
            }
        }
    }
}

// Checkstyle Configuration (Updated path to app/config)
checkstyle {
    toolVersion = "10.12.3"
    configFile = file("${rootDir}/app/config/checkstyle/checkstyle.xml")
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// PMD Configuration
pmd {
    toolVersion = "6.55.0"
    rulesMinimumPriority.set(5)
}

tasks.withType<Pmd> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// JavaDoc Generation
tasks.javadoc {
    options.encoding = "UTF-8"
    isFailOnError = false
}

// Enable compiler warnings
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}
