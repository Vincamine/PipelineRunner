package edu.neu.cs6510.sp25.t1.common.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GitCloneUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void cloneRepository() throws GitAPIException {
        // Use Mockito to mock Git operations
        try (MockedStatic<Git> gitMock = mockStatic(Git.class)) {
            // Arrange
            String repoUrl = "https://github.com/Mingtianfang-Li/demoProject2";
            File targetDir = tempDir.toFile();

            // Mock the Git clone operation
            Git mockGit = mock(Git.class);
            Repository mockRepo = mock(Repository.class);
            File mockGitDir = mock(File.class);
            File mockParentFile = mock(File.class);

            // Set up the mock chain
            org.eclipse.jgit.api.CloneCommand mockCloneCmd = mock(org.eclipse.jgit.api.CloneCommand.class);
            gitMock.when(Git::cloneRepository).thenReturn(mockCloneCmd);
            when(mockCloneCmd.setURI(repoUrl)).thenReturn(mockCloneCmd);
            when(mockCloneCmd.setDirectory(targetDir)).thenReturn(mockCloneCmd);
            when(mockCloneCmd.call()).thenReturn(mockGit);
            when(mockGit.getRepository()).thenReturn(mockRepo);
            when(mockRepo.getDirectory()).thenReturn(mockGitDir);
            when(mockGitDir.getParentFile()).thenReturn(mockParentFile);

            // Act
            File result = GitCloneUtil.cloneRepository(repoUrl, targetDir);

            // Assert
            assertEquals(mockParentFile, result);
        }
    }

    @Test
    void testCloneRepository() throws GitAPIException {
        // Use Mockito to mock Git operations
        try (MockedStatic<Git> gitMock = mockStatic(Git.class)) {
            // Arrange
            String repoUrl = "https://github.com/Mingtianfang-Li/demoProject2";
            String branch = "main";
            File targetDir = tempDir.toFile();

            // Mock the Git clone operation
            Git mockGit = mock(Git.class);
            Repository mockRepo = mock(Repository.class);
            File mockGitDir = mock(File.class);
            File mockParentFile = mock(File.class);

            // Set up the mock chain
            org.eclipse.jgit.api.CloneCommand mockCloneCmd = mock(org.eclipse.jgit.api.CloneCommand.class);
            gitMock.when(Git::cloneRepository).thenReturn(mockCloneCmd);
            when(mockCloneCmd.setURI(repoUrl)).thenReturn(mockCloneCmd);
            when(mockCloneCmd.setDirectory(targetDir)).thenReturn(mockCloneCmd);
            when(mockCloneCmd.setBranch("refs/heads/" + branch)).thenReturn(mockCloneCmd);
            when(mockCloneCmd.call()).thenReturn(mockGit);
            when(mockGit.getRepository()).thenReturn(mockRepo);
            when(mockRepo.getDirectory()).thenReturn(mockGitDir);
            when(mockGitDir.getParentFile()).thenReturn(mockParentFile);

            // Act
            File result = GitCloneUtil.cloneRepository(repoUrl, targetDir, branch);

            // Assert
            assertEquals(mockParentFile, result);
        }
    }

    @Test
    void checkoutCommit() throws Exception {
        // Use Mockito to mock Git operations
        try (MockedStatic<Git> gitMock = mockStatic(Git.class)) {
            // Arrange
            File repoDir = tempDir.toFile();
            String commitHash = "7f175e2706e6202530715ec8b246ac5282bfd45a";

            // Mock the Git open operation
            Git mockGit = mock(Git.class);
            org.eclipse.jgit.api.FetchCommand mockFetchCmd = mock(org.eclipse.jgit.api.FetchCommand.class);
            org.eclipse.jgit.api.CheckoutCommand mockCheckoutCmd = mock(org.eclipse.jgit.api.CheckoutCommand.class);

            // Set up the mock chain
            gitMock.when(() -> Git.open(any(File.class))).thenReturn(mockGit);
            when(mockGit.fetch()).thenReturn(mockFetchCmd);
            when(mockFetchCmd.call()).thenReturn(null);
            when(mockGit.checkout()).thenReturn(mockCheckoutCmd);
            when(mockCheckoutCmd.setName(commitHash)).thenReturn(mockCheckoutCmd);
            when(mockCheckoutCmd.call()).thenReturn(null);

            // Act - should not throw exception
            GitCloneUtil.checkoutCommit(repoDir, commitHash);

            // Assert - verify method calls
            gitMock.verify(() -> Git.open(repoDir));
            verify(mockGit).fetch();
            verify(mockFetchCmd).call();
            verify(mockGit).checkout();
            verify(mockCheckoutCmd).setName(commitHash);
            verify(mockCheckoutCmd).call();
        }
    }

    @Test
    void checkoutCommitInBranch() throws Exception {
        // Use Mockito to mock Git operations
        try (MockedStatic<Git> gitMock = mockStatic(Git.class)) {
            // Arrange
            File repoDir = tempDir.toFile();
            String branchName = "main";
            String commitHash = "7f175e2706e6202530715ec8b246ac5282bfd45a";

            // Mock the Git open operation
            Git mockGit = mock(Git.class);
            org.eclipse.jgit.api.FetchCommand mockFetchCmd = mock(org.eclipse.jgit.api.FetchCommand.class);
            org.eclipse.jgit.api.CheckoutCommand mockCheckoutCmd = mock(org.eclipse.jgit.api.CheckoutCommand.class);

            // Set up the mock chain
            gitMock.when(() -> Git.open(any(File.class))).thenReturn(mockGit);
            when(mockGit.fetch()).thenReturn(mockFetchCmd);
            when(mockFetchCmd.call()).thenReturn(null);
            when(mockGit.checkout()).thenReturn(mockCheckoutCmd);
            when(mockCheckoutCmd.setName(anyString())).thenReturn(mockCheckoutCmd);
            when(mockCheckoutCmd.setCreateBranch(anyBoolean())).thenReturn(mockCheckoutCmd);
            when(mockCheckoutCmd.setStartPoint(anyString())).thenReturn(mockCheckoutCmd);
            when(mockCheckoutCmd.setUpstreamMode(any())).thenReturn(mockCheckoutCmd);
            when(mockCheckoutCmd.call()).thenReturn(null);

            // Act - should not throw exception
            GitCloneUtil.checkoutCommitInBranch(repoDir, branchName, commitHash);

            // Assert - verify the key method calls
            gitMock.verify(() -> Git.open(repoDir));
            verify(mockGit).fetch();
            verify(mockFetchCmd).call();
            verify(mockGit, times(2)).checkout(); // Should be called twice
        }
    }

    @Test
    void isInsideGitRepo() {
        File nonGitDir = tempDir.toFile();
        boolean result = GitCloneUtil.isInsideGitRepo(nonGitDir);
        assertFalse(result);
    }
}