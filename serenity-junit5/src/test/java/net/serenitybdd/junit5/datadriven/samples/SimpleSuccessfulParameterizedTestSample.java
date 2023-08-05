package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;

import net.thucydides.samples.SampleScenarioSteps;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;

@ExtendWith(SerenityJUnit5Extension.class)
public class SimpleSuccessfulParameterizedTestSample {

    @Managed(driver = "chrome",options = "--headless")
    WebDriver driver;
    
    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest
    @ValueSource(strings = { "A", "B" ,"C"})
    public void test1() {
    }


    @ParameterizedTest
    @ValueSource(strings = { "A", "B" ,"C"})
    public void test2() {
    }

}
