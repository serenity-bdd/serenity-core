package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.WebDriver;


@ExtendWith(SerenityJUnit5Extension.class)
public class ScenarioWithTestSpecificDataAndAFailingTestSample {

    @Managed(driver = "chrome",options = "--headless")
    WebDriver driver;

    @Steps
    public SampleScenarioSteps steps;

    //TODO - it must have a {} parameter in title ...
    @ParameterizedTest(name = "Csv File Data Test {0}")
    @CsvFileSource(resources="/test-data/simple-data.csv",numLinesToSkip = 1)
    public void happy_day_scenario(String name, String age,String address) throws Throwable {
        steps.data_driven_test_step_that_fails(age);
    }
}
