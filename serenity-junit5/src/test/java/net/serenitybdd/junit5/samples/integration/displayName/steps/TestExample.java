package net.serenitybdd.junit5.samples.integration.displayName.steps;

import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Assert;

public class TestExample extends ScenarioSteps {
    public void test_spaces_fail() {
        Assert.assertTrue(false);
    }
}