package edu.neu.cs6510.sp25.t1.common.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

/**
 * Custom deserializer for Stage objects.
 * <p>
 * This deserializer handles two cases:
 * 1. When a stage is provided as a simple string (just a name)
 * 2. When a stage is provided as a complete object with all attributes
 */
public class StageDeserializer extends JsonDeserializer<Stage> {

    @Override
    public Stage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);
        
        // Case 1: If the node is a text node, treat it as a stage name
        if (node instanceof TextNode) {
            String stageName = node.asText();
            return new Stage(stageName);
        }
        
        // Case 2: Otherwise, deserialize as a full stage object
        if (node instanceof ObjectNode objNode) {
            UUID id = null;
            if (objNode.has("id") && !objNode.get("id").isNull()) {
                id = UUID.fromString(objNode.get("id").asText());
            }
            
            String name = null;
            if (objNode.has("name")) {
                name = objNode.get("name").asText();
            }
            
            UUID pipelineId = null;
            if (objNode.has("pipelineId") && !objNode.get("pipelineId").isNull()) {
                pipelineId = UUID.fromString(objNode.get("pipelineId").asText());
            }
            
            int executionOrder = 0;
            if (objNode.has("executionOrder")) {
                executionOrder = objNode.get("executionOrder").asInt();
            }
            
            List<Job> jobs = new ArrayList<>();
            if (objNode.has("jobs") && objNode.get("jobs").isArray()) {
                ArrayNode jobNodes = (ArrayNode) objNode.get("jobs");
                for (JsonNode jobNode : jobNodes) {
                    Job job = mapper.treeToValue(jobNode, Job.class);
                    jobs.add(job);
                }
            }
            
            LocalDateTime createdAt = null;
            if (objNode.has("createdAt") && !objNode.get("createdAt").isNull()) {
                createdAt = mapper.treeToValue(objNode.get("createdAt"), LocalDateTime.class);
            }
            
            LocalDateTime updatedAt = null;
            if (objNode.has("updatedAt") && !objNode.get("updatedAt").isNull()) {
                updatedAt = mapper.treeToValue(objNode.get("updatedAt"), LocalDateTime.class);
            }
            
            return new Stage(id, name, pipelineId, executionOrder, jobs, createdAt, updatedAt);
        }
        
        throw new IOException("Cannot deserialize Stage: unexpected input format");
    }
}