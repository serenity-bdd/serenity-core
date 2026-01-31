package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for alert/dialog interactions - Accept, Dismiss, Enter text.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class AlertInteractionsTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_accept_alert() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/javascript_alerts"),
            // Set up dialog handler before triggering
            HandleDialog.byAccepting(),
            Click.on("button[onclick='jsAlert()']")
        );

        // Verify the result shows alert was accepted
        String result = alice.asksFor(
            net.serenitybdd.screenplay.playwright.questions.Text.of("#result")
        );
        assertThat(result).isEqualTo("You successfully clicked an alert");
    }

    @Test
    void should_dismiss_confirm_dialog() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/javascript_alerts"),
            HandleDialog.byDismissing(),
            Click.on("button[onclick='jsConfirm()']")
        );

        String result = alice.asksFor(
            net.serenitybdd.screenplay.playwright.questions.Text.of("#result")
        );
        assertThat(result).isEqualTo("You clicked: Cancel");
    }

    @Test
    void should_accept_confirm_dialog() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/javascript_alerts"),
            HandleDialog.byAccepting(),
            Click.on("button[onclick='jsConfirm()']")
        );

        String result = alice.asksFor(
            net.serenitybdd.screenplay.playwright.questions.Text.of("#result")
        );
        assertThat(result).isEqualTo("You clicked: Ok");
    }

    @Test
    void should_enter_text_in_prompt() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/javascript_alerts"),
            HandleDialog.byEntering("Hello World"),
            Click.on("button[onclick='jsPrompt()']")
        );

        String result = alice.asksFor(
            net.serenitybdd.screenplay.playwright.questions.Text.of("#result")
        );
        assertThat(result).isEqualTo("You entered: Hello World");
    }
}
