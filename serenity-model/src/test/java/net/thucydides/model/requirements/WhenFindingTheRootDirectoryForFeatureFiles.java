package net.thucydides.model.requirements;

import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

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
}
