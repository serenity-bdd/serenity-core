package net.serenitybdd.cucumber.integration.ju5;



import io.cucumber.junit.CucumberOptions;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@CucumberOptions(
		plugin = "io.cucumber.core.plugin.SerenityReporter",
		glue = "net.serenitybdd.cucumber.integration.steps",
		features = {"samples/simple_scenario.feature:90"}
)
@SelectClasspathResource("samples/simple_scenario.feature")
public class CucumberTestSuite {
}
