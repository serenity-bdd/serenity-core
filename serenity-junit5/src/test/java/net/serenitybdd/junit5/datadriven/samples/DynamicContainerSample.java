package net.serenitybdd.junit5.datadriven.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Sample test class demonstrating @TestFactory with nested DynamicContainers.
 * Each container and its tests should be properly reported in Serenity.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class DynamicContainerSample {

    @TestFactory
    Stream<DynamicNode> dynamicTestsWithContainers() {
        return Stream.of("User", "Admin")
                .map(role -> dynamicContainer("Tests for " + role,
                        Stream.of(
                                dynamicTest("Can login as " + role, () -> {
                                    assertTrue(true, role + " should be able to login");
                                }),
                                dynamicTest("Can view dashboard as " + role, () -> {
                                    assertTrue(true, role + " should see dashboard");
                                })
                        )
                ));
    }
}