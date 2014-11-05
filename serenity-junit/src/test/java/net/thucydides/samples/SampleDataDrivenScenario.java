package net.thucydides.samples;

import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.runners.ThucydidesParameterizedRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;

@RunWith(ThucydidesParameterizedRunner.class)
public class SampleDataDrivenScenario {


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
    private String option1;
    private Integer option2;

    public SampleDataDrivenScenario(String option1, Integer option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.stepWithParameters(option1, option2);
    }

    @Test
    public void another_happy_day_scenario() {
        steps.stepWithParameters(option1,option2);
    }


}
