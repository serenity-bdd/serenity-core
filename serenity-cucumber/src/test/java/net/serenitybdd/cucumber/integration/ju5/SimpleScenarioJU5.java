package net.serenitybdd.cucumber.integration.ju5;


import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.runner.RunWith;

@Suite
@CucumberOptions(
		plugin = "io.cucumber.core.plugin.SerenityReporter",
        features="src/test/resources/samples/simple_scenario.feature"
)
@SelectClasspathResource("samples")
public class SimpleScenarioJU5 {}
