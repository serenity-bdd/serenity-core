package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Sample test class demonstrating @TestFactory with Serenity step libraries.
 * Each dynamic test should record its own steps in the Serenity report.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class DynamicTestWithStepsSample {

    @Steps
    SampleSteps steps;

    @TestFactory
    Stream<DynamicTest> dynamicTestsWithSteps() {
        return Stream.of("Alpha", "Beta", "Gamma")
                .map(item -> dynamicTest("Process " + item, () -> {
                    steps.step_one(item);
                    steps.step_two(item);
                }));
    }

    public static class SampleSteps {
        @Step("Processing item: {0}")
        public void step_one(String item) {
            // Step implementation
        }

        @Step("Completing item: {0}")
        public void step_two(String item) {
            // Step implementation
        }
    }
}