package net.serenitybdd.junit5.samples.integration.displayName;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
@DisplayNameGeneration(JUnit5CustomDisplayNameGenerated.class)
public class JUnit5CustomDisplayNameExample {

    @Test
    void sample_custom_display_name_test() {
        System.out.println("Sample Custom Display Name Generator Example");
    }

    //@DisplayName takes precedence over generation
    @Test
    @DisplayName("Sample Test With Display Name")
    void sample_display_name_test() {
        System.out.println("Sample Display Name Generator Example");
    }


}
