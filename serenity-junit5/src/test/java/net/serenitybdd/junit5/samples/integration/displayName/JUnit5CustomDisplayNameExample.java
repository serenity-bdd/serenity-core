package net.serenitybdd.junit5.samples.integration.displayName;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith(SerenityJUnit5Extension.class)
@DisplayNameGeneration(JUnit5CustomDisplayNameGenerated.class)
public class JUnit5CustomDisplayNameExample {

    @Test
    void sample_custom_display_name_test() {
    }

   //@DisplayName takes precedence over generation
    @Test
    @DisplayName("Sample Test With Display Name")
    void sample_display_name_test() {
    }

    @ParameterizedTest
    @CsvSource({
            "200,Occasional,2,204"
    })
    void sample_custom_display_name_parameterized_test() {

    }

}