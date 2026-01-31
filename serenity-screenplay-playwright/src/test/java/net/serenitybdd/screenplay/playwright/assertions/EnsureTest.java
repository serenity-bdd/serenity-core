package net.serenitybdd.screenplay.playwright.assertions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.regex.Pattern;

/**
 * Tests for enhanced Ensure assertions.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class EnsureTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_ensure_text_content() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Ensure.that("h1").hasText("Welcome to the-internet"),
            Ensure.that("h1").containsText("Welcome"),
            Ensure.that("h1").hasTextMatching("Welcome.*internet")
        );
    }

    @Test
    void should_ensure_text_matching_with_pattern() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Ensure.that("h1").hasTextMatching(Pattern.compile("Welcome.*", Pattern.CASE_INSENSITIVE))
        );
    }

    @Test
    void should_ensure_attribute_value() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Ensure.that("#username").hasAttribute("name", "username"),
            Ensure.that("#username").hasAttribute("type", "text")
        );
    }

    @Test
    void should_ensure_attribute_contains() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Ensure.that("form").hasAttributeContaining("action", "authenticate")
        );
    }

    @Test
    void should_ensure_element_has_class() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/add_remove_elements/"),
            Ensure.that("#content .example").hasClass("example")
        );
    }

    @Test
    void should_ensure_element_count() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes"),
            Ensure.that("#checkboxes input").hasCount(2)
        );
    }

    @Test
    void should_ensure_url_contains() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Ensure.thatTheCurrentUrl().contains("/login")
        );
    }

    @Test
    void should_ensure_url_matches() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Ensure.thatTheCurrentUrl().matches(Pattern.compile(".*/login$"))
        );
    }

    @Test
    void should_ensure_page_title() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Ensure.thatThePageTitle().isEqualTo("The Internet"),
            Ensure.thatThePageTitle().contains("Internet")
        );
    }

    @Test
    void should_ensure_element_is_focused() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            net.serenitybdd.screenplay.playwright.interactions.Click.on("#username"),
            Ensure.that("#username").isFocused()
        );
    }

    @Test
    void should_ensure_element_is_not_empty() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Ensure.that("h1").isNotEmpty()
        );
    }

    @Test
    void should_ensure_input_is_empty() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Ensure.that("#username").isEmpty()
        );
    }

    @Test
    void should_ensure_css_value() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Ensure.that("h1").hasCSSValue("display", "block")
        );
    }
}
