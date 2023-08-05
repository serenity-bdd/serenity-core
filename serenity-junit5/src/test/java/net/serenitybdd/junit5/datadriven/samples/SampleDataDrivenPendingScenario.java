package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Pending;
import net.thucydides.core.annotations.Steps;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;


@ExtendWith(SerenityJUnit5Extension.class)
public class SampleDataDrivenPendingScenario {

    @Steps
    public SampleScenarioSteps steps;


    @ParameterizedTest
    @MethodSource("getParametersForPendingScenario")
    @Pending
    public void pending_scenario(String option1,int option2) {
        steps.stepWithParameters(option1,option2);
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
