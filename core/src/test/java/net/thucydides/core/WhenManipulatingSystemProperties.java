package net.thucydides.core;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenManipulatingSystemProperties {

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Test
    public void should_be_able_to_read_a_thucydides_system_property() {
        System.setProperty("jira.url","http://my.jira.server");
        String storedValue = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.JIRA_URL);

        assertThat(storedValue, is("http://my.jira.server"));
    }

    @Test
    public void should_be_able_to_read_a_thucydides_system_property_when_default_is_provided() {
        System.setProperty("jira.url","http://my.jira.server");
        String storedValue = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.JIRA_URL,
                                                                                 "DEFAULT");

        assertThat(storedValue, is("http://my.jira.server"));
    }

    @Test
    public void should_be_able_to_read_a_numerical_thucydides_system_property() {
        System.setProperty("thucydides.step.delay","10");
        Integer storedValue = ThucydidesSystemProperties.getProperties().getIntegerValue(ThucydidesSystemProperty.THUCYDIDES_STEP_DELAY, 0);

        assertThat(storedValue, is(10));
    }

    @Test
    public void should_return_default_value_if_numerical_system_property_not_provided() {
        System.clearProperty("thucycides.step.delay");
        Integer storedValue = ThucydidesSystemProperties.getProperties().getIntegerValue(ThucydidesSystemProperty.THUCYDIDES_STEP_DELAY, 20);

        assertThat(storedValue, is(20));
    }

    @Test
    public void should_be_able_to_read_a_boolean_thucydides_system_property() {
        System.setProperty("thucycides.use.unique.browser","true");
        Boolean storedValue = ThucydidesSystemProperties.getProperties().getBooleanValue (ThucydidesSystemProperty.THUCYDIDES_USE_UNIQUE_BROWSER, true);

        assertThat(storedValue, is(true));
    }

    @Test
    public void should_be_able_to_read_a_false_boolean_thucydides_system_property() {
        System.setProperty("thucydides.use.unique.browser","false");
        Boolean storedValue = ThucydidesSystemProperties.getProperties().getBooleanValue (ThucydidesSystemProperty.THUCYDIDES_USE_UNIQUE_BROWSER, true);

        assertThat(storedValue, is(false));
    }

    @Test
    public void should_be_able_to_read_a_boolean_thucydides_system_property_using_the_default_value() {
        System.clearProperty("thucycides.use.unique.browser");
        Boolean storedValue = ThucydidesSystemProperties.getProperties().getBooleanValue (ThucydidesSystemProperty.THUCYDIDES_USE_UNIQUE_BROWSER, true);

        assertThat(storedValue, is(true));
    }

    @Test
    public void should_return_the_default_value_if_a_thucydides_system_property_has_not_been_defined() {
        System.clearProperty("jira.url");
        String storedValue = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.JIRA_URL,
                                                                                 "DEFAULT");

        assertThat(storedValue, is("DEFAULT"));
    }

    @Test
    public void should_know_when_a_property_is_not_defined() {
        System.clearProperty("jira.url");
        assertThat(ThucydidesSystemProperties.getProperties().isDefined(ThucydidesSystemProperty.JIRA_URL), is(false));
    }

    @Test
    public void should_know_when_a_property_is_defined() {
        System.setProperty("jira.url","http://my.jira.server");
        assertThat(ThucydidesSystemProperties.getProperties().isDefined(ThucydidesSystemProperty.JIRA_URL), is(true));
        assertThat(ThucydidesSystemProperties.getProperties().isEmpty(ThucydidesSystemProperty.JIRA_URL), is(false));
    }

    @Test
    public void should_know_when_a_property_is_empty() {
        System.setProperty("jira.url","");
        assertThat(ThucydidesSystemProperties.getProperties().isDefined(ThucydidesSystemProperty.JIRA_URL), is(true));
        assertThat(ThucydidesSystemProperties.getProperties().isEmpty(ThucydidesSystemProperty.JIRA_URL), is(true));
    }

    @Test
    public void should_treat_an_undefined_property_as_empty() {
        System.clearProperty("jira.url");
        assertThat(ThucydidesSystemProperties.getProperties().isDefined(ThucydidesSystemProperty.JIRA_URL), is(false));
        assertThat(ThucydidesSystemProperties.getProperties().isEmpty(ThucydidesSystemProperty.JIRA_URL), is(true));
    }

    @Test
    public void should_be_able_to_set_a_thucydides_property() {
        ThucydidesSystemProperties.getProperties().setValue(ThucydidesSystemProperty.JIRA_URL, "SOME VALUE");
        String storedValue = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.JIRA_URL);

        assertThat(storedValue, is("SOME VALUE"));
    }

    @Test
    public void system_properties_should_print_to_the_property_name() {
        ThucydidesSystemProperties.getProperties().setValue(ThucydidesSystemProperty.JIRA_URL, "SOME VALUE");
        String storedValue = ThucydidesSystemProperties.getProperties().getValue(ThucydidesSystemProperty.JIRA_URL);

        assertThat(ThucydidesSystemProperty.JIRA_URL.toString(), is("jira.url"));
    }
}
