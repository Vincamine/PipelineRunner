package edu.neu.cs6510.sp25.t1.backend.service.pipeline;

import edu.neu.cs6510.sp25.t1.backend.info.ClonedPipelineInfo;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.utils.GitCloneUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class GitPipelineService {
  public ClonedPipelineInfo cloneRepoAndLocatePipelineFile(PipelineExecutionRequest request) throws Exception {
    String mountBasePath = "/mnt/pipeline";
    UUID uuid = UUID.randomUUID();
    File cloneDir = new File(mountBasePath, uuid.toString());

    String repo = request.getRepo();
    String branch = request.getBranch();

    File clonedRepo;
    if (branch != null && !branch.isEmpty()) {
      clonedRepo = GitCloneUtil.cloneRepository(repo, cloneDir, branch);
    } else {
      clonedRepo = GitCloneUtil.cloneRepository(repo, cloneDir);
    }

    File pipelinesDir = new File(clonedRepo, ".pipelines");
    if (!pipelinesDir.exists() || !pipelinesDir.isDirectory()) {
      throw new IOException("'.pipelines' directory not found: " + pipelinesDir.getAbsolutePath());
    }

    File[] yamlFiles = pipelinesDir.listFiles((dir, name) ->
        name.toLowerCase().endsWith(".yaml") || name.toLowerCase().endsWith(".yml")
    );

    if (yamlFiles == null || yamlFiles.length == 0) {
      throw new IOException("No YAML file found in: " + pipelinesDir.getAbsolutePath());
    }

    return new ClonedPipelineInfo(yamlFiles[0].getAbsolutePath(), uuid);
  }
}
