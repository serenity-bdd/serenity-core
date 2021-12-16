package net.serenitybdd.cucumber.integration;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Created by Ramanathan Raghunathan on 18/12/2017.
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features="src/test/resources/features/calculator/basic_arithmetic_with_tables_and_examples_tags.feature",tags = "@feature or @scenario_outline")
public class RunExamplesMatchingFeatureLevelOrOutlineLevelTags {}