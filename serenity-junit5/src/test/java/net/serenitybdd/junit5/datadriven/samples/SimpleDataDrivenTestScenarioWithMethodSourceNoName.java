package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityBDD;
import net.serenitybdd.junit5.StepsInjectorTestInstancePostProcessor;
import net.thucydides.core.annotations.Steps;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(StepsInjectorTestInstancePostProcessor.class)
@SerenityBDD
public class SimpleDataDrivenTestScenarioWithMethodSourceNoName {

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest(name = "run {index} with {arguments}")
    @MethodSource
    void withMethodSourceSimpleStaticNoName(String string1,String string2) {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

    static Stream<Arguments> withMethodSourceSimpleStaticNoName() {
        return Stream.of(arguments("apple", "banana"), arguments("peaches","pears"));
    }
}
