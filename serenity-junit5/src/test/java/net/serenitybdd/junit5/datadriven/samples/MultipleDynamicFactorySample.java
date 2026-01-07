package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Sample test class demonstrating multiple @TestFactory methods.
 * Each factory should produce separate test outcomes in the Serenity report.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class MultipleDynamicFactorySample {

    @TestFactory
    Stream<DynamicTest> firstFactory() {
        return Stream.of("A", "B")
                .map(letter -> dynamicTest("First factory - " + letter, () -> {
                    assertTrue(letter.length() == 1, "Should be single character");
                }));
    }

    @TestFactory
    Collection<DynamicTest> secondFactory() {
        return List.of(
                dynamicTest("Second factory - X", () -> assertTrue(true)),
                dynamicTest("Second factory - Y", () -> assertTrue(true)),
                dynamicTest("Second factory - Z", () -> assertTrue(true))
        );
    }
}