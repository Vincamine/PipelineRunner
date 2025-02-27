package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.service.ReportService;
import edu.neu.cs6510.sp25.t1.util.ReportErrorHandler;
import edu.neu.cs6510.sp25.t1.util.ReportFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import picocli.CommandLine;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReportCommand class.
 * Tests various scenarios including successful executions and error cases.
 */
class ReportCommandTest {
        private ReportService mockReportService;
        private ReportCommand reportCommand;
        private CommandLine cmd;

        /**
         * Sets up the test environment before each test.
         * Initializes mocks and creates a fresh command instance.
         */
        @BeforeEach
        void setUp() {
                mockReportService = mock(ReportService.class);
                reportCommand = new ReportCommand(mockReportService);
                cmd = new CommandLine(reportCommand);
        }

        /**
         * Tests successful retrieval and display of pipeline reports.
         */
        @Test
        void testReports_ValidPipeline_Success() {
                final ReportEntry mockReport = new ReportEntry(
                                "123",
                                "SUCCESS",
                                "Pipeline executed successfully",
                                Instant.now().toEpochMilli(),
                                "SUCCESS",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                1,
                                "abc123",
                                Instant.now().minusSeconds(600).toEpochMilli(),
                                Instant.now().toEpochMilli());

                when(mockReportService.getPipelineRuns(eq("repo-url"), eq("123"))).thenReturn(List.of(mockReport));

                try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
                        reportFormatterMock.when(() -> ReportFormatter.format(mockReport))
                                        .thenReturn("SUCCESS: Pipeline executed successfully");

                        int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123");

                        assertEquals(0, exitCode);
                        verify(mockReportService).getPipelineRuns(eq("repo-url"), eq("123"));
                }
        }

        /**
         * Tests successful retrieval and display of all pipeline names.
         */
        @Test
        void testReports_ListAllPipelines_Success() {
                final List<String> pipelineNames = List.of("pipeline1", "pipeline2");
                when(mockReportService.getAllPipelineNames(eq("repo-url")))
                                .thenReturn(pipelineNames);

                try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
                        reportFormatterMock.when(() -> ReportFormatter.format(any()))
                                        .thenReturn("Available Pipelines: pipeline1, pipeline2");

                        int exitCode = cmd.execute("--repo", "repo-url");

                        assertEquals(0, exitCode, "Pipeline list should be retrieved successfully.");
                        verify(mockReportService).getAllPipelineNames(eq("repo-url"));
                }
        }

        /**
         * Tests proper handling of missing reports.
         */
        @Test
        void testReports_NoReportsFound() {
                when(mockReportService.getPipelineRuns(eq("repo-url"), eq("123")))
                                .thenReturn(Collections.emptyList());

                try (MockedStatic<ReportErrorHandler> errorHandlerMock = mockStatic(ReportErrorHandler.class)) {
                        int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123");

                        errorHandlerMock.verify(
                                        () -> ReportErrorHandler.reportMissingPipeline(eq("123")),
                                        times(1));
                        assertEquals(0, exitCode, "Command should execute successfully but report missing pipeline.");
                }
        }

        /**
         * Tests successful retrieval of stage reports.
         */
        @Test
        void testReports_StageReport_Success() {
                final ReportEntry mockReport = new ReportEntry(
                                "stage1",
                                "SUCCESS",
                                "Stage executed successfully",
                                Instant.now().toEpochMilli(),
                                "SUCCESS",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                1,
                                "abc123",
                                Instant.now().minusSeconds(600).toEpochMilli(),
                                Instant.now().toEpochMilli());

                when(mockReportService.getStageReport(eq("repo-url"), eq("123"), eq(1), eq("stage1")))
                                .thenReturn(List.of(mockReport));

                try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
                        reportFormatterMock.when(() -> ReportFormatter.format(mockReport))
                                        .thenReturn("SUCCESS: Stage executed successfully");

                        int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123", "--run", "1", "--stage",
                                        "stage1");

                        assertEquals(0, exitCode);
                        verify(mockReportService).getStageReport(eq("repo-url"), eq("123"), eq(1), eq("stage1"));
                }
        }

        /**
         * Tests error handling when repository URL is missing.
         */
        @Test
        void testReports_MissingRepoUrl() {
                try (MockedStatic<ReportErrorHandler> errorHandlerMock = mockStatic(ReportErrorHandler.class)) {
                        int exitCode = cmd.execute("--pipeline", "123");

                        errorHandlerMock.verify(
                                        () -> ReportErrorHandler
                                                        .reportError(eq("Either --repo or --local must be specified.")),
                                        times(1));
                        assertCommandFailed(exitCode);
                }
        }

        /**
         * Tests proper handling of service exceptions.
         */
        @Test
        void testReports_ServiceThrowsException() {
                when(mockReportService.getPipelineRuns(eq("repo-url"), eq("123")))
                                .thenThrow(new RuntimeException("Database error"));

                try (MockedStatic<ReportErrorHandler> errorHandlerMock = mockStatic(ReportErrorHandler.class)) {
                        int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123");

                        errorHandlerMock.verify(
                                        () -> ReportErrorHandler.reportError(
                                                        eq("Unexpected error occurred"),
                                                        any(RuntimeException.class)),
                                        times(1));
                        assertEquals(1, exitCode, "Command should return error exit code");
                }
        }

        /**
         * Tests validation of job report parameters.
         */
        @Test
        void testReports_JobMissingStage() {
                try (MockedStatic<ReportErrorHandler> errorHandlerMock = mockStatic(ReportErrorHandler.class)) {
                        int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123",
                                        "--run", "1", "--job", "job1");

                        errorHandlerMock.verify(
                                        () -> ReportErrorHandler.reportError(
                                                        eq("Stage name must be specified when requesting job report.")),
                                        times(1));
                        assertCommandFailed(exitCode);
                }
        }

        /**
         * Tests successful retrieval of local repository reports.
         */
        @Test
        void testReports_LocalRepository_Success() {
                final ReportEntry mockReport = new ReportEntry(
                                "123",
                                "SUCCESS",
                                "Pipeline executed successfully",
                                Instant.now().toEpochMilli(),
                                "SUCCESS",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                3,
                                "commitABC",
                                Instant.now().minusSeconds(1200).toEpochMilli(),
                                Instant.now().toEpochMilli());

                when(mockReportService.getLocalPipelineRuns("123")).thenReturn(List.of(mockReport));

                try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
                        reportFormatterMock.when(() -> ReportFormatter.format(mockReport))
                                        .thenReturn("SUCCESS: Pipeline executed successfully");

                        int exitCode = cmd.execute("--local", "--pipeline", "123");

                        assertEquals(0, exitCode, "Local repository reports should be retrieved successfully.");
                        verify(mockReportService).getLocalPipelineRuns("123");
                }
        }

        /**
         * Tests validation of stage report parameters.
         */
        @Test
        void testReports_StageMissingPipeline() {
                try (MockedStatic<ReportErrorHandler> errorHandlerMock = mockStatic(ReportErrorHandler.class)) {
                        int exitCode = cmd.execute("--repo", "repo-url", "--stage", "stage1");

                        errorHandlerMock.verify(
                                        () -> ReportErrorHandler.reportError(
                                                        eq("Pipeline name and run number must be specified for stage/job reports.")),
                                        times(1));
                        assertCommandFailed(exitCode);
                }
        }

        /**
         * Tests successful retrieval of job reports.
         */
        @Test
        void testReports_JobReport_Success() {
                final ReportEntry mockReport = new ReportEntry(
                                "job1",
                                "SUCCESS",
                                "Job executed successfully",
                                Instant.now().toEpochMilli(),
                                "SUCCESS",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                2,
                                "commitXYZ",
                                Instant.now().minusSeconds(600).toEpochMilli(),
                                Instant.now().toEpochMilli());

                when(mockReportService.getJobReport(eq("repo-url"), eq("123"), eq(1), eq("stage1"), eq("job1")))
                                .thenReturn(List.of(mockReport));

                try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
                        reportFormatterMock.when(() -> ReportFormatter.format(mockReport))
                                        .thenReturn("SUCCESS: Job executed successfully");

                        int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123",
                                        "--run", "1", "--stage", "stage1", "--job", "job1");

                        assertEquals(0, exitCode, "Job report should be retrieved successfully.");
                        verify(mockReportService).getJobReport(eq("repo-url"), eq("123"), eq(1), eq("stage1"),
                                        eq("job1"));
                }
        }

        /**
         * Tests successful retrieval of specific pipeline run summary.
         */
        @Test
        void testReports_RunSummary_Success() {
                final ReportEntry mockReport = new ReportEntry(
                                "run1",
                                "SUCCESS",
                                "Run completed successfully",
                                Instant.now().toEpochMilli(),
                                "SUCCESS",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                5,
                                "commitRUN",
                                Instant.now().minusSeconds(1500).toEpochMilli(),
                                Instant.now().toEpochMilli());

                when(mockReportService.getPipelineRunSummary(eq("repo-url"), eq("123"), eq(1)))
                                .thenReturn(List.of(mockReport));

                try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
                        reportFormatterMock.when(() -> ReportFormatter.format(mockReport))
                                        .thenReturn("SUCCESS: Run completed successfully");

                        int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123", "--run", "1");

                        assertEquals(0, exitCode, "Run summary should be retrieved successfully.");
                        verify(mockReportService).getPipelineRunSummary(eq("repo-url"), eq("123"), eq(1));
                }
        }

        /**
         * Tests handling of local pipeline run summary.
         */
        void testReports_LocalRunSummary_Success() {
                final ReportEntry mockReport = new ReportEntry(
                                "run1",
                                "SUCCESS",
                                "Local run completed successfully",
                                Instant.now().toEpochMilli(),
                                "SUCCESS",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                6,
                                "commitLOCAL",
                                Instant.now().minusSeconds(1800).toEpochMilli(),
                                Instant.now().toEpochMilli());

                when(mockReportService.getLocalPipelineRunSummary(eq("123"), eq(1)))
                                .thenReturn(List.of(mockReport));

                try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
                        reportFormatterMock.when(() -> ReportFormatter.format(mockReport))
                                        .thenReturn("SUCCESS: Local run completed successfully");

                        int exitCode = cmd.execute("--local", "--pipeline", "123", "--run", "1");

                        assertEquals(0, exitCode, "Local run summary should be retrieved successfully.");
                        verify(mockReportService).getLocalPipelineRunSummary(eq("123"), eq(1));
                }
        }

        /**
         * Helper method to assert that a command failed with usage error.
         *
         * @param exitCode the exit code returned by the command
         */
        private void assertCommandFailed(int exitCode) {
                assertEquals(CommandLine.ExitCode.USAGE, exitCode,
                                "Command should fail with usage error exit code");
        }
}