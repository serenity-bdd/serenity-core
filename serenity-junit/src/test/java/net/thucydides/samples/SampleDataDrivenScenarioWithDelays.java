package net.thucydides.samples;


import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.junit.annotations.TestData;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;

/**
 * User: YamStranger
 * Date: 11/17/15
 * Time: 10:23 AM
 */
@RunWith(SerenityParameterizedRunner.class)
public class SampleDataDrivenScenarioWithDelays extends SampleDataDrivenScenario {

    @TestData
    public static Collection testData() {
        return Arrays.asList(new Object[][]{
            {"a", 1},
            {"B", 2},
            {"c", 3},
            {"D", 4},
            {"e", 5},
            {"F", 6},
            {"g", 7},
            {"h", 8},
            {"i", 9},
            {"j", 10},
            {"k", 11},
            {"l", 12},
        });
    }

    public SampleDataDrivenScenarioWithDelays(final String option1, final Integer option2) {
        super(option1, option2);
    }

    @Test
    public void happy_day_scenario() {
        steps.stepWithParameters(this.getOption1(), getOption2(), true);
    }

    @Test
    public void another_happy_day_scenario() {
        steps.stepWithParameters(getOption1(), getOption2(), true);
    }
}