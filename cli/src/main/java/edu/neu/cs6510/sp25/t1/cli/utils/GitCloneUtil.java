package edu.neu.cs6510.sp25.t1.cli.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class GitCloneUtil {
  /**
   * Clone a remote Git repo to a target directory.
   *
   * @param repoUrl The remote Git repo URL
   * @param targetDir The local directory where it should be cloned
   * @return The File pointing to the cloned directory
   * @throws GitAPIException If cloning fails
   */
  public static File cloneRepository(String repoUrl, File targetDir) throws GitAPIException {
    return Git.cloneRepository()
        .setURI(repoUrl)
        .setDirectory(targetDir)
        .call()
        .getRepository()
        .getDirectory()
        .getParentFile();
  }

  /**
   * Clone a specific branch of a remote Git repo to a target directory.
   *
   * @param repoUrl   The remote Git repo URL
   * @param targetDir The local directory where it should be cloned
   * @param branch    The branch name to clone
   * @return The File pointing to the cloned directory
   * @throws GitAPIException If cloning fails
   */
  public static File cloneRepository(String repoUrl, File targetDir, String branch) throws GitAPIException {
    return Git.cloneRepository()
        .setURI(repoUrl)
        .setDirectory(targetDir)
        .setBranch("refs/heads/" + branch)
        .call()
        .getRepository()
        .getDirectory()
        .getParentFile();
  }

  /**
   * Pulls the latest changes from the given branch in the specified Git repository directory.
   *
   * @param repoDir the local Git repository directory
   * @param branch the branch name to pull from
   * @throws Exception if an error occurs during the pull
   */
  public static void pullLatest(File repoDir, String branch) throws Exception {
    Git git = Git.open(repoDir);
    git.pull()
        .setRemoteBranchName(branch)
        .call();
  }
}
