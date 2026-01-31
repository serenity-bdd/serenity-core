package net.serenitybdd.screenplay.playwright.abilities;

import com.microsoft.playwright.Page;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HasTeardown;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that BrowseTheWebWithPlaywright properly implements HasTeardown
 * for resource cleanup.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class TeardownTest {

    @Test
    void ability_should_implement_has_teardown() {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.usingTheDefaultConfiguration();

        assertThat(ability)
            .as("BrowseTheWebWithPlaywright should implement HasTeardown")
            .isInstanceOf(HasTeardown.class);
    }

    @Test
    void ability_should_cleanup_resources_on_teardown() {
        Actor alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        // Create a page by opening a URL
        alice.attemptsTo(Open.url("https://example.com"));

        Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
        assertThat(page.isClosed()).isFalse();

        // Get the ability and call teardown directly
        BrowseTheWebWithPlaywright ability = alice.abilityTo(BrowseTheWebWithPlaywright.class);
        ((HasTeardown) ability).tearDown();

        // Page should be closed after teardown
        assertThat(page.isClosed())
            .as("Page should be closed after tearDown()")
            .isTrue();
    }

    @Test
    void wrapUp_should_trigger_teardown() {
        Actor alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        alice.attemptsTo(Open.url("https://example.com"));
        Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();

        // wrapUp() should call tearDown() on all abilities with HasTeardown
        alice.wrapUp();

        assertThat(page.isClosed())
            .as("Page should be closed after actor.wrapUp()")
            .isTrue();
    }

    @Test
    void teardown_should_unregister_page_from_playwright_serenity() {
        Actor alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

        alice.attemptsTo(Open.url("https://example.com"));

        // Verify page is registered
        assertThat(PlaywrightSerenity.getRegisteredPages()).isNotEmpty();

        // Teardown
        alice.wrapUp();

        // Page should be unregistered
        assertThat(PlaywrightSerenity.getRegisteredPages())
            .as("Page should be unregistered from PlaywrightSerenity after teardown")
            .isEmpty();
    }
}