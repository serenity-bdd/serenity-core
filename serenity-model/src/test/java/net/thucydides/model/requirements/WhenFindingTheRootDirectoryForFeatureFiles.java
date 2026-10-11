package net.thucydides.model.requirements;

import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WhenFindingTheRootDirectoryForFeatureFiles {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    void withAWindowsPath() {
        RootDirectory rootDirectory = new RootDirectory(environmentVariables,"C:\\dev\\src\\test\\resources\\features");
        Set<String> directoryPaths = rootDirectory.getRootDirectoryPaths();
    }

    @Test
    void withADefinedProjectDirectory() {
        environmentVariables.setProperty("serenity.project.directory",new File(".").getAbsolutePath());
        String featureDirectory = new File("src/test/resources/features").getAbsolutePath();
        RootDirectory rootDirectory = new RootDirectory(environmentVariables,featureDirectory);
        Set<String> directoryPaths = rootDirectory.getRootDirectoryPaths();
    }

    @Test
    void featuresOrStoriesRootDirectoryFallsBackToTestRequirementsBasedir(@TempDir Path tempDir) throws Exception {
        // Simulates the Gradle plugin's AggregateTask wiring: `requirementsBaseDir` is set,
        // but `serenity.requirements.dir` is not. featuresOrStoriesRootDirectory() should
        // pick up the basedir instead of falling through to the working-directory scan
        // (which misfires in multi-module Gradle builds because user.dir == root project).
        Path featuresDir = tempDir.resolve("features");
        Files.createDirectories(featuresDir);

        environmentVariables.setProperty("serenity.test.requirements.basedir", featuresDir.toString());

        RootDirectory rootDirectory = new RootDirectory(environmentVariables, ".");
        Optional<Path> resolved = rootDirectory.featuresOrStoriesRootDirectory();

        assertTrue(resolved.isPresent(), "Expected featuresOrStoriesRootDirectory() to honour serenity.test.requirements.basedir");
        assertEquals(featuresDir.toAbsolutePath(), resolved.get().toAbsolutePath());
    }

    @Test
    void serenityRequirementsDirTakesPrecedenceOverBasedir(@TempDir Path tempDir) throws Exception {
        // When both properties are set, the existing behaviour (SERENITY_REQUIREMENTS_DIR wins)
        // must be preserved — the new fallback must not regress that order.
        Path primary = tempDir.resolve("primary/features");
        Path fallback = tempDir.resolve("fallback/features");
        Files.createDirectories(primary);
        Files.createDirectories(fallback);

        environmentVariables.setProperty("serenity.requirements.dir", primary.toString());
        environmentVariables.setProperty("serenity.test.requirements.basedir", fallback.toString());

        Optional<Path> resolved = new RootDirectory(environmentVariables, ".").featuresOrStoriesRootDirectory();

        assertTrue(resolved.isPresent());
        assertEquals(primary.toAbsolutePath(), resolved.get().toAbsolutePath());
    }

    @Test
    void basedirFallbackIgnoresNonExistentPaths(@TempDir Path tempDir) {
        // Guard against a stale basedir value masking the classpath/working-dir fallback.
        String nonExistent = tempDir.resolve("does-not-exist").toString();
        environmentVariables.setProperty("serenity.test.requirements.basedir", nonExistent);

        Optional<Path> resolved = new RootDirectory(environmentVariables, ".").featuresOrStoriesRootDirectory();

        // The fallback must not hand back a path that does not exist — callers treat the
        // Optional.of(...) result as authoritative.
        resolved.ifPresent(path -> assertTrue(path.toFile().exists(),
                "featuresOrStoriesRootDirectory() should not return a non-existent basedir: " + path));
    }
}
