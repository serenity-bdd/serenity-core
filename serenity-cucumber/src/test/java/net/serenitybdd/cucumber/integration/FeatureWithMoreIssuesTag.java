package net.serenitybdd.cucumber.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/resources/samples/multiple_jira_issues.feature")
public class FeatureWithMoreIssuesTag {}
