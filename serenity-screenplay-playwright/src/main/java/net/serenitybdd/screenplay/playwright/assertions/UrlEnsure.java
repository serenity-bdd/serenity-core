package net.serenitybdd.screenplay.playwright.assertions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.regex.Pattern;

/**
 * URL assertions using Playwright's page assertions.
 */
public class UrlEnsure {
    private final Double timeout;

    UrlEnsure(Double timeout) {
        this.timeout = timeout;
    }

    public UrlEnsure withTimeout(Double timeout) {
        return new UrlEnsure(timeout);
    }

    private PageAssertions.HasURLOptions urlOptions() {
        return timeout != null ? new PageAssertions.HasURLOptions().setTimeout(timeout) : null;
    }

    /**
     * Check that the URL contains the specified substring.
     */
    public Performable contains(String substring) {
        return Task.where("the current URL should contain '" + substring + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                String url = page.url();
                if (!url.contains(substring)) {
                    throw new AssertionError("Expected URL to contain '" + substring + "' but was: " + url);
                }
            }
        );
    }

    /**
     * Check that the URL matches the expected value exactly.
     */
    public Performable isEqualTo(String expectedUrl) {
        return Task.where("the current URL should be '" + expectedUrl + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                PlaywrightAssertions.assertThat(page).hasURL(expectedUrl, urlOptions());
            }
        );
    }

    /**
     * Check that the URL matches the specified pattern.
     */
    public Performable matches(Pattern pattern) {
        return Task.where("the current URL should match pattern '" + pattern.pattern() + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                PlaywrightAssertions.assertThat(page).hasURL(pattern, urlOptions());
            }
        );
    }
}
