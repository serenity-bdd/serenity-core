package net.serenitybdd.plugins.jira;

import net.serenitybdd.plugins.jira.service.JIRAConfiguration;
import net.serenitybdd.plugins.jira.service.SystemPropertiesJIRAConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenObtainingTheJIRAConfiguration {

    JIRAConfiguration configuration;
    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Before
    public void saveSystemProperties() {
        configuration = new SystemPropertiesJIRAConfiguration(environmentVariables);
    }

    @Test
    public void username_should_be_specified_in_the_jira_username_system_property() {
        environmentVariables.setProperty("jira.username", "joe");
        assertThat(configuration.getJiraUser(), is("joe"));
    }

    @Test
    public void password_should_be_specified_in_the_jira_password_system_property() {
        environmentVariables.setProperty("jira.password", "secret");
        assertThat(configuration.getJiraPassword(), is("secret"));
    }

    @Test
    public void base_url_should_be_specified_in_the_jira_url_system_property() {
        environmentVariables.setProperty("jira.url", "http://build.server/jira");
        assertThat(configuration.getJiraWebserviceUrl(), is("http://build.server/jira"));
    }

}
