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
}
