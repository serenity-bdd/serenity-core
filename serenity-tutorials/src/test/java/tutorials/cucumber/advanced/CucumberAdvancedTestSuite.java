package tutorials.cucumber.advanced;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Cucumber test suite for Advanced Cucumber Patterns tutorial.
 *
 * Run with: mvn verify -Ptutorials
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/advanced")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty"
)
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "tutorials.cucumber.advanced"
)
@ConfigurationParameter(
    key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME,
    value = "true"
)
public class CucumberAdvancedTestSuite {
}
