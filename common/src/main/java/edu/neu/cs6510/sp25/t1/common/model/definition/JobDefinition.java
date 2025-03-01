package edu.neu.cs6510.sp25.t1.common.model.definition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a job in a CI/CD pipeline configuration.
 */
public class JobDefinition {
    private String name;
    private String stageName;
    private String image;
    private List<String> script;
    private List<String> needs; // Dependencies
    private boolean allowFailure;

    /**
     * Constructor for JobDefinition.
     * @param name
     * @param stageName
     * @param image
     * @param script
     * @param needs
     * @param allowFailure
     */
    @JsonCreator
    public JobDefinition(
            @JsonProperty("name") String name,
            @JsonProperty("stage") String stageName,
            @JsonProperty("image") String image,
            @JsonProperty("script") List<String> script,
            @JsonProperty("needs") List<String> needs,
            @JsonProperty("allowFailure") boolean allowFailure) {
        this.name = name;
        this.stageName = stageName;
        this.image = image;
        this.script = script;
        this.needs = needs != null ? needs : List.of();
        this.allowFailure = allowFailure;
    }

    /**
     * Getter for name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for stage name.
     * @return
     */
    public String getStageName() {
        return stageName;
    }

    /**
     * Getter for image.
     * @return image
     */
    public String getImage() {
        return image;
    }

    /**
     * Getter for script.
     * @return script
     */
    public List<String> getScript() {
        return script;
    }

    /**
     * Getter for needs.
     * @return needs
     */
    public List<String> getNeeds() {
        return needs;
    }

    /**
     * Getter for allowFailure.
     * @return allowFailure
     */
    public boolean isAllowFailure() {
        return allowFailure;
    }
}
