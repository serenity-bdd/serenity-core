package net.serenitybdd.cucumber.smoketests;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features="classpath:smoketests/using_background_steps.feature")
public class WhenUsingBackgroundSteps {}
