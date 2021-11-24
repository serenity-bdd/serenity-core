package net.serenitybdd.maven.plugins;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WhenGeneratingAnAggregateReport {

    SerenityAggregatorMojo plugin;

    File outputDirectory = new File(".").getAbsoluteFile();


    File sourceDirectory = new File(".").getAbsoluteFile();

    @Mock
    HtmlAggregateStoryReporter reporter;

    @Mock
    MavenProject project;

    @Before
    public void setupPlugin() {
        MockitoAnnotations.initMocks(this);

        plugin = new SerenityAggregatorMojo();
        plugin.setOutputDirectory(outputDirectory);
        plugin.setSourceDirectory(sourceDirectory);
        plugin.setReporter(reporter);
        plugin.project = project;
        plugin.session = Mockito.mock(MavenSession.class);
        MavenProject project=Mockito.mock(MavenProject.class);
        Mockito.when(project.getBasedir()).thenReturn(new File("."));
        Mockito.when(plugin.session.getCurrentProject()).thenReturn(project);
    }

    @Test
    public void the_output_directory_can_be_set_via_the_plugin_configuration() throws Exception {
        plugin.execute();

        verify(reporter).setOutputDirectory(outputDirectory);
    }

    @Test
    public void the_requirements_base_dir_can_be_set_via_the_plugin_configuration() throws Exception {
        plugin.requirementsBaseDir = "somedir";
        plugin.execute();
        assertEquals("somedir", plugin.environmentVariables.getProperty("serenity.test.requirements.basedir"));
    }

    @Test
    public void the_aggregate_report_should_be_generated_using_the_specified_source_directory() throws Exception {
        plugin.execute();

        verify(reporter).generateReportsForTestResultsFrom(outputDirectory);
    }

    @Ignore
    public void the_aggregate_report_should_generate_a_new_output_directory_if_not_present() throws Exception {
        when(outputDirectory.exists()).thenReturn(false);

        plugin.execute();

        verify(outputDirectory).mkdirs();
    }

    @Ignore
    public void the_aggregate_report_should_use_an_existing_output_directory_if_present() throws Exception {
        plugin.execute();

        verify(outputDirectory,never()).mkdirs();
    }

    @Test(expected = MojoExecutionException.class)
    public void if_the_report_cant_be_written_the_plugin_execution_should_fail() throws Exception {
        doThrow(new IOException("IO error")).when(reporter).generateReportsForTestResultsFrom(any(File.class));
        plugin.execute();
    }
}
