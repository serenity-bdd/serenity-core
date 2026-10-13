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
 * Uses local HTML test fixtures to avoid any dependency on external sites.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class WaitStrategiesTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    private String localPage(String name) {
        java.net.URL resource = getClass().getClassLoader().getResource("test-pages/" + name);
        if (resource == null) {
            throw new IllegalStateException("Test page not found on classpath: test-pages/" + name);
        }
        return resource.toExternalForm();
    }

    @Test
    void should_wait_for_element_to_be_visible() {
        alice.attemptsTo(
            Open.url(localPage("dynamic-loading.html")),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isVisible()
        );

        String text = alice.asksFor(Text.of("#finish h4"));
        assertThat(text).isEqualTo("Hello World!");
    }

    @Test
    void should_wait_for_element_to_be_hidden() {
        alice.attemptsTo(
            Open.url(localPage("dynamic-loading.html")),
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
            Open.url(localPage("dynamic-loading.html")),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isVisible().forNoMoreThan(Duration.ofSeconds(10))
        );

        String text = alice.asksFor(Text.of("#finish h4"));
        assertThat(text).isEqualTo("Hello World!");
    }

    @Test
    void should_wait_for_element_to_be_attached() {
        alice.attemptsTo(
            Open.url(localPage("dynamic-loading.html")),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isAttached()
        );
    }

    @Test
    void should_wait_for_element_to_be_enabled() {
        alice.attemptsTo(
            Open.url(localPage("dynamic-controls.html")),
            Click.on("#input-example button"),
            WaitUntil.the("#input-example input").isEnabled().forNoMoreThan(Duration.ofSeconds(10))
        );
    }

    @Test
    void should_wait_for_page_load() {
        alice.attemptsTo(
            Open.url(localPage("index.html")),
            WaitUntil.thePageIsLoaded()
        );
    }

    @Test
    void should_wait_for_network_idle() {
        alice.attemptsTo(
            Open.url(localPage("index.html")),
            WaitUntil.networkIsIdle()
        );
    }

    @Test
    void should_wait_for_url_to_contain() {
        alice.attemptsTo(
            Open.url(localPage("index.html")),
            Click.on("a[href='abtest.html']"),
            WaitUntil.theUrl().contains("abtest")
        );
    }

    @Test
    void should_wait_for_element_to_contain_text() {
        alice.attemptsTo(
            Open.url(localPage("dynamic-loading.html")),
            Click.on("#start button"),
            WaitUntil.the("#finish h4").isVisible(),
            Ensure.that("#finish h4").containsText("Hello")
        );
    }
}
