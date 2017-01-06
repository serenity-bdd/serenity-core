package net.thucydides.core.webdriver;

import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import static net.thucydides.core.util.FileSeparatorUtil.changeSeparatorIfRequired;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        environmentVariables.setProperty("thucydides.step.delay", "1000");

        assertThat(configuration.getStepDelay(), is(1000));
    }

    @Test
    public void a_configuration_can_be_safely_copied() {
        environmentVariables.setProperty("thucydides.step.delay", "1000");
        Configuration copy = configuration.copy();
        ((SystemPropertiesConfiguration) copy).getEnvironmentVariables().setProperty("thucydides.step.delay", "2000");
        assertThat(copy.getStepDelay(), is(not(configuration.getStepDelay())));
        assertThat(configuration.getStepDelay(), is(1000));
    }


    @Test
    public void the_browser_restart_value_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("thucydides.restart.browser.frequency", "5");

        assertThat(configuration.getRestartFrequency(), is(5));
    }


    @Test
    public void there_is_no_step_delay_by_default() {
        assertThat(configuration.getStepDelay(), is(0));
    }

    @Test
    public void the_unique_browser_value_can_be_defined_in_a_system_property() {
        String outputDirectory = changeSeparatorIfRequired("build/reports/thucydides");
        environmentVariables.setProperty("thucydides.outputDirectory", outputDirectory);

        assertThat(configuration.getOutputDirectory().getAbsoluteFile().toString(), endsWith(outputDirectory));
    }

    @Test
    public void the_output_directory_can_be_defined_in_a_system_property() {
        environmentVariables.setProperty("thucydides.use.unique.browser", "true");

        assertThat(configuration.shouldUseAUniqueBrowser(), is(true));
    }

    @Test
    public void system_properties_cannot_be_set_if_defined() {
        environmentVariables.setProperty("thucydides.use.unique.browser", "true");
        configuration.setIfUndefined("thucydides.use.unique.browser", "false");

        assertThat(configuration.shouldUseAUniqueBrowser(), is(true));
    }

    @Test
    public void system_properties_can_be_set_if_undefined() {
        configuration.setIfUndefined("thucydides.use.unique.browser", "false");

        assertThat(configuration.shouldUseAUniqueBrowser(), is(false));
    }

    @Test
    public void the_default_unique_browser_value_should_be_false() {
        assertThat(configuration.shouldUseAUniqueBrowser(), is(false));
    }
}
