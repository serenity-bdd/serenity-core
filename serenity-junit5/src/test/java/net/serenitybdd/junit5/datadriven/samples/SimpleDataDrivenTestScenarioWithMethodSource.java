package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.StepsInjectorTestInstancePostProcessor;
import net.thucydides.core.annotations.Steps;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(StepsInjectorTestInstancePostProcessor.class)
public class SimpleDataDrivenTestScenarioWithMethodSource {

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest(name = "run {index} with {arguments}")
    @MethodSource("stringProvider")
    public void withMethodSourceSimpleStatic(String string1,String string2) {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        //assertNotNull(word1);
    }

    static Stream<Arguments> stringProvider() {
        return Stream.of(arguments("apple", "banana"), arguments("peaches","pears"));
    }
}