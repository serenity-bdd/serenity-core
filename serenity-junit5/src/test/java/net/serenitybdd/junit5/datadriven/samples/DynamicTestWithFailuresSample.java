package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Sample test class demonstrating @TestFactory with mixed pass/fail results.
 * Each dynamic test should maintain its own pass/fail status in Serenity reports.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class DynamicTestWithFailuresSample {

    @TestFactory
    Stream<DynamicTest> dynamicTestsWithMixedResults() {
        return Stream.of(
                dynamicTest("Passing test 1", () -> {
                    assertTrue(true, "This should pass");
                }),
                dynamicTest("Failing test", () -> {
                    fail("This test is expected to fail");
                }),
                dynamicTest("Passing test 2", () -> {
                    assertTrue(true, "This should also pass");
                })
        );
    }
}