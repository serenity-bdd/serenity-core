package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.questions.TheWebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for browser navigation interactions.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class NavigationTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_navigate_back() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Click.on("text='A/B Testing'")
        );

        String urlAfterClick = alice.asksFor(TheWebPage.currentUrl());
        assertThat(urlAfterClick).contains("/abtest");

        alice.attemptsTo(Navigate.back());

        String urlAfterBack = alice.asksFor(TheWebPage.currentUrl());
        assertThat(urlAfterBack).doesNotContain("/abtest");
    }

    @Test
    void should_navigate_forward() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Click.on("text='A/B Testing'"),
            Navigate.back()
        );

        String urlAfterBack = alice.asksFor(TheWebPage.currentUrl());
        assertThat(urlAfterBack).doesNotContain("/abtest");

        alice.attemptsTo(Navigate.forward());

        String urlAfterForward = alice.asksFor(TheWebPage.currentUrl());
        assertThat(urlAfterForward).contains("/abtest");
    }

    @Test
    void should_refresh_page() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Navigate.refresh()
        );

        // If we got here without error, refresh worked
        String title = alice.asksFor(TheWebPage.title());
        assertThat(title).isNotEmpty();
    }
}
