package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.questions.JavaScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JavaScript execution and evaluation.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class JavaScriptTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_execute_javascript() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            ExecuteJavaScript.async("document.title = 'Changed Title'")
        );

        String title = alice.asksFor(
            net.serenitybdd.screenplay.playwright.questions.TheWebPage.title()
        );
        assertThat(title).isEqualTo("Changed Title");
    }

    @Test
    void should_execute_javascript_with_arguments() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            ExecuteJavaScript.async("([a, b]) => document.title = a + ' ' + b")
                .withArguments("Hello", "World")
        );

        String title = alice.asksFor(
            net.serenitybdd.screenplay.playwright.questions.TheWebPage.title()
        );
        assertThat(title).isEqualTo("Hello World");
    }

    @Test
    void should_evaluate_javascript_returning_string() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        String userAgent = alice.asksFor(
            JavaScript.evaluate("navigator.userAgent")
        );
        assertThat(userAgent).isNotEmpty();
    }

    @Test
    void should_evaluate_javascript_returning_number() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        Object result = alice.asksFor(
            JavaScript.evaluate("2 + 2")
        );
        assertThat(result).isEqualTo(4);
    }

    @Test
    void should_evaluate_javascript_with_arguments() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        Object result = alice.asksFor(
            JavaScript.evaluate("([a, b]) => a * b")
                .withArguments(6, 7)
        );
        assertThat(result).isEqualTo(42);
    }

    @Test
    void should_scroll_element_into_view() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            ScrollIntoView.element("a[href='/status_codes']")
        );
        // Test passes if no error occurs
    }
}
