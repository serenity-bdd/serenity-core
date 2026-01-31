package net.serenitybdd.screenplay.playwright.browsing;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.ManageCookies;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.CookieValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for cookie management.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class CookieManagementTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_add_a_cookie() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            ManageCookies.addCookie("test_cookie", "test_value")
        );

        String cookieValue = alice.asksFor(CookieValue.of("test_cookie"));
        assertThat(cookieValue).isEqualTo("test_value");
    }

    @Test
    void should_check_if_cookie_exists() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            ManageCookies.addCookie("existing_cookie", "some_value")
        );

        Boolean exists = alice.asksFor(CookieValue.exists("existing_cookie"));
        assertThat(exists).isTrue();

        Boolean notExists = alice.asksFor(CookieValue.exists("non_existing_cookie"));
        assertThat(notExists).isFalse();
    }

    @Test
    void should_clear_all_cookies() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            ManageCookies.addCookie("cookie1", "value1"),
            ManageCookies.addCookie("cookie2", "value2"),
            ManageCookies.clearAll()
        );

        Boolean cookie1Exists = alice.asksFor(CookieValue.exists("cookie1"));
        Boolean cookie2Exists = alice.asksFor(CookieValue.exists("cookie2"));

        assertThat(cookie1Exists).isFalse();
        assertThat(cookie2Exists).isFalse();
    }

    @Test
    void should_add_cookie_with_options() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            ManageCookies.addCookie("session_cookie", "session_value")
                .withPath("/")
                .httpOnly()
        );

        String cookieValue = alice.asksFor(CookieValue.of("session_cookie"));
        assertThat(cookieValue).isEqualTo("session_value");
    }
}
