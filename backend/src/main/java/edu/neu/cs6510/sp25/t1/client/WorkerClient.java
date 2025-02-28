package edu.neu.cs6510.sp25.t1.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import edu.neu.cs6510.sp25.t1.grpc.WorkerServiceGrpc;
import edu.neu.cs6510.sp25.t1.grpc.PipelineRequest;
import edu.neu.cs6510.sp25.t1.grpc.PipelineResponse;
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

    public void sendJobToWorker(String repoId, String pipelineId, String configFilePath) {
        PipelineRequest request = PipelineRequest.newBuilder()
                .setRepoId(repoId)
                .setPipelineId(pipelineId)
                .setConfigFilePath(configFilePath)
                .build();

        PipelineResponse response = workerStub.executePipeline(request);
        logger.info("Worker response: " + response.getMessage());
    }
}
