plugins {
    java
    application
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0")

    // Google Guava
    implementation("com.google.guava:guava:32.1.2-jre")

    // YAML parser
    implementation("org.yaml:snakeyaml:2.0")

    // gRPC Dependencies
    implementation("io.grpc:grpc-protobuf:1.60.0")
    implementation("io.grpc:grpc-stub:1.60.0")
    implementation("io.grpc:grpc-netty-shaded:1.60.0")
    
    // For javax.annotation.Generated
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Protobuf
    implementation("com.google.protobuf:protobuf-java:3.25.1")

    // Spring Boot Dependencies
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-context")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/java", "build/generated/source/proto/main/grpc")
        }
    }
}