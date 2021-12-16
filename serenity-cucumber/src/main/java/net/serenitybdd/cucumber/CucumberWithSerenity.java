package net.serenitybdd.cucumber;

import io.cucumber.junit.CucumberSerenityRunner;
import org.junit.runners.model.InitializationError;

/**
 * Run Cucumber with Serenity using JUnit 4
 */
public class CucumberWithSerenity extends CucumberSerenityRunner {

    public CucumberWithSerenity(Class clazz) throws InitializationError {
        super(clazz);
    }
}
