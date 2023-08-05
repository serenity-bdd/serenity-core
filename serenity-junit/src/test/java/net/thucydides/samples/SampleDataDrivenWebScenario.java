package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.TestData;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
public class SampleDataDrivenWebScenario {


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
                    {"j", 10}
            });
        }
    private String option1;
    private Integer option2;

    public SampleDataDrivenWebScenario(String option1, Integer option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    @Managed(driver = "chrome", options="--headless")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.aStepThatAlsoUsesABrowser();
        steps.stepWithParameters(option1, option2);
    }

    @Test
    @Ignore
    public void not_so_happy_day_scenario() {
        steps.stepWithParameters(option1,option2);
    }


}
