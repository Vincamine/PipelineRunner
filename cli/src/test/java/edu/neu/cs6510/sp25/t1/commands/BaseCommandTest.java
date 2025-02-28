package edu.neu.cs6510.sp25.t1.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Unit tests for BaseCommand class.
 */
class BaseCommandTest {

    private BaseCommand baseCommand;

    @BeforeEach
    void setUp() {
        baseCommand = new BaseCommand() {
            @Override
            public Integer call() {
                return 0; // Dummy implementation for testing
            }
        };
    }

    @Test
    void testFormatOutputPlainText() {
        baseCommand.setOutputFormat("plaintext");
        String result = baseCommand.formatOutput("Test Output");
        assertEquals("Test Output", result);
    }

    @Test
    void testFormatOutputJson() {
        baseCommand.setOutputFormat("json");
        String result = baseCommand.formatOutput(Map.of("key", "value"));
        assertTrue(result.contains("\"key\" : \"value\""));
    }

    @Test
    void testFormatOutputYaml() {
        baseCommand.setOutputFormat("yaml");
        String result = baseCommand.formatOutput(Map.of("key", "value"));
        assertTrue(result.contains("key: \"value\""));
    }

    @Test
    void testFormatOutputNullResponse() {
        String result = baseCommand.formatOutput(null);
        assertEquals("Error: No response received.", result);
    }

    @Test
    void testSettersAndGetters() {
        baseCommand.setConfigFile("config.yaml");
        baseCommand.setRepo("https://github.com/example/repo");
        baseCommand.setBranch("dev");
        baseCommand.setCommit("abcdef");
        baseCommand.setOutputFormat("json");

        assertEquals("config.yaml", baseCommand.configFile);
        assertEquals("https://github.com/example/repo", baseCommand.repo);
        assertEquals("dev", baseCommand.branch);
        assertEquals("abcdef", baseCommand.commit);
        assertEquals("json", baseCommand.outputFormat);
    }
}
