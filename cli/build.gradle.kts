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
    implementation("info.picocli:picocli:4.7.4")
    annotationProcessor("info.picocli:picocli-codegen:4.7.4")

    // JUnit and Mockito for testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    testImplementation("org.mockito:mockito-core:5.8.0")

    // ShadowJar dependencies
    implementation(project(":common"))
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.cli.CliApp")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("ci-tool")
    archiveClassifier.set("")
    archiveVersion.set("")

    manifest {
        attributes("Main-Class" to "edu.neu.cs6510.sp25.t1.cli.CliApp")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
