package net.serenitybdd.junit.runners;

import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.batches.TestCountBasedBatchManager;
import net.thucydides.core.webdriver.DriverConfigurationError;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.samples.SuccessfulSingleTestScenario;
import net.thucydides.samples.SuccessfulSingleTestScenarioWithABrowser;
import net.thucydides.samples.SuccessfulSingleTestScenarioWithWrongBrowser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

/**
 * Instanciating new Webdriver instances. When using the Thucydides test runner,
 * new WebDriver driver instances are instanciated based on system properties.
 *
 * @author johnsmart
 */
public class WhenInstanciatingANewTestRunner extends AbstractTestStepRunnerTest {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();


    @Test
    public void the_default_output_directory_should_follow_the_maven_convention() throws InitializationError {

        SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class);

        File outputDirectory = runner.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is("target" + FILE_SEPARATOR + "site" + FILE_SEPARATOR + "serenity"));
    }


    @Test
    public void system_should_complain_if_we_use_an_unsupported_driver()
            throws InitializationError {

        try {
            environmentVariables.setProperty("webdriver.driver", "netscape");
            SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class);

            runner.run(new RunNotifier());

            fail("Should have thrown DriverConfigurationError");
        } catch (DriverConfigurationError e) {
            assertThat(e.getMessage(), containsString("Unsupported browser type: netscape"));
        }
    }


    @Test
    public void lynx_is_not_a_supported_driver()
            throws InitializationError {
        try {
            environmentVariables.setProperty("webdriver.driver", "lynx");

            SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class);

            runner.run(new RunNotifier());

            fail("Should have thrown DriverConfigurationError");
        } catch (DriverConfigurationError e) {
            assertThat(e.getMessage(), containsString("Unsupported browser type: lynx"));
        }
    }

    @Test
    public void driver_can_be_overridden_using_the_driver_property_in_the_Managed_annotation() throws InitializationError {
        environmentVariables.setProperty("webdriver.driver", "opera");

        SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenarioWithABrowser.class);

        runner.run(new RunNotifier());
    }

    @Test
    public void should_not_allow_an_incorrectly_specified_driver()
            throws InitializationError {
        try {
            environmentVariables.setProperty("webdriver.driver", "firefox");

            SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenarioWithWrongBrowser.class);

            runner.run(new RunNotifier());

            fail("Should have thrown DriverConfigurationError");
        } catch (DriverConfigurationError e) {
            assertThat(e.getMessage(), containsString("Unsupported browser type: doesnotexist"));
        }
    }

    @Test
    public void the_output_directory_can_be_defined_by_a_system_property() throws InitializationError {

        environmentVariables.setProperty("thucydides.outputDirectory", "target" + FILE_SEPARATOR
                + "reports" + FILE_SEPARATOR
                + "thucydides");

        SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class);

        File outputDirectory = runner.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is("target" + FILE_SEPARATOR
                + "reports" + FILE_SEPARATOR
                + "thucydides"));

    }

    @Test
    public void the_output_directory_can_be_defined_by_a_system_property_using_any_standard_separators() throws InitializationError {

        SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class);

        environmentVariables.setProperty("thucydides.outputDirectory", "target/reports/thucydides");

        File outputDirectory = runner.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is("target" + FILE_SEPARATOR
                + "reports" + FILE_SEPARATOR
                + "thucydides"));

    }

    @Test
    public void a_batch_runner_is_set_by_default() throws InitializationError {
        SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class);

        assertThat(runner.getBatchManager(), instanceOf(SystemVariableBasedBatchManager.class));

    }

    @Test
    public void a_batch_runner_can_be_overridden_using_system_property() throws InitializationError {

//        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_BATCH_STRATEGY.getPropertyName(), BatchStrategy.DIVIDE_BY_TEST_COUNT.name());
        environmentVariables.setProperty("thucydides.batch.strategy", "DIVIDE_BY_TEST_COUNT");

        SerenityRunner runner = getTestRunnerUsing(SuccessfulSingleTestScenario.class);

        assertThat(runner.getBatchManager(), instanceOf(TestCountBasedBatchManager.class));

    }

}
