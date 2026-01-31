package net.serenitybdd.screenplay.playwright.browsing;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.interactions.SwitchTo;
import net.serenitybdd.screenplay.playwright.questions.PageCount;
import net.serenitybdd.screenplay.playwright.questions.TheWebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for multiple page (tab) handling.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class MultiplePageTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_open_new_page_and_navigate() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            SwitchTo.newPageAt("https://the-internet.herokuapp.com/login")
        );

        // Should now have 2 pages open
        Integer pageCount = alice.asksFor(PageCount.inTheBrowser());
        assertThat(pageCount).isEqualTo(2);

        // Current page should be the login page
        String currentUrl = alice.asksFor(TheWebPage.currentUrl());
        assertThat(currentUrl).contains("login");
    }

    @Test
    void should_switch_between_pages_by_index() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            SwitchTo.newPageAt("https://the-internet.herokuapp.com/login"),
            SwitchTo.pageNumber(0)  // Switch back to first page
        );

        String currentUrl = alice.asksFor(TheWebPage.currentUrl());
        assertThat(currentUrl).doesNotContain("login");
    }

    @Test
    void should_switch_to_page_by_url() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes"),
            SwitchTo.newPageAt("https://the-internet.herokuapp.com/login"),
            SwitchTo.pageWithUrl("checkboxes")  // Switch back to first page by partial URL
        );

        // Verify we're on the checkboxes page
        String currentUrl = alice.asksFor(TheWebPage.currentUrl());
        assertThat(currentUrl).contains("checkboxes");
    }

    @Test
    void should_close_current_page_and_switch_back() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            SwitchTo.newPageAt("https://the-internet.herokuapp.com/login"),
            SwitchTo.previousPageAfterClosingCurrent()
        );

        // Should now have 1 page open
        Integer pageCount = alice.asksFor(PageCount.inTheBrowser());
        assertThat(pageCount).isEqualTo(1);

        // Should be back on the home page
        String currentUrl = alice.asksFor(TheWebPage.currentUrl());
        assertThat(currentUrl).doesNotContain("login");
    }
}
