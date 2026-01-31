package net.serenitybdd.screenplay.playwright.waits;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for wait strategies using Playwright's native waiting mechanisms.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class WaitStrategiesTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_wait_for_element_to_be_visible() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/2"),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isVisible()
        );

        String text = alice.asksFor(Text.of("#finish h4"));
        assertThat(text).isEqualTo("Hello World!");
    }

    @Test
    void should_wait_for_element_to_be_hidden() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/1"),
            Click.on("#start button"),
            WaitUntil.the("#loading").isHidden()
        );

        // After loading is hidden, finish should be visible
        alice.attemptsTo(
            Ensure.that("#finish h4").isVisible()
        );
    }

    @Test
    void should_wait_with_custom_timeout() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/2"),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isVisible().forNoMoreThan(Duration.ofSeconds(30))
        );

        String text = alice.asksFor(Text.of("#finish h4"));
        assertThat(text).isEqualTo("Hello World!");
    }

    @Test
    void should_wait_for_element_to_be_attached() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/2"),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isAttached()
        );
    }

    @Test
    void should_wait_for_element_to_be_enabled() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_controls"),
            Click.on("#input-example button"),
            // The enable animation takes several seconds
            WaitUntil.the("#input-example input").isEnabled().forNoMoreThan(Duration.ofSeconds(15))
        );
    }

    @Test
    void should_wait_for_page_load() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            WaitUntil.thePageIsLoaded()
        );
    }

    @Test
    void should_wait_for_network_idle() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            WaitUntil.networkIsIdle()
        );
    }

    @Test
    void should_wait_for_url_to_contain() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Click.on("a[href='/abtest']"),
            WaitUntil.theUrl().contains("/abtest")
        );
    }

    @Test
    void should_wait_for_element_to_contain_text() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/dynamic_loading/2"),
            Click.on("#start button"),
            // First wait for element to be visible, then check text
            WaitUntil.the("#finish h4").isVisible(),
            Ensure.that("#finish h4").containsText("Hello")
        );
    }
}
