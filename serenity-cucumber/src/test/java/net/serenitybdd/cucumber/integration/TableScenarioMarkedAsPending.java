package net.serenitybdd.cucumber.integration;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features="src/test/resources/samples/pending_table_based_numbers.feature")
public class TableScenarioMarkedAsPending {}
