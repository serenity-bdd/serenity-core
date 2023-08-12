package net.thucydides.core.webdriver;

import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;
import org.junit.Before;
import org.junit.Test;

import static net.thucydides.core.util.FileSeparatorUtil.changeSeparatorIfRequired;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

public class WhenManagingGlobalConfiguration {

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @Test
    public void the_step_delay_value_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("serenity.step.delay", "1000");

        assertThat(configuration.getStepDelay(), is(1000));
    }

    @Test
    public void the_browser_restart_value_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("serenity.restart.browser.frequency", "5");

        assertThat(configuration.getRestartFrequency(), is(5));
    }


    @Test
    public void there_is_no_step_delay_by_default() {
        assertThat(configuration.getStepDelay(), is(0));
    }

    @Test
    public void the_unique_browser_value_can_be_defined_in_a_system_property() {
        String outputDirectory = changeSeparatorIfRequired("build/reports/thucydides");
        environmentVariables.setProperty("serenity.outputDirectory", outputDirectory);
        configuration = new SystemPropertiesConfiguration(environmentVariables);

        assertThat(configuration.getOutputDirectory().getAbsoluteFile().toString(), endsWith(outputDirectory));
    }

    @Test
    public void the_output_directory_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("serenity.use.unique.browser", "true");

        assertThat(configuration.shouldUseAUniqueBrowser(), is(true));
    }

    @Test
    public void system_properties_cannot_be_set_if_defined() {
        environmentVariables.setProperty("serenity.use.unique.browser", "true");
        configuration.setIfUndefined("serenity.use.unique.browser", "false");

        assertThat(configuration.shouldUseAUniqueBrowser(), is(true));
    }

    @Test
    public void system_properties_can_be_set_if_undefined() {
        configuration.setIfUndefined("serenity.use.unique.browser", "false");

        assertThat(configuration.shouldUseAUniqueBrowser(), is(false));
    }

}
