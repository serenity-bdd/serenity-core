package net.serenitybdd.screenplay.playwright.integration;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that serenity-screenplay-playwright properly integrates with serenity-playwright module.
 * The PlaywrightSerenity registry should automatically see pages created by the Screenplay ability.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class ModuleIntegrationTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void should_register_page_with_playwright_serenity_when_page_is_created() {
        // When the actor opens a URL (which creates a page)
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        // Then the page should be registered with PlaywrightSerenity
        assertThat(PlaywrightSerenity.getRegisteredPages())
            .as("Page should be registered with PlaywrightSerenity")
            .isNotEmpty();
    }

    @Test
    void should_be_able_to_access_current_page_via_playwright_serenity() {
        alice.attemptsTo(
            Open.url("https://example.com")
        );

        // The current page from the ability should match the one in PlaywrightSerenity
        var abilityPage = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
        var registryPage = PlaywrightSerenity.getCurrentPage();

        assertThat(registryPage)
            .as("PlaywrightSerenity.getCurrentPage() should return the actor's current page")
            .isEqualTo(abilityPage);
    }
}