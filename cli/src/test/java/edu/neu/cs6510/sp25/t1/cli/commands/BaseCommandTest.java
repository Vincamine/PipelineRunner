//package edu.neu.cs6510.sp25.t1.cli.commands;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
///**
// * Unit tests for BaseCommand class.
// */
//class BaseCommandTest {
//
//  private BaseCommand baseCommand;
//  private final ObjectMapper objectMapper = new ObjectMapper();
//  private final YAMLMapper yamlMapper = new YAMLMapper();
//
//  @BeforeEach
//  void setUp() {
//    baseCommand = new BaseCommand() {
//      @Override
//      public Integer call() {
//        return 0; // Dummy implementation for testing
//      }
//    };
//  }
//
//  @Test
//  void testFormatOutputPlainText() {
//    baseCommand.setOutputFormat("plaintext");
//    String result = baseCommand.formatOutput("Test Output");
//    assertEquals("Test Output", result);
//  }
//
//  @Test
//  void testFormatOutputJson() throws Exception {
//    baseCommand.setOutputFormat("json");
//
//    // Convert the map to a JSON string before passing
//    String jsonInput = objectMapper.writeValueAsString(Map.of("key", "value"));
//    String result = baseCommand.formatOutput(jsonInput);
//
//    // Normalize JSON output for assertion
//    String expectedJson = objectMapper.writerWithDefaultPrettyPrinter()
//            .writeValueAsString(objectMapper.readTree(jsonInput));
//
//    assertEquals(expectedJson, result);
//  }
//
//  @Test
//  void testFormatOutputYaml() throws Exception {
//    baseCommand.setOutputFormat("yaml");
//
//    // Convert the map to a JSON string before passing
//    String jsonInput = objectMapper.writeValueAsString(Map.of("key", "value"));
//    String yamlOutput = baseCommand.formatOutput(jsonInput).trim(); // Trim to remove unwanted spaces
//
//    // Normalize expected YAML (trim leading "---" for comparison)
//    String expectedYaml = yamlMapper.writeValueAsString(yamlMapper.readTree(jsonInput)).trim();
//    if (expectedYaml.startsWith("---")) {
//      expectedYaml = expectedYaml.substring(3).trim(); // Remove "---"
//    }
//
//    assertEquals(expectedYaml, yamlOutput);
//  }
//
//  @Test
//  void testFormatOutputNullResponse() {
//    String result = baseCommand.formatOutput(null);
//    assertEquals("Error: No response received.", result);
//  }
//
//  @Test
//  void testSettersAndGetters() {
//    baseCommand.setConfigFile("config.yaml");
//    baseCommand.setRepo("https://github.com/example/repo");
//    baseCommand.setBranch("dev");
//    baseCommand.setCommit("abcdef");
//    baseCommand.setOutputFormat("json");
//
//    assertEquals("config.yaml", baseCommand.configFile);
//    assertEquals("https://github.com/example/repo", baseCommand.repo);
//    assertEquals("dev", baseCommand.branch);
//    assertEquals("abcdef", baseCommand.commit);
//    assertEquals("json", baseCommand.outputFormat);
//  }
//}
