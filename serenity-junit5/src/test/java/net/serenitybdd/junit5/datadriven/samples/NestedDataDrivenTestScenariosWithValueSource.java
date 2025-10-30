package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SerenityJUnit5Extension.class)
public class NestedDataDrivenTestScenariosWithValueSource {

    @Steps
    public SampleScenarioSteps steps;

    @ParameterizedTest(name = "run {index} with {arguments}")
    @ValueSource(strings = { "Hello", "JUnit" })
    void withValueSource(String word) {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        assertNotNull(word);
    }

    @Nested
    class NestedTestClass {
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void withValueSourceIntegers(int number) {
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }
    }
}
