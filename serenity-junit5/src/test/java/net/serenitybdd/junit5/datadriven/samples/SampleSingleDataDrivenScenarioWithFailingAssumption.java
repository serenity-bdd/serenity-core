package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;

import java.util.stream.Stream;

@ExtendWith(SerenityJUnit5Extension.class)
public class SampleSingleDataDrivenScenarioWithFailingAssumption {

    @Managed(driver = "chrome",options = "--headless")
    WebDriver driver;

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest
    @MethodSource("getParametersForHappyDayScenario")
    public void happy_day_scenario(String option1,Integer option2) {
        steps.stepWithFailedAssumption();
        steps.stepWithParameters(option1, option2);
    }

    private static Stream<Arguments> getParametersForHappyDayScenario() {
        return Stream.of(
                Arguments.of("a", 1),
                Arguments.of("B", 2),
                Arguments.of("c", 3),
                Arguments.of("D", 4),
                Arguments.of("e", 5)
        );
    }

}
