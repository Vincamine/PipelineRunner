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

  /**
   * Fetches and checks out a specific commit in the given Git repo.
   *
   * @param repoDir the local Git repository directory
   * @param commitHash the SHA of the commit to checkout
   * @throws Exception if an error occurs
   */
  public static void checkoutCommit(File repoDir, String commitHash) throws Exception {
    try (Git git = Git.open(repoDir)) {
      // Fetch from origin to get all recent commits
      git.fetch().call();

      // Checkout the specific commit
      git.checkout()
          .setName(commitHash)
          .call();
    }
  }

  /**
   * Checks out a specific commit within a given branch.
   *
   * This puts the repo in a detached HEAD state at the commit.
   *
   * @param repoDir the local Git repository directory
   * @param branchName the name of the branch to fetch and check out (e.g., "main")
   * @param commitHash the commit SHA to check out
   * @throws Exception if an error occurs
   */
  public static void checkoutCommitInBranch(File repoDir, String branchName, String commitHash) throws Exception {
    try (Git git = Git.open(repoDir)) {
      // Step 1: Fetch all from origin
      git.fetch().call();

      // Step 2: Checkout the branch (create local tracking if needed)
      git.checkout()
          .setName(branchName)
          .setCreateBranch(true)
          .setStartPoint("origin/" + branchName)
          .setUpstreamMode(org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode.TRACK)
          .call();

      // Step 3: Checkout the specific commit
      git.checkout()
          .setName(commitHash)
          .call();
    }
  }
}
