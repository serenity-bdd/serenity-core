package net.serenitybdd.cucumber.integration.ju5;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.runner.RunWith;

@Suite
@IncludeEngines("cucumber")
@CucumberOptions(
		plugin = "io.cucumber.core.plugin.SerenityReporter",
		glue = "net.serenitybdd.cucumber.integration.steps"
)
@SelectClasspathResource("samples/simple_scenario.feature")
public class CucumberTestSuite {
}
