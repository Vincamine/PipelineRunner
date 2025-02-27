plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1" // Fat JAR packaging
}

dependencies {
    implementation(project(":common"))

    // HTTP Client
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2")

    // Docker support
    implementation("com.github.docker-java:docker-java:3.3.4")
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.BackendApplication")
}

// Build a Fat JAR
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("ci-backend")
    archiveClassifier.set("")
    archiveVersion.set("")
}
