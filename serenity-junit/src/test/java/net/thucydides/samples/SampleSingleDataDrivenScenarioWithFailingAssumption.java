package net.thucydides.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.junit.annotations.TestData;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
public class SampleSingleDataDrivenScenarioWithFailingAssumption {


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
                    {"j", 13},
                    {"k", 14},
                    {"l", 15},
            });
        }
    private String option1;
    private Integer option2;

    public SampleSingleDataDrivenScenarioWithFailingAssumption(String option1, Integer option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.stepWithFailedAssumption();
        steps.stepWithParameters(option1, option2);
    }

}
