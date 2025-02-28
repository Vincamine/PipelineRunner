plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.worker.WorkerApp")
}
