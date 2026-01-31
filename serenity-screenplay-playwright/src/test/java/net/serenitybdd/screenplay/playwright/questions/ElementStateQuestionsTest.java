package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for element state questions - presence, enabled state, count.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class ElementStateQuestionsTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_check_element_presence() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        Boolean present = alice.asksFor(Presence.of("h1"));
        Boolean absent = alice.asksFor(Presence.of(".nonexistent-element"));

        assertThat(present).isTrue();
        assertThat(absent).isFalse();
    }

    @Test
    void should_check_element_enabled_state() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_controls")
        );

        // The input is initially disabled
        Boolean enabled = alice.asksFor(Enabled.of("#input-example input"));
        assertThat(enabled).isFalse();
    }

    @Test
    void should_check_element_disabled_state() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_controls")
        );

        // The input is initially disabled
        Boolean disabled = alice.asksFor(Disabled.of("#input-example input"));
        assertThat(disabled).isTrue();
    }

    @Test
    void should_get_element_count() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes")
        );

        Integer count = alice.asksFor(Count.of("#checkboxes input"));
        assertThat(count).isEqualTo(2);
    }

    @Test
    void should_get_zero_count_for_missing_elements() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        Integer count = alice.asksFor(Count.of(".nonexistent-element"));
        assertThat(count).isEqualTo(0);
    }

    @Test
    void should_check_if_element_is_checked() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes")
        );

        // Second checkbox is checked by default
        Boolean firstChecked = alice.asksFor(Checked.of("#checkboxes input >> nth=0"));
        Boolean secondChecked = alice.asksFor(Checked.of("#checkboxes input >> nth=1"));

        assertThat(firstChecked).isFalse();
        assertThat(secondChecked).isTrue();
    }

    @Test
    void should_check_if_element_is_focused() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Click.on("#username")
        );

        Boolean focused = alice.asksFor(Focused.of("#username"));
        assertThat(focused).isTrue();
    }

    @Test
    void should_check_if_element_is_editable() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login")
        );

        Boolean editable = alice.asksFor(Editable.of("#username"));
        assertThat(editable).isTrue();
    }
}
