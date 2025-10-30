package net.serenitybdd.junit5.samples.integration;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(SerenityJUnit5Extension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JUnit5GeneratedExample {

    @Test
    void sample_test() {
    }

    @Nested
    class Nested_Test_Cases {

        @Test
        void sample_nested_test() {
        }
    }
}
