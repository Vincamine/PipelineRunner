package edu.neu.cs6510.sp25.t1.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import edu.neu.cs6510.sp25.t1.grpc.Worker.PipelineRequest;
import edu.neu.cs6510.sp25.t1.grpc.Worker.PipelineResponse;
import edu.neu.cs6510.sp25.t1.grpc.WorkerServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkerClient {

    private static final Logger logger = LoggerFactory.getLogger(WorkerClient.class);
    private final WorkerServiceGrpc.WorkerServiceBlockingStub workerStub;

    public WorkerClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        workerStub = WorkerServiceGrpc.newBlockingStub(channel);
    }

    public void sendJobToWorker(String repoUrl, String branch, String commit) {
        // Build request using correct field names from worker.proto
        PipelineRequest request = PipelineRequest.newBuilder()
                .setRepoUrl(repoUrl)   // Corrected field name
                .setBranch(branch)     // Corrected field name
                .setCommit(commit)     // Corrected field name
                .build();

        // Use correct gRPC method name
        PipelineResponse response = workerStub.runPipeline(request);

        // Fix response logging (use available fields)
        logger.info("Worker response - Status: " + response.getStatus());
        logger.info("Worker response - Logs: " + response.getLogs());
    }
}
