package net.serenitybdd.screenplay.playwright.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.TheWebPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests verifying that Screenplay actors can wrap a {@code @UsePlaywright}-provided
 * Page via {@link BrowseTheWebWithPlaywright#withPage(Page)}, reusing the external browser
 * session instead of creating a duplicate.
 */
@UsePlaywright
@DisplayName("@UsePlaywright + Screenplay integration")
public class WhenUsingUsePlaywrightWithScreenplayIT {

    Actor alice;

    @BeforeEach
    void setup(Page page) {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.withPage(page));
    }

    @AfterEach
    void cleanup() {
        if (alice != null) {
            alice.wrapUp();
        }
    }

    @Test
    @DisplayName("Actor can use standard interactions with an external page")
    void actor_can_use_standard_interactions(Page page) {
        alice.attemptsTo(Open.url("https://example.com"));

        String title = alice.asksFor(TheWebPage.title());
        assertThat(title).containsIgnoringCase("example");
    }

    @Test
    @DisplayName("getCurrentPage() returns the injected page")
    void getCurrentPage_returns_injected_page(Page page) {
        Page currentPage = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
        assertThat(currentPage).isSameAs(page);
    }

    @Test
    @DisplayName("getBrowser() returns the @UsePlaywright browser")
    void getBrowser_returns_external_browser(Page page) {
        Browser browser = BrowseTheWebWithPlaywright.as(alice).getBrowser();

        assertThat(browser).isNotNull();
        assertThat(browser.isConnected()).isTrue();
        // The browser should be the same one that owns the injected page
        assertThat(browser).isSameAs(page.context().browser());
    }

    @Test
    @DisplayName("Cookie operations work via derived context")
    void cookie_operations_work_via_derived_context(Page page) {
        alice.attemptsTo(Open.url("https://example.com"));

        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(alice);

        // Clear any existing cookies
        ability.clearCookies();
        assertThat(ability.getCookies()).isEmpty();

        // Add a cookie and verify it's accessible
        var cookie = new com.microsoft.playwright.options.Cookie("test-cookie", "test-value")
            .setDomain("example.com")
            .setPath("/");
        ability.addCookie(cookie);

        assertThat(ability.getCookies())
            .anyMatch(c -> "test-cookie".equals(c.name) && "test-value".equals(c.value));
    }

    @Test
    @DisplayName("Teardown does not close the external page")
    void teardown_does_not_close_external_page(Page page) {
        alice.attemptsTo(Open.url("https://example.com"));

        // Trigger teardown via wrapUp
        alice.wrapUp();

        // The external page should still be open — @UsePlaywright owns its lifecycle
        assertThat(page.isClosed())
            .as("External page should NOT be closed by Screenplay teardown")
            .isFalse();

        // The page should be unregistered from PlaywrightSerenity
        assertThat(PlaywrightSerenity.getRegisteredPages())
            .as("Page should be unregistered from PlaywrightSerenity after teardown")
            .doesNotContain(page);

        // Prevent double wrapUp in @AfterEach
        alice = null;
    }
}
