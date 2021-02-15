package net.serenitybdd.maven.plugins;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 7/09/11
 * Time: 7:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class WhenUsingTheMavenProjectHelper {

    @Mock
    MavenProject project;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void project_identifier_should_be_the_group_and_artifact_id() {
        when(project.getGroupId()).thenReturn("group");
        when(project.getArtifactId()).thenReturn("artifact");

        assertThat(MavenProjectHelper.getProjectIdentifier(project), is("group-artifact"));

    }

    @Test
    public void project_identifier_should_an_empty_string_if_no_project_is_defined() {
        assertThat(MavenProjectHelper.getProjectIdentifier(null), is(""));

    }
}
