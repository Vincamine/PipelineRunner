plugins {
    application
}

dependencies {
    implementation(project(":common"))

    // Command-line parser (Picocli)
    implementation("info.picocli:picocli:4.7.4")
    annotationProcessor("info.picocli:picocli-codegen:4.7.4")
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.App")
}

// Ensure Picocli annotation processing
tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations["annotationProcessor"]
}
