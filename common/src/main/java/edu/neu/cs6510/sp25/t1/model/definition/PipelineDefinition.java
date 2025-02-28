package edu.neu.cs6510.sp25.t1.model.definition;

import java.util.List;
import java.util.Map;

/**
 * Represents the structure of a CI/CD pipeline configuration.
 */
public class PipelineDefinition {
    private String name;
    private List<StageDefinition> stages;
    private Map<String, String> globals; // Global variables

    /**
     * Constructs a new PipelineDefinition instance.
     * 
     * @param name
     * @param stages
     * @param globals
     */
    public PipelineDefinition(String name, List<StageDefinition> stages, Map<String, String> globals) {
        this.name = name;
        this.stages = stages;
        this.globals = globals;
    }

    /**
     * Getters for the fields.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getters for the fields.
     * @return the stages
     */
    public List<StageDefinition> getStages() {
        return stages;
    }

    /**
     * Getters for the fields.
     * @return the globals
     */
    public Map<String, String> getGlobals() {
        return globals;
    }
}
