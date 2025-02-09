package edu.neu.cs6510.sp25.t1.cli.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for validating if the CLI is running inside a Git repository.
 * <p>
 * This class provides methods to:
 * <ul>
 *     <li>Check if the current working directory is inside a Git repository.</li>
 *     <li>Validate Git presence and throw an error if not inside a Git repository.</li>
 * </ul>
 * <p>
 * It ensures that CLI commands operate within a valid Git context.
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Check if inside a Git repository
 * boolean isGitRepo = GitValidator.isGitRepository();
 *
 * // Validate and throw an error if not inside a Git repository
 * GitValidator.validateGitRepo();
 * </pre>
 */
public class GitValidator {
    private static final Logger LOGGER = Logger.getLogger(GitValidator.class.getName());

    /**
     * Checks if the current working directory or any parent directory is a Git repository.
     * <p>
     * This method walks up the directory tree from the current working directory
     * until it finds a `.git` folder.
     *
     * @return {@code true} if a `.git` directory is found; otherwise, {@code false}.
     */
    public static boolean isGitRepository() {
        File currentDir = new File(System.getProperty("user.dir"));

        while (currentDir != null) {
            final File gitDir = Paths.get(currentDir.getAbsolutePath(), ".git").toFile();

            LOGGER.log(Level.INFO, "DEBUG: Checking {0}", gitDir.getAbsolutePath());

            if (gitDir.exists() && gitDir.isDirectory()) {
                LOGGER.log(Level.INFO, "✅ Git repository detected at {0}", currentDir.getAbsolutePath());
                return true;
            }

            currentDir = currentDir.getParentFile(); // Move up one level
        }

        LOGGER.log(Level.WARNING, "❌ No Git repository detected.");
        return false;
    }

    /**
     * Validates whether the current working directory is inside a Git repository.
     * <p>
     * If a `.git` directory is not found, this method throws an exception.
     *
     * @throws IllegalStateException if the CLI is not executed from a Git repository.
     */
    public static void validateGitRepo() {
        if (!isGitRepository()) {
            LOGGER.log(Level.SEVERE, "❌ Error: This CLI must be run from the root of a Git repository.");
            throw new IllegalStateException("Error: This CLI must be run from the root of a Git repository.");
        }
    }
}
