// package edu.neu.cs6510.sp25.t1.common;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.net.HttpURLConnection;
// import java.net.URI;
// import java.nio.file.Paths;

// class EndToEndIntegrationTest {

//     @Test
//     void testEndToEndPipelineExecution() throws Exception {
//         // Step 1: Start services using ProcessBuilder
//         ProcessBuilder dockerCompose = new ProcessBuilder("docker-compose", "up", "-d");
//         dockerCompose.inheritIO();
//         Process process = dockerCompose.start();
//         process.waitFor();

//         // Wait to ensure backend is running
//         Thread.sleep(10000);

//         // Step 2: Run CLI command to trigger pipeline
//         ProcessBuilder cliCommand = new ProcessBuilder("java", "-jar", "../cli/build/libs/cli.jar", "run", "--pipeline",
//                 "pipeline.yaml");
//         cliCommand.directory(Paths.get(System.getProperty("user.dir"), "../cli").toFile());
//         cliCommand.inheritIO();
//         Process cliProcess = cliCommand.start();
//         cliProcess.waitFor();

//         // Step 3: Check backend for execution logs (Retry Mechanism)
//         URI uri = new URI("http://localhost:8080/api/v1/pipelines/123/executions");
//         boolean backendAvailable = false;
//         int maxRetries = 5;

//         for (int i = 0; i < maxRetries; i++) {
//             try {
//                 HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
//                 connection.setRequestMethod("GET");
//                 connection.setConnectTimeout(5000);
//                 connection.setReadTimeout(5000);

//                 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                 String response = in.readLine();
//                 in.close();

//                 if (response != null && response.contains("Success")) {
//                     backendAvailable = true;
//                     break;
//                 }
//             } catch (Exception e) {
//                 System.out.println("Backend not available yet... retrying in 3 seconds.");
//                 Thread.sleep(3000);
//             }
//         }

//         assertTrue(backendAvailable, "Backend did not become available within timeout.");

//         // Step 4: Fetch logs via CLI
//         ProcessBuilder reportCommand = new ProcessBuilder("java", "-jar", "../cli/build/libs/cli.jar", "report",
//                 "--pipeline", "my-pipeline");
//         reportCommand.directory(Paths.get(System.getProperty("user.dir"), "../cli").toFile());
//         reportCommand.inheritIO();
//         Process reportProcess = reportCommand.start();
//         reportProcess.waitFor();

//         BufferedReader reportReader = new BufferedReader(new InputStreamReader(reportProcess.getInputStream()));
//         String reportOutput = reportReader.readLine();
//         reportReader.close();

//         assertTrue(reportOutput != null && reportOutput.contains("Execution History:"));
//     }

// }
