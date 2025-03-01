package edu.neu.cs6510.sp25.t1.common.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for verifying if the CLI is being executed inside a Git
 * repository.
 */
public class GitValidator {
    private static final Logger LOGGER = Logger.getLogger(GitValidator.class.getName());

    /**
     * Checks whether the current directory is inside a Git repository.
     *
     * @return {@code true} if a `.git` directory is found; otherwise,
     *         {@code false}.
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
     * Ensures the CLI is running inside a Git repository.
     *
     * @throws IllegalStateException if the CLI is not executed from a Git
     *                               repository.
     */
    public static void validateGitRepo() {
        if (!isGitRepository()) {
            final String message = "This CLI must be run from the root of a Git repository.";
            LOGGER.log(Level.SEVERE, message);
            throw new IllegalStateException(message);
        }
    }
}
