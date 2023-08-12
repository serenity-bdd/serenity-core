package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.serenitybdd.annotations.Steps;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
public class SimpleSuccessfulParametrizedTestSample {

    protected String userRole = "ROLE";

    public SimpleSuccessfulParametrizedTestSample(String userRole) {
        this.userRole = userRole;
    }

    @TestData(columnNames = "User")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{{"A"}, {"B"}, {"C"}});
    }

    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void test1() {
    }

    @Test
    public void test2() {
    }

}
