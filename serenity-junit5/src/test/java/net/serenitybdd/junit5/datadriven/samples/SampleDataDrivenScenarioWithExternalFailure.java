package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.core.pages.Pages;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SerenityJUnit5Extension.class)
public class SampleDataDrivenScenarioWithExternalFailure {

    @Managed(driver = "chrome", options="--headless")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest
    @MethodSource("getParametersForPendingScenario")
    public void happy_day_scenario_with_failure(String option1,int option2) {
        steps.stepWithParameters(option1, option2);
        assertThat(option2,not(equalTo(2)));
    }

    private static Stream<Arguments> getParametersForPendingScenario() {
        return Stream.of(
                Arguments.of("a", 1),
                Arguments.of("B", 2),
                Arguments.of("c", 3),
                Arguments.of("D", 4),
                Arguments.of("e", 5)
        );
    }
}
