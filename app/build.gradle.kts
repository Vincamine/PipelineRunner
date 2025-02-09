plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    java
    jacoco
    checkstyle
    pmd
}

repositories {
    // Use Maven Central for resolving dependencies.
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

    // JUnit 5 API & Engine (for unit testing)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // JUnit Platform Launcher (Fixes JUnit5TestLoader not found issue)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

    // Mockito for unit testing
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")

    // Google Guava (Utility library)
    implementation("com.google.guava:guava:32.1.2-jre")
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

// Apply a specific Java toolchain (Java 21)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("edu.neu.cs6510.sp25.t1.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for running tests.
    useJUnitPlatform()
}

// Force JUnit 5 versions to prevent conflicts
configurations.all {
    resolutionStrategy {
        force("org.junit.platform:junit-platform-commons:1.10.0")
        force("org.junit.platform:junit-platform-launcher:1.10.0")
        force("org.junit.jupiter:junit-jupiter-api:5.10.0")
        force("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    }
}

// Jacoco (Code Coverage)
jacoco {
    toolVersion = "0.8.10"
}

tasks.test {
    workingDir = rootProject.projectDir
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// Checkstyle Configuration
checkstyle {
    toolVersion = "10.12.3"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
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

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "edu.neu.cs6510.sp25.t1.App"
    }
}

tasks.withType<Pmd> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

// JavaDoc generation
tasks.javadoc {
    options.encoding = "UTF-8"
    isFailOnError = false
}
