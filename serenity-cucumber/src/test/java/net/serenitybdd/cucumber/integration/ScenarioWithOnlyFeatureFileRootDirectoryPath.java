package net.serenitybdd.cucumber.integration;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Created by Ramanathan Raghunathan on 06/01/2018.
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features="src/test/resources/features",tags = "@feature and @example_one")
public class ScenarioWithOnlyFeatureFileRootDirectoryPath {}