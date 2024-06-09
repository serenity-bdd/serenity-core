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
        MavenProject project=Mockito.mock(MavenProject.class);
        Mockito.when(project.getBasedir()).thenReturn(new File("."));
        Mockito.when(plugin.session.getCurrentProject()).thenReturn(project);
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_gherkin_errors() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/ecommerce_features";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_gherkin_errors_in_a_default_directory_structure() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/bad_features";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_gherkin_errors_in_a_nested_directory_structure() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/nested_bad_features";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_gherkin_errors_in_nested_modules() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/parent_module";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_incorrect_tags_in_a_scenario() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/bad_tags_example/bad_tag_in_scenario";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_incorrect_tags_in_a_nested_scenario() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/bad_tags_example/bad_tag_in_nested_scenario";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_incorrect_tags_in_a_rule() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/bad_tags_example/bad_tag_in_rules";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_incorrect_tags_in_exmaples() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/bad_tags_example/bad_tag_in_example";
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_fail_if_there_are_incorrect_tags_in_a_feature() throws Exception {
        plugin.featureFilesDirectory = "src/test/resources/bad_tags_example/bad_tag_in_feature";
        plugin.execute();
    }

}
