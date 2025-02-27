plugins {
    application
}

dependencies {
    implementation(project(":common"))

    // Docker support for running jobs in containers
    implementation("com.github.docker-java:docker-java:3.3.4")
}

application {
    mainClass.set("edu.neu.cs6510.sp25.t1.WorkerApplication")
}
