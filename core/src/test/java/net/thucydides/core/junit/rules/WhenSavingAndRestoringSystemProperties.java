package net.thucydides.core.junit.rules;


import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.util.SystemEnvironmentVariables;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenSavingAndRestoringSystemProperties {

    private static final String SYSTEM_PROPERTY = ThucydidesSystemProperty.THUCYDIDES_REPORT_RESOURCES.getPropertyName();
    @Mock
    Statement statement;

    @Mock
    FrameworkMethod frameworkMethod;

    @Mock
    Object testClass;

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void webdriver_system_properties_should_be_saved_and_restored_when_a_test_modifies_them() throws Throwable {

        System.setProperty(SYSTEM_PROPERTY,"original-value");

        SaveWebdriverSystemPropertiesRule rule = new SaveWebdriverSystemPropertiesRule();
        Statement statementWithRule = rule.apply(statement, frameworkMethod, testClass);

        System.setProperty(SYSTEM_PROPERTY,"new-value-for-tests");

        statementWithRule.evaluate();

        assertThat(System.getProperty(SYSTEM_PROPERTY), is("original-value"));

    }

    @Test
    public void originally_empty_webdriver_system_properties_should_be_removed() throws Throwable {

        System.clearProperty(SYSTEM_PROPERTY);

        SaveWebdriverSystemPropertiesRule rule = new SaveWebdriverSystemPropertiesRule();
        Statement statementWithRule = rule.apply(statement, frameworkMethod, testClass);

        System.setProperty(SYSTEM_PROPERTY, "http://www.amazon.com");

        statementWithRule.evaluate();

        assertThat(System.getProperty(SYSTEM_PROPERTY), is(nullValue()));
    }

    @Test
    public void should_be_able_to_set_Thycydides_system_properties_easily() {

        String originalIssueTracker = new SystemPropertiesIssueTracking(new SystemEnvironmentVariables()).getIssueTrackerUrl();

        ThucydidesSystemProperties.getProperties().setValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL, "http://arbitrary.issue.tracker");

        String updatedIssueTracker = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL);;

        assertThat(updatedIssueTracker, is(not(originalIssueTracker)));

    }

    @Test
    public void should_be_able_to_read_system_values() {
        ThucydidesSystemProperties.getProperties().setValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL, "http://arbitrary.issue.tracker");

        String issueTracker = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL);

        assertThat(issueTracker, is("http://arbitrary.issue.tracker"));

    }

    @Test
    public void should_be_able_to_read_boolean_system_values() {
        System.setProperty("some.boolean.value", "true");

        SystemEnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        Boolean value = environmentVariables.getPropertyAsBoolean("some.boolean.value", false);
        System.clearProperty("some.boolean.value");
        assertThat(value, is(true));
    }

    @Test
    public void should_be_able_to_read_boolean_system_value_if_set_to_nothing() {
        System.setProperty("some.boolean.value", "");

        SystemEnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        Boolean value = environmentVariables.getPropertyAsBoolean("some.boolean.value", false);
        System.clearProperty("some.boolean.value");
        assertThat(value, is(true));
    }


    @Test
    public void should_be_able_to_read_issue_tracker_url() {
        ThucydidesSystemProperties.getProperties().setValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL, "http://arbitrary.issue.tracker");

        String issueTracker = new SystemPropertiesIssueTracking(new SystemEnvironmentVariables()).getIssueTrackerUrl();

        assertThat(issueTracker, is("http://arbitrary.issue.tracker"));

    }

    @Test
    public void should_be_able_to_read_jira_issue_tracker_url() {
        ThucydidesSystemProperties.getProperties().setValue(ThucydidesSystemProperty.JIRA_URL, "http://arbitrary.issue.tracker");

        String issueTracker = new SystemPropertiesIssueTracking(new SystemEnvironmentVariables()).getIssueTrackerUrl();

        assertThat(issueTracker, is("http://arbitrary.issue.tracker/browse/{0}"));

    }

    @Test
    public void should_be_able_to_read_system_values_with_default() {
        System.clearProperty(ThucydidesSystemProperty.THUCYDIDES_PUBLIC_URL.getPropertyName());

        String publicUrl = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.THUCYDIDES_PUBLIC_URL,"default");

        assertThat(publicUrl, is("default"));

    }

    @Test
    public void system_property_tostring_should_return_property_name() {
        assertThat(ThucydidesSystemProperty.THUCYDIDES_PUBLIC_URL.toString(), is("thucydides.public.url"));
    }


}
