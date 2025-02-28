package edu.neu.cs6510.sp25.t1.commands;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import picocli.CommandLine;

/**
 * Base class for CLI commands.
 * Provides common options and methods for command-line interaction.
 */
public abstract class BaseCommand implements Callable<Integer> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @CommandLine.Option(names = { "-v", "--verbose" }, description = "Enable verbose output.")
    protected boolean verbose;

    @CommandLine.Option(names = { "-f", "--file" }, description = "Path to the pipeline configuration file")
    protected String configFile;

    @CommandLine.Option(names = { "-r", "--repo" }, description = "Path or URL to the repository")
    protected String repo;

    @CommandLine.Option(names = { "-br", "--branch" }, description = "Git branch to use", defaultValue = "main")
    protected String branch;

    @CommandLine.Option(names = { "-co", "--commit" }, description = "Git commit hash to use")
    protected String commit;

    @CommandLine.Option(names = { "-o",
            "--output" }, description = "Output format: plaintext, json, yaml", defaultValue = "plaintext")
    protected String outputFormat;

    /**
     * Default constructor for unit testing.
     */
    public BaseCommand() {
    }

    /**
     * Set the config file - for unit testing.
     * 
     * @param configFile
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    /**
     * Set the repo - for unit testing.
     * 
     * @param repo
     */
    public void setRepo(String repo) {
        this.repo = repo;
    }

    /**
     * Set the branch - for unit testing.
     * 
     * @param branch
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * Set the commit - for unit testing.
     * 
     * @param commit
     */
    public void setCommit(String commit) {
        this.commit = commit;
    }

    /**
     * Set the output format - for unit testing.
     * 
     * @param outputFormat
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * Formats the output based on the specified format.
     * 
     * @param response The API response object.
     * @return The formatted output string.
     */
    protected String formatOutput(Object response) {
        if (response == null) {
            return "Error: No response received.";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            YAMLMapper yamlMapper = new YAMLMapper();

            switch (outputFormat.toLowerCase()) {
                case "json":
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                case "yaml":
                    return yamlMapper.writeValueAsString(response);
                default:
                    return response.toString();
            }
        } catch (Exception e) {
            logger.error("Failed to format output", e);
            return "Error formatting output.";
        }
    }
}
