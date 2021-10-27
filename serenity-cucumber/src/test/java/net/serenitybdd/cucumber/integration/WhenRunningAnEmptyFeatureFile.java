package net.serenitybdd.cucumber.integration;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Run all of the passing sample scenarios - a quick smoke test
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = {"src/test/resources/samples/empty"}, tags = "@shouldPass")
public class WhenRunningAnEmptyFeatureFile {
}