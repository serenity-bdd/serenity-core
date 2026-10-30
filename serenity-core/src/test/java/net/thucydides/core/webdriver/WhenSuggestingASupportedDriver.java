package net.thucydides.core.webdriver;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WhenSuggestingASupportedDriver {

    @Test
    public void a_misspelt_chrome_is_matched_to_chrome() {
        assertThat(SupportedWebDriver.getClosestDriverValueTo("chromee")).isEqualTo(SupportedWebDriver.CHROME);
    }

    @Test
    public void a_misspelt_firefox_is_matched_to_firefox() {
        assertThat(SupportedWebDriver.getClosestDriverValueTo("firefx")).isEqualTo(SupportedWebDriver.FIREFOX);
    }

    @Test
    public void the_matching_ignores_case() {
        assertThat(SupportedWebDriver.getClosestDriverValueTo("Safarri")).isEqualTo(SupportedWebDriver.SAFARI);
    }

    @Test
    public void the_error_message_suggests_the_closest_driver() {
        assertThatThrownBy(() -> SupportedWebDriver.getDriverTypeFor("chromee"))
                .hasMessageContaining("Did you mean chrome?");
    }
}
