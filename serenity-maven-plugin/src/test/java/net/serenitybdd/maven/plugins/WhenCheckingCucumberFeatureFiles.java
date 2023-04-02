package net.serenitybdd.maven.plugins;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class WhenCheckingCucumberFeatureFiles {

    SerenityGherkinCheckerMojo plugin;

    @Mock
    HtmlAggregateStoryReporter reporter;

    @Mock
    MavenProject project;

    @Before
    public void setupPlugin() {
        MockitoAnnotations.initMocks(this);

        plugin = new SerenityGherkinCheckerMojo();
        plugin.project = project;
        plugin.session = Mockito.mock(MavenSession.class);
        plugin.featureFilesDirectory = "src/test/resources/ecommerce_features";
        MavenProject project=Mockito.mock(MavenProject.class);
        Mockito.when(project.getBasedir()).thenReturn(new File("."));
        Mockito.when(plugin.session.getCurrentProject()).thenReturn(project);
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_gherkin_errors() throws Exception {
        plugin.execute();
    }
}
