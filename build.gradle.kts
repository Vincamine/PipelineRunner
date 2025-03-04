plugins {
    java
    jacoco
    checkstyle
    pmd
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

        testImplementation("org.mockito:mockito-core:5.4.0")
        testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    jacoco {
        toolVersion = "0.8.10"
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    // Exclude test execution by folder (package-based)
                    exclude("**/config/**")          // Ignore all config classes
                    exclude("**/data/dto/**")        // Ignore all DTOs
                    exclude("**/data/entity/**")     // Ignore entities
                    exclude("**/enums/**")           // Ignore enums

                    // Exclude test execution by name pattern
                    exclude("**/*Config.*")          // Files ending in 'Config'
                    exclude("**/*DTO.*")             // Files ending in 'DTO'
                    exclude("**/*Entity.*")          // Files ending in 'Entity'
                    exclude("**/*Enum.*")            // Files ending in 'Enum'
                }
            })
        )
    }
//
//    tasks.jacocoTestCoverageVerification {
//        dependsOn(tasks.jacocoTestReport)
//        violationRules {
//            rule {
//                element = "CLASS"
//
//                // Exclude classes by package
//                excludes.addAll(
//                    listOf(
//                        "com.example.config.*",        // Ignore all config files
//                        "com.example.data.dto.*",      // Ignore all DTOs
//                        "com.example.data.entity.*",   // Ignore all Entities
//                        "com.example.enums.*"          // Ignore all Enums
//                    )
//                )
//
//                limit {
//                    counter = "LINE"
//                    value = "COVEREDRATIO"
//                    minimum = "0.7".toBigDecimal()
//                }
//                limit {
//                    counter = "BRANCH"
//                    value = "COVEREDRATIO"
//                    minimum = "0.7".toBigDecimal()
//                }
//            }
//        }
//    }
//
//    tasks.check {
//        dependsOn(tasks.test)
//        dependsOn(tasks.jacocoTestCoverageVerification) // Ensures coverage check runs on build, comment out for now
//    }

    checkstyle {
        toolVersion = "10.12.3"
        configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
        configProperties["checkstyle.suppressions.file"] = file("$rootDir/config/checkstyle/checkstyle-suppressions.xml").absolutePath
    }

    tasks.withType<Checkstyle> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

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

    tasks.javadoc {
        options.encoding = "UTF-8"
        isFailOnError = false
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

