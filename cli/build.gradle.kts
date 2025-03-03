plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))


    // OkHttp (For BackendClient)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // Jackson (For JSON processing)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Picocli for CLI
    implementation("info.picocli:picocli:4.7.4")
    annotationProcessor("info.picocli:picocli-codegen:4.7.4")

    // Apache HTTP Client (Optional, if needed)
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2")

    // JUnit and Mockito for testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
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

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations["annotationProcessor"]
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
