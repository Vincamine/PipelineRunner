// package edu.neu.cs6510.sp25.t1.common;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.net.HttpURLConnection;
// import java.net.URI;
// import java.io.File;
// import java.util.stream.Collectors;

// class EndToEndIntegrationTest {

//     @Test
//     void testEndToEndPipelineExecution() throws Exception {
//         // Get absolute paths dynamically
//         File projectRoot = new File(System.getProperty("user.dir")).getAbsoluteFile().getParentFile();
//         File cliJar = new File(projectRoot, "cli/build/libs/ci-tool.jar");
//         File pipelineFile = new File(projectRoot, ".pipelines/pipeline.yaml");

//         assertTrue(cliJar.exists(), "CLI jar not found at " + cliJar.getAbsolutePath());
//         assertTrue(pipelineFile.exists(), "Pipeline YAML not found at " + pipelineFile.getAbsolutePath());

//         // Step 1: Start services using Docker Compose
//         ProcessBuilder dockerCompose = new ProcessBuilder("docker-compose", "up", "-d");
//         dockerCompose.inheritIO();
//         Process process = dockerCompose.start();
//         int exitCode = process.waitFor();
//         assertTrue(exitCode == 0, "Failed to start Docker services!");

//         // Wait for backend to start
//         Thread.sleep(10000);

//         // Step 2: Run CLI command to trigger pipeline
//         ProcessBuilder cliCommand = new ProcessBuilder("java", "-jar", cliJar.getAbsolutePath(),
//                 "run", "--pipeline", pipelineFile.getAbsolutePath());

//         cliCommand.directory(new File(projectRoot, "cli")); // Set working dir
//         cliCommand.inheritIO();
//         Process cliProcess = cliCommand.start();
//         int cliExitCode = cliProcess.waitFor();
//         assertTrue(cliExitCode == 0, "CLI command failed to execute!");

//         // Step 3: Check backend for execution logs (Retry Mechanism)
//         URI uri = new URI("http://localhost:8080/api/v1/pipelines/123/executions");
//         boolean backendAvailable = false;
//         int maxRetries = 10;

//         for (int i = 0; i < maxRetries; i++) {
//             try {
//                 HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
//                 connection.setRequestMethod("GET");
//                 connection.setConnectTimeout(5000);
//                 connection.setReadTimeout(5000);
//                 int responseCode = connection.getResponseCode();

//                 if (responseCode == 200) {
//                     backendAvailable = true;
//                     break;
//                 }
//                 System.out.println("Backend not available yet (HTTP " + responseCode + ")... retrying in 3 seconds.");
//                 Thread.sleep(3000);
//             } catch (Exception e) {
//                 System.out.println("Exception while checking backend: " + e.getMessage());
//                 Thread.sleep(3000);
//             }
//         }
//         assertTrue(backendAvailable, "Backend did not become available within timeout.");

//         // Step 4: Fetch logs via CLI
//         ProcessBuilder reportCommand = new ProcessBuilder("java", "-jar", cliJar.getAbsolutePath(),
//                 "report", "--pipeline", "my-pipeline");
//         reportCommand.directory(new File(projectRoot, "cli"));

//         Process reportProcess = reportCommand.start();
//         int reportExitCode = reportProcess.waitFor();
//         assertTrue(reportExitCode == 0, "CLI report command failed!");

//         // Read full CLI output instead of a single line
//         BufferedReader reportReader = new BufferedReader(new InputStreamReader(reportProcess.getInputStream()));
//         String reportOutput = reportReader.lines().collect(Collectors.joining("\n"));
//         reportReader.close();
//         assertTrue(reportOutput.contains("Execution History"), "Pipeline execution logs not found!");
//     }
// }
