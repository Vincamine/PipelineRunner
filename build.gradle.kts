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
                    exclude(
                        // Ignore Spring Boot entry points
                        "**/backend/BackendApp.class",
                        "**/worker/WorkerApp.class",
                        "**/cli/CliApp.class",

                        // Ignore Enums
                        "**/common/model/PipelineState.class",

                        // Ignore Configuration Classes (Only for dependency injection)
                        "**/backend/config/CicdConfigProperties.class",
                        "**/backend/config/GitConfig.class",
                        "**/backend/config/ArtifactsConfig.class",
                        "**/backend/config/WorkerConfig.class",
                        "**/backend/config/JwtConfigProperties.class",
                        "**/common/config/RestTemplateConfig.class",

                        // Ignore Model Classes (JPA Entities without logic)
                        "**/backend/model/Job.class",
                        "**/backend/model/Stage.class",
                        "**/backend/model/Pipeline.class",

                        // Ignore DTO Classes (Data Transfer Objects without logic)
                        "**/backend/dto/JobDTO.class",
                        "**/backend/dto/PipelineDTO.class",
                        "**/backend/dto/PipelineExecutionSummary.class",
                        "**/backend/dto/PipelineStatusResponse.class",
                        "**/backend/dto/StageDTO.class",
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
                    // Ignore Spring Boot entry points
                    "edu.neu.cs6510.sp25.t1.backend.BackendApp",
                    "edu.neu.cs6510.sp25.t1.worker.WorkerApp",
                    "edu.neu.cs6510.sp25.t1.cli.CliApp",

                    // Ignore Enums
                    "edu.neu.cs6510.sp25.t1.common.model.PipelineState",

                    // Ignore Configuration Classes (Only for dependency injection)
                    "edu.neu.cs6510.sp25.t1.backend.config.CicdConfigProperties",
                    "edu.neu.cs6510.sp25.t1.backend.config.GitConfig",
                    "edu.neu.cs6510.sp25.t1.backend.config.ArtifactsConfig",
                    "edu.neu.cs6510.sp25.t1.backend.config.WorkerConfig",
                    "edu.neu.cs6510.sp25.t1.backend.config.JwtConfigProperties",
                    "edu.neu.cs6510.sp25.t1.common.config.RestTemplateConfig",

                    // Ignore Model Classes (JPA Entities without logic)
                    "edu.neu.cs6510.sp25.t1.backend.model.Job",
                    "edu.neu.cs6510.sp25.t1.backend.model.Stage",
                    "edu.neu.cs6510.sp25.t1.backend.model.Pipeline",

                    // Ignore DTO Classes (Data Transfer Objects without logic)
                    "edu.neu.cs6510.sp25.t1.backend.dto.JobDTO",
                    "edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO",
                    "edu.neu.cs6510.sp25.t1.backend.dto.PipelineExecutionSummary",
                    "edu.neu.cs6510.sp25.t1.backend.dto.PipelineStatusResponse",
                    "edu.neu.cs6510.sp25.t1.backend.dto.StageDTO"
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

//    tasks.check {
//        dependsOn(tasks.test)
////        dependsOn(tasks.jacocoTestCoverageVerification) // Ensures coverage check runs on build, comment out for now
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

