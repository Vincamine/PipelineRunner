plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":common"))
    
    // Picocli for CLI
    implementation("info.picocli:picocli:4.7.4")
    annotationProcessor("info.picocli:picocli-codegen:4.7.4")

    // Apache HTTP Client
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2")

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.cli.App")
}

tasks.named<Jar>("shadowJar") {
    archiveBaseName.set("ci-tool")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations["annotationProcessor"]
}
