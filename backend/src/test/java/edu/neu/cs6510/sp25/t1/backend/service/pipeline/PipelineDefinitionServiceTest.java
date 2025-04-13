package edu.neu.cs6510.sp25.t1.backend.service.pipeline;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobScriptRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageRepository;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PipelineDefinitionServiceTest {

    @Mock
    private PipelineRepository pipelineRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobScriptRepository jobScriptRepository;

    @InjectMocks
    private PipelineDefinitionService pipelineDefinitionService;

    @Captor
    private ArgumentCaptor<PipelineEntity> pipelineCaptor;

    @Captor
    private ArgumentCaptor<StageEntity> stageCaptor;

    @Captor
    private ArgumentCaptor<JobEntity> jobCaptor;

    private PipelineExecutionRequest request;
    private Map<String, Object> pipelineConfig;
    private UUID pipelineId;
    private UUID stageId;
    private UUID jobId;

    @BeforeEach
    public void setUp() {
        pipelineId = UUID.randomUUID();
        stageId = UUID.randomUUID();
        jobId = UUID.randomUUID();

        // Create request
        UUID requestId = UUID.randomUUID();
        String filePath = "/path/to/pipeline.yaml";
        String repo = "https://github.com/test/repo";
        String branch = "main";
        boolean isLocal = false;
        int runNumber = 1;
        String commitHash = "abc123";

        request = new PipelineExecutionRequest(
                requestId,
                filePath,
                repo,
                branch,
                isLocal,
                runNumber,
                commitHash
        );

        // Create pipeline configuration
        pipelineConfig = new HashMap<>();
        pipelineConfig.put("name", "test-pipeline");

        // Setup stages (nested format)
        List<Map<String, Object>> stages = new ArrayList<>();

        // Build stage
        Map<String, Object> buildStage = new HashMap<>();
        buildStage.put("name", "build");

        // Build stage jobs
        List<Map<String, Object>> buildJobs = new ArrayList<>();
        Map<String, Object> compileJob = new HashMap<>();
        compileJob.put("name", "compile");
        compileJob.put("image", "gradle:latest");
        compileJob.put("script", "./gradlew build");
        buildJobs.add(compileJob);
        buildStage.put("jobs", buildJobs);
        stages.add(buildStage);

        // Test stage
        Map<String, Object> testStage = new HashMap<>();
        testStage.put("name", "test");

        // Test stage jobs
        List<Map<String, Object>> testJobs = new ArrayList<>();
        Map<String, Object> unitTestJob = new HashMap<>();
        unitTestJob.put("name", "unit-test");
        unitTestJob.put("image", "gradle:latest");
        unitTestJob.put("script", "./gradlew test");
        unitTestJob.put("allow_failure", true);
        testJobs.add(unitTestJob);
        testStage.put("jobs", testJobs);
        stages.add(testStage);

        pipelineConfig.put("stages", stages);
    }

    @Test
    public void testCreateOrGetPipelineEntity_ExistingPipeline() {
        // Arrange
        request = spy(request);
        when(request.getPipelineId()).thenReturn(pipelineId);
        when(pipelineRepository.existsById(pipelineId)).thenReturn(true);

        // Act
        UUID result = pipelineDefinitionService.createOrGetPipelineEntity(request, pipelineConfig);

        // Assert
        assertEquals(pipelineId, result);
        verify(pipelineRepository).existsById(pipelineId);
        verifyNoMoreInteractions(pipelineRepository);
    }

    @Test
    public void testCreateOrGetPipelineEntity_NonExistingPipeline() {
        // Arrange
        request = spy(request);
        when(request.getPipelineId()).thenReturn(pipelineId);
        when(pipelineRepository.existsById(pipelineId)).thenReturn(false);

        PipelineEntity savedPipeline = new PipelineEntity();
        savedPipeline.setId(UUID.randomUUID());
        when(pipelineRepository.saveAndFlush(any(PipelineEntity.class))).thenReturn(savedPipeline);
        when(pipelineRepository.existsById(savedPipeline.getId())).thenReturn(true);

        // Act
        UUID result = pipelineDefinitionService.createOrGetPipelineEntity(request, pipelineConfig);

        // Assert
        assertEquals(savedPipeline.getId(), result);
        verify(pipelineRepository).existsById(pipelineId);
        verify(pipelineRepository).saveAndFlush(any(PipelineEntity.class));
        verify(pipelineRepository).existsById(savedPipeline.getId());
    }

    @Test
    public void testCreateOrGetPipelineEntity_CreateNew() {
        // Arrange
        request = spy(request);
        when(request.getPipelineId()).thenReturn(null);

        PipelineEntity savedPipeline = new PipelineEntity();
        savedPipeline.setId(UUID.randomUUID());
        when(pipelineRepository.saveAndFlush(any(PipelineEntity.class))).thenReturn(savedPipeline);
        when(pipelineRepository.existsById(savedPipeline.getId())).thenReturn(true);

        // Act
        UUID result = pipelineDefinitionService.createOrGetPipelineEntity(request, pipelineConfig);

        // Assert
        assertEquals(savedPipeline.getId(), result);
        verify(pipelineRepository).saveAndFlush(pipelineCaptor.capture());
        verify(pipelineRepository).existsById(savedPipeline.getId());

        PipelineEntity capturedPipeline = pipelineCaptor.getValue();
        assertEquals("test-pipeline", capturedPipeline.getName());
        assertEquals(request.getRepo(), capturedPipeline.getRepositoryUrl());
        assertEquals(request.getBranch(), capturedPipeline.getBranch());
        assertEquals(request.getCommitHash(), capturedPipeline.getCommitHash());
    }

    @Test
    public void testCreatePipelineDefinition_NestedFormat() {
        // Arrange
        PipelineEntity pipeline = new PipelineEntity();
        pipeline.setId(pipelineId);
        pipeline.setName("test-pipeline");

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        StageEntity buildStage = new StageEntity();
        buildStage.setId(UUID.randomUUID());

        StageEntity testStage = new StageEntity();
        testStage.setId(UUID.randomUUID());

        when(stageRepository.save(any(StageEntity.class)))
                .thenReturn(buildStage)
                .thenReturn(testStage);

        when(stageRepository.existsById(any(UUID.class))).thenReturn(true);

        JobEntity compileJob = new JobEntity();
        compileJob.setId(UUID.randomUUID());

        JobEntity unitTestJob = new JobEntity();
        unitTestJob.setId(UUID.randomUUID());

        when(jobRepository.save(any(JobEntity.class)))
                .thenReturn(compileJob)
                .thenReturn(unitTestJob);

        when(jobRepository.existsById(any(UUID.class))).thenReturn(true);

        when(stageRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(buildStage))
                .thenReturn(Optional.of(testStage));

        // Act
        pipelineDefinitionService.createPipelineDefinition(pipelineId, pipelineConfig, "/root/path");

        // Assert
        verify(pipelineRepository).findById(pipelineId);

        // Verify stages were created
        verify(stageRepository, times(2)).save(stageCaptor.capture());
        List<StageEntity> capturedStages = stageCaptor.getAllValues();

        assertEquals(2, capturedStages.size());
        assertEquals("build", capturedStages.get(0).getName());
        assertEquals(0, capturedStages.get(0).getExecutionOrder());
        assertEquals("test", capturedStages.get(1).getName());
        assertEquals(1, capturedStages.get(1).getExecutionOrder());

        // Verify jobs were created
        verify(jobRepository, times(2)).save(jobCaptor.capture());
        List<JobEntity> capturedJobs = jobCaptor.getAllValues();

        assertEquals(2, capturedJobs.size());
        assertEquals("compile", capturedJobs.get(0).getName());
        assertEquals("gradle:latest", capturedJobs.get(0).getDockerImage());
        assertEquals(false, capturedJobs.get(0).isAllowFailure());

        assertEquals("unit-test", capturedJobs.get(1).getName());
        assertEquals("gradle:latest", capturedJobs.get(1).getDockerImage());
        assertEquals(true, capturedJobs.get(1).isAllowFailure());

        // Verify job scripts were saved
        verify(jobScriptRepository, times(2)).saveScript(any(UUID.class), anyString());
    }

    @Test
    public void testCreatePipelineDefinition_TopLevelFormat() {
        // Arrange
        // Change config to top-level format
        Map<String, Object> topLevelConfig = new HashMap<>();
        topLevelConfig.put("name", "test-pipeline");

        // Top-level stages as strings
        List<String> stageNames = List.of("build", "test");
        topLevelConfig.put("stages", stageNames);

        // Top-level jobs
        List<Map<String, Object>> jobs = new ArrayList<>();

        Map<String, Object> compileJob = new HashMap<>();
        compileJob.put("name", "compile");
        compileJob.put("stage", "build");
        compileJob.put("image", "gradle:latest");
        compileJob.put("script", "./gradlew build");
        jobs.add(compileJob);

        Map<String, Object> unitTestJob = new HashMap<>();
        unitTestJob.put("name", "unit-test");
        unitTestJob.put("stage", "test");
        unitTestJob.put("image", "gradle:latest");
        unitTestJob.put("script", "./gradlew test");
        unitTestJob.put("allow_failure", true);
        jobs.add(unitTestJob);

        topLevelConfig.put("jobs", jobs);

        PipelineEntity pipeline = new PipelineEntity();
        pipeline.setId(pipelineId);
        pipeline.setName("test-pipeline");

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

        StageEntity buildStage = new StageEntity();
        buildStage.setId(UUID.randomUUID());

        StageEntity testStage = new StageEntity();
        testStage.setId(UUID.randomUUID());

        when(stageRepository.save(any(StageEntity.class)))
                .thenReturn(buildStage)
                .thenReturn(testStage);

        when(stageRepository.existsById(any(UUID.class))).thenReturn(true);

        JobEntity compileJobEntity = new JobEntity();
        compileJobEntity.setId(UUID.randomUUID());

        JobEntity unitTestJobEntity = new JobEntity();
        unitTestJobEntity.setId(UUID.randomUUID());

        when(jobRepository.save(any(JobEntity.class)))
                .thenReturn(compileJobEntity)
                .thenReturn(unitTestJobEntity);

        when(jobRepository.existsById(any(UUID.class))).thenReturn(true);

        // Act
        pipelineDefinitionService.createPipelineDefinition(pipelineId, topLevelConfig, "/root/path");

        // Assert
        verify(pipelineRepository).findById(pipelineId);

        // Verify stages were created
        verify(stageRepository, times(2)).save(stageCaptor.capture());
        List<StageEntity> capturedStages = stageCaptor.getAllValues();

        assertEquals(2, capturedStages.size());
        assertEquals("build", capturedStages.get(0).getName());
        assertEquals(0, capturedStages.get(0).getExecutionOrder());
        assertEquals("test", capturedStages.get(1).getName());
        assertEquals(1, capturedStages.get(1).getExecutionOrder());

        // Verify jobs were created
        verify(jobRepository, times(2)).save(jobCaptor.capture());
        List<JobEntity> capturedJobs = jobCaptor.getAllValues();

        assertEquals(2, capturedJobs.size());
        assertEquals("compile", capturedJobs.get(0).getName());
        assertEquals("gradle:latest", capturedJobs.get(0).getDockerImage());
        assertEquals(false, capturedJobs.get(0).isAllowFailure());

        assertEquals("unit-test", capturedJobs.get(1).getName());
        assertEquals("gradle:latest", capturedJobs.get(1).getDockerImage());
        assertEquals(true, capturedJobs.get(1).isAllowFailure());

        // Verify job scripts were saved
        verify(jobScriptRepository, times(2)).saveScript(any(UUID.class), anyString());
    }

    @Test
    public void testCreatePipelineDefinition_PipelineNotFound() {
        // Arrange
        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pipelineDefinitionService.createPipelineDefinition(pipelineId, pipelineConfig, "/root/path");
        });

        assertTrue(exception.getMessage().contains("Pipeline not found"));
        verify(pipelineRepository).findById(pipelineId);
        verifyNoInteractions(stageRepository);
        verifyNoInteractions(jobRepository);
    }

    @Test
    public void testExtractPipelineName_NameProvided() {
        // Arrange
        Map<String, Object> config = new HashMap<>();
        config.put("name", "custom-pipeline");

        // Act
        String name = invokeExtractPipelineName(config);

        // Assert
        assertEquals("custom-pipeline", name);
    }

    @Test
    public void testExtractPipelineName_NameNotProvided() {
        // Arrange
        Map<String, Object> config = new HashMap<>();

        // Act
        String name = invokeExtractPipelineName(config);

        // Assert
        assertTrue(name.startsWith("pipeline-"));
        assertEquals(17, name.length()); // "pipeline-" + 8 chars
    }

    @Test
    public void testExtractRepositoryUrl_FromRequest() {
        // Arrange
        request = spy(request);
        when(request.getRepo()).thenReturn("https://github.com/custom/repo");

        Map<String, Object> config = new HashMap<>();
        config.put("repository", "should-not-use-this");

        // Act
        String url = invokeExtractRepositoryUrl(request, config);

        // Assert
        assertEquals("https://github.com/custom/repo", url);
    }

    @Test
    public void testExtractRepositoryUrl_FromConfig() {
        // Arrange
        request = spy(request);
        when(request.getRepo()).thenReturn(null);

        Map<String, Object> config = new HashMap<>();
        config.put("repository", "https://github.com/config/repo");

        // Act
        String url = invokeExtractRepositoryUrl(request, config);

        // Assert
        assertEquals("https://github.com/config/repo", url);
    }

    @Test
    public void testExtractRepositoryUrl_Default() {
        // Arrange
        request = spy(request);
        when(request.getRepo()).thenReturn(null);

        Map<String, Object> config = new HashMap<>();

        // Act
        String url = invokeExtractRepositoryUrl(request, config);

        // Assert
        assertEquals("local-repository", url);
    }

    // Helper methods to invoke private methods
    private String invokeExtractPipelineName(Map<String, Object> config) {
        try {
            java.lang.reflect.Method method = PipelineDefinitionService.class.getDeclaredMethod("extractPipelineName", Map.class);
            method.setAccessible(true);
            return (String) method.invoke(pipelineDefinitionService, config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke extractPipelineName", e);
        }
    }

    private String invokeExtractRepositoryUrl(PipelineExecutionRequest request, Map<String, Object> config) {
        try {
            java.lang.reflect.Method method = PipelineDefinitionService.class.getDeclaredMethod("extractRepositoryUrl", PipelineExecutionRequest.class, Map.class);
            method.setAccessible(true);
            return (String) method.invoke(pipelineDefinitionService, request, config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke extractRepositoryUrl", e);
        }
    }
}