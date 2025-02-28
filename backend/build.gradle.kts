plugins {
    java
    application
}

dependencies {
    implementation(project(":common"))

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")

    // Google Guava (Utility library)
    implementation("com.google.guava:guava:32.1.2-jre")

    // YAML parser
    implementation("org.yaml:snakeyaml:2.0")
}
