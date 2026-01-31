package net.serenitybdd.screenplay.playwright.targets;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for parameterized Target selectors.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class ParameterizedTargetTest {

    Actor alice;

    // Parameterized target using {0}, {1} placeholders
    static Target CHECKBOX_BY_INDEX = Target.the("checkbox {0}").locatedBy("#checkboxes input:nth-child({0})");
    static Target LINK_BY_TEXT = Target.the("{0} link").locatedBy("text={0}");

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_resolve_parameterized_selector_with_single_parameter() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes")
        );

        // Use parameterized target with index 1
        alice.attemptsTo(
            Click.on(CHECKBOX_BY_INDEX.of("1"))
        );

        // Verify the checkbox was clicked
        alice.attemptsTo(
            Ensure.that("#checkboxes input:nth-child(1)").isChecked()
        );
    }

    @Test
    void should_create_new_target_with_substituted_values() {
        Target resolvedTarget = CHECKBOX_BY_INDEX.of("2");

        assertThat(resolvedTarget.asSelector())
            .as("Selector should have parameter substituted")
            .isEqualTo("#checkboxes input:nth-child(2)");

        assertThat(resolvedTarget.toString())
            .as("Label should have parameter substituted")
            .isEqualTo("checkbox 2");
    }

    @Test
    void should_support_text_based_parameterized_selectors() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        // Click on a link using parameterized text selector
        alice.attemptsTo(
            Click.on(LINK_BY_TEXT.of("Checkboxes"))
        );

        // Verify we navigated to checkboxes page
        alice.attemptsTo(
            Ensure.that("h3").hasText("Checkboxes")
        );
    }

    @Test
    void original_target_should_remain_unchanged_after_of() {
        String originalSelector = CHECKBOX_BY_INDEX.asSelector();

        // Create parameterized version
        CHECKBOX_BY_INDEX.of("5");

        // Original should be unchanged
        assertThat(CHECKBOX_BY_INDEX.asSelector())
            .as("Original target should not be modified")
            .isEqualTo(originalSelector);
    }
}
