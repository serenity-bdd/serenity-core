package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for form interactions - Clear, Uncheck.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class FormInteractionsTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_clear_input_field() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/inputs"),
            Enter.theValue("12345").into("input[type='number']"),
            Ensure.that("input[type='number']").currentValue("12345"),
            Clear.field("input[type='number']"),
            Ensure.that("input[type='number']").currentValue("")
        );
    }

    @Test
    void should_uncheck_checkbox() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes"),
            // Second checkbox is checked by default
            Ensure.that("#checkboxes input >> nth=1").isChecked(),
            Uncheck.checkbox("#checkboxes input >> nth=1"),
            Ensure.that("#checkboxes input >> nth=1").isNotChecked()
        );
    }

    @Test
    void should_check_and_uncheck_checkbox() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes"),
            // First checkbox is unchecked by default
            Ensure.that("#checkboxes input >> nth=0").isNotChecked(),
            Check.the("#checkboxes input >> nth=0"),
            Ensure.that("#checkboxes input >> nth=0").isChecked(),
            Uncheck.checkbox("#checkboxes input >> nth=0"),
            Ensure.that("#checkboxes input >> nth=0").isNotChecked()
        );
    }
}
