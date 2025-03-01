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
    
        // Exclude Spring Boot entry points from JaCoCo coverage report
        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/backend/BackendApp.class",  // Ignore BackendApp.java (Spring Boot entry point)
                        "**/WorkerApp.class",   // Ignore Worker entry point
                        "**/CliApp.class",      // Ignore CLI entry point
                    )
                }
            })
        )
    }

    tasks.jacocoTestCoverageVerification {
        dependsOn(tasks.jacocoTestReport)
        violationRules {
            rule {
                element = "CLASS"
                excludes = listOf(
                    "edu.neu.cs6510.sp25.t1.backend.BackendApp",  // Ignore BackendApp
                    "edu.neu.cs6510.sp25.t1.WorkerApp",   // Ignore WorkerApp 
                    "edu.neu.cs6510.sp25.t1.CliApp",      // Ignore CLI App 
                )

                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.7".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.7".toBigDecimal()
                }
            }
        }
    }


    tasks.check {
        dependsOn(tasks.test)
        // dependsOn(tasks.jacocoTestCoverageVerification)
    }

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
