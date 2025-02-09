package edu.neu.cs6510.sp25.t1.cli.util;

import java.io.File;
import java.nio.file.Paths;

/**
 * Utility class for validating if the CLI is running inside a Git repository.
 * <p>
 * This class provides methods to:
 * <ul>
 *     <li>Check if the current working directory is a Git repository.</li>
 *     <li>Validate Git presence and throw an error if not inside a Git repository.</li>
 * </ul>
 * <p>
 * It is useful for ensuring that the CLI commands operate within a valid Git context.
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Check if inside a Git repository
 * boolean isGitRepo = GitValidator.isGitRepository();
 *
 * // Validate and throw an error if not inside a Git repository
 * GitValidator.validateGitRepo();
 * </pre>
 *
 */
public class GitValidator {
    /**
     * Checks if the current working directory is a valid Git repository.
     * <p>
     * It verifies that a `.git` directory exists and contains essential Git files such as:
     * <ul>
     *     <li>`HEAD` - Tracks the current branch reference.</li>
     *     <li>`config` - Stores repository configuration details.</li>
     * </ul>
     *
     * @return {@code true} if the `.git` directory exists and contains essential Git files; otherwise, {@code false}.
     */
    public static boolean isGitRepository() {
        // Get the absolute path of the current working directory
        final String projectDir = System.getProperty("user.dir");
        final File gitDir = Paths.get(projectDir, ".git").toFile();
    
        // Debugging information
        System.out.println("DEBUG: Project directory -> " + projectDir);
        System.out.println("DEBUG: Checking if .git exists -> " + gitDir.exists());
        System.out.println("DEBUG: Checking if .git is a directory -> " + gitDir.isDirectory());
    
        return gitDir.exists() && gitDir.isDirectory();
    }
    
    // public static boolean isGitRepository() {
    //     File gitDir = new File(".git");
    //     System.out.println("DEBUG: Checking if .git exists -> " + gitDir.exists());
    //     System.out.println("DEBUG: Checking if .git is a directory -> " + gitDir.isDirectory());
    //     return gitDir.exists() && gitDir.isDirectory();
    // }
    
    
    // public static boolean isGitRepository() {
    //     final File gitDir = new File(".git");
    //     System.out.println("Current Directory: " + new File(".").getAbsolutePath());
    //     // Ensure that .git exists, is a directory, and contains essential Git files
    //     return gitDir.exists() && gitDir.isDirectory() &&
    //            new File(gitDir, "HEAD").exists() &&
    //            new File(gitDir, "config").exists();
    // }

    /**
     * Validates if the current working directory is a Git repository.
     * <p>
     * If the directory is not a valid Git repository, an {@link IllegalStateException} is thrown.
     * This ensures that CLI commands requiring Git functionality are executed in the correct context.
     * </p>
     *
     * @throws IllegalStateException if the CLI is not run from the root of a Git repository.
     */
    public static void validateGitRepo() {
        if (!isGitRepository()) {
            System.err.println("❌ Error: This CLI must be run from the root of a Git repository.");
            throw new IllegalStateException("Error: This CLI must be run from the root of a Git repository.");
        }
    }
}
