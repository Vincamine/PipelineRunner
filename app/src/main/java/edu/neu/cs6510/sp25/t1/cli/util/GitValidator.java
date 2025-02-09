package edu.neu.cs6510.sp25.t1.cli.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for validating if the CLI is running inside a Git repository.
 */
public class GitValidator {
    private static final Logger LOGGER = Logger.getLogger(GitValidator.class.getName());

    /**
     * Checks if the current working directory is a Git repository.
     * 
     * @return true if the directory contains a .git folder, false otherwise.
     */
    public static boolean isGitRepository() {
        final File gitDir = new File(".git");
        return gitDir.exists() && gitDir.isDirectory();
    }

    /**
     * Validates the Git repository presence and throws an exception if not found.
     * 
     * @throws IllegalStateException if the CLI is not run from a Git repository.
     */
    public static void validateGitRepo() {
        if (!isGitRepository()) {
            LOGGER.log(Level.SEVERE, "Error: This CLI must be run from the root of a Git repository.");
            throw new IllegalStateException("Error: This CLI must be run from the root of a Git repository.");
        }
    }
}
