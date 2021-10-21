package net.serenitybdd.junit5.datadriven.samples;


import net.serenitybdd.junit5.SerenityBDD;
import net.serenitybdd.junit5.StepsInjectorTestInstancePostProcessor;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.WebDriver;

@ExtendWith(StepsInjectorTestInstancePostProcessor.class)
@SerenityBDD
public class ScenarioWithTestSpecificDataAndASkippedTestSample {

    @Managed(driver = "htmlunit")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;


    @ParameterizedTest(name = "Csv File Data Test {0}")
    @CsvFileSource(resources="/test-data/simple-data.csv",numLinesToSkip = 1)
    public void happy_day_scenario(String name, int age,String address) throws Throwable {
        steps.data_driven_test_step_that_is_skipped();
    }
}