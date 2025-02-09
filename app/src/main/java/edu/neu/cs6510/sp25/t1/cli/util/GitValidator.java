package edu.neu.cs6510.sp25.t1.cli.util;

import java.io.File;
import java.nio.file.Paths;
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
     * @return {@code true} if a `.git` directory is found; otherwise, {@code false}.
     */
    public static boolean isGitRepository() {
        File currentDir = new File(System.getProperty("user.dir"));

        while (currentDir != null) {
            final File gitDir = Paths.get(currentDir.getAbsolutePath(), ".git").toFile();
            if (gitDir.exists() && gitDir.isDirectory()) {
                return true;
            }
            currentDir = currentDir.getParentFile();
        }

        return false;
    }

    /**
     * Validates if the CLI is running inside a Git repository and throws an error if not.
     *
     * @throws IllegalStateException if the CLI is not executed from a Git repository.
     */
    public static void validateGitRepo() {
        if (!isGitRepository()) {
            final String error = ErrorFormatter.format("GitValidator.java", 50, 10, "This CLI must be run from the root of a Git repository.");
            LOGGER.log(Level.SEVERE, error);
            throw new IllegalStateException(error);
        }
    }
}
