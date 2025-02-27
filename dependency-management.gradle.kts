val javaVersion = JavaLanguageVersion.of(21)

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")

    repositories {
        mavenCentral()
    }

    dependencies {
        // Common Dependencies
        implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
        implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
        implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")
        implementation("org.yaml:snakeyaml:2.0")
        implementation("com.google.guava:guava:32.1.2-jre")

        // Unit Testing
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
        testImplementation("org.mockito:mockito-core:5.4.0")
        testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
    }

    java {
        toolchain {
            languageVersion.set(javaVersion)
        }
    }

    // Enforce 70% test coverage
    tasks.jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = "0.7".toBigDecimal()
                }
            }
        }
    }

    tasks.check {
        dependsOn(tasks.jacocoTestCoverageVerification)
    }

    checkstyle {
        toolVersion = "10.12.3"
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    }

    pmd {
        toolVersion = "6.55.0"
        rulesMinimumPriority.set(5)
    }
}
