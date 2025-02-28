// package edu.neu.cs6510.sp25.t1.api;


// import edu.neu.cs6510.sp25.t1.model.PipelineStatusResponse;
// import edu.neu.cs6510.sp25.t1.service.RunPipelineService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.UUID;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(PipelineController.class)
// public class PipelineControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private RunPipelineService runPipelineService;

//     private String repoId = "test-repo";
//     private String pipelineId = "test-pipeline";
//     private UUID pipelineRunId;

//     @BeforeEach
//     void setUp() {
//         pipelineRunId = UUID.randomUUID();
//     }

//     @Test
//     void testRunPipeline() throws Exception {
//         when(runPipelineService.startPipelineExecution(repoId, pipelineId)).thenReturn(pipelineRunId);

//         mockMvc.perform(post("/api/v1/pipelines/" + repoId + "/" + pipelineId + "/run"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Pipeline execution started with ID: " + pipelineRunId));
//     }

//     @Test
//     void testGetPipelineStatus() throws Exception {
//         PipelineStatusResponse response = new PipelineStatusResponse(pipelineRunId.toString(), "Running");
//         when(runPipelineService.getPipelineStatus(repoId, pipelineId)).thenReturn(response);

//         mockMvc.perform(get("/api/v1/pipelines/" + repoId + "/" + pipelineId + "/status"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json("{\"pipelineRunId\":\"" + pipelineRunId + "\", \"status\":\"Running\"}"));
//     }
// }
