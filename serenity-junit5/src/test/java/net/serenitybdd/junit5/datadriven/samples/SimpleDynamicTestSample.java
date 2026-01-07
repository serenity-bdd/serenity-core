package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Sample test class demonstrating basic @TestFactory usage with Serenity.
 * Each dynamic test should appear as a separate test in the Serenity report.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class SimpleDynamicTestSample {

    @TestFactory
    Stream<DynamicTest> simpleDynamicTests() {
        return Stream.of("First", "Second", "Third")
                .map(name -> dynamicTest("Test " + name, () -> {
                    // Simple assertion that should pass
                    assertTrue(name.length() > 0, "Name should not be empty");
                }));
    }
}