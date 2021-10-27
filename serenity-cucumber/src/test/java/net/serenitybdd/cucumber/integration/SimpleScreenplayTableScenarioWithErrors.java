package net.serenitybdd.cucumber.integration;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Created by john on 23/07/2014.
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features= "src/test/resources/samples/screenplay_table_based_scenario_with_failures_and_errors.feature")
public class SimpleScreenplayTableScenarioWithErrors {
}
