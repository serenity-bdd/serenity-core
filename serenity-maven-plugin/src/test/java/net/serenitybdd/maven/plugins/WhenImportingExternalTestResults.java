package net.serenitybdd.maven.plugins;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class WhenImportingExternalTestResults {

    SerenityAdaptorMojo plugin;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File outputDirectory;

    @Before
    public void setupPlugin() throws IOException {

        outputDirectory = temporaryFolder.newFolder("sample-output");

        plugin = new SerenityAdaptorMojo();
        plugin.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_data_from_specified_test_results() throws Exception {
        plugin.setFormat("xunit");
        plugin.setSource(getResourcesAt("/xunit-sample-output"));

        plugin.execute();

        assertThat(outputDirectory.list(jsonFiles())).hasSize(5);
    }

    private FilenameFilter jsonFiles() {
        return (file, filename) -> filename.endsWith(".json") && !filename.startsWith("manifest");
    }

    private File getResourcesAt(String path) {
        return new File(getClass().getResource(path).getFile());
    }
}
