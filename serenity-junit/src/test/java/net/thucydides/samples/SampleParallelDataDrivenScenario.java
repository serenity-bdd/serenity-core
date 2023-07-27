package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent
public class SampleParallelDataDrivenScenario {

    @TestData
    public static Collection testData() {
            return Arrays.asList(new Object[][]{
                    {"a", 1},
                    {"b", 2},
                    {"c", 3}
            });
        }
    private String option1;
    private Integer option2;

    public SampleParallelDataDrivenScenario(String option1, Integer option2) {
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
        steps.stepWithParameters(option1,option2);
    }

}
