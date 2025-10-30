package net.serenitybdd.core.webdriver.driverproviders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CapabilityValueTest {

    @ParameterizedTest
    @CsvSource({ "true, true", "false, false" })
    public void fromString_returns_boolean_when_input_is_boolean_string(String input, Boolean expected) {
        Object actual = CapabilityValue.fromString(input);
        assertThat(actual).isInstanceOf(Boolean.class);
        assertThat((Boolean) actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({ "1234, 1234", "0, 0", "-1234, -1234" })
    public void fromString_returns_integer_when_input_is_integer_string(String input, Integer expected) {
        Object actual = CapabilityValue.fromString(input);
        assertThat(actual).isInstanceOf(Integer.class);
        assertThat((Integer) actual).isEqualTo(expected);
    }

    @Test
    public void fromString_returns_original_string_when_input_is_non_integer_non_boolean_string() {
        String input = "hello";
        Object actual = CapabilityValue.fromString(input);
        assertThat(actual).isInstanceOf(String.class);
        assertThat((String) actual).isEqualTo(input);
    }

    @Test
    public void fromString_handles_null_input_gracefully() {
        Object actual = CapabilityValue.fromString(null);
        assertThat(actual).isNull();
    }
}
