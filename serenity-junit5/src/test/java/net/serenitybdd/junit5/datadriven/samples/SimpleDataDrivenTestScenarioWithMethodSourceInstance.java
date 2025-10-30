package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SerenityJUnit5Extension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleDataDrivenTestScenarioWithMethodSourceInstance {

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest(name = "run {index} with {arguments}")
    @MethodSource("stringProvider")
    void withMethodSourceSimpleStatic(String string1,String string2) {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

    Stream<Arguments> stringProvider() {
        return Stream.of(arguments("apple", "banana"), arguments("peaches","pears"));
    }
}
