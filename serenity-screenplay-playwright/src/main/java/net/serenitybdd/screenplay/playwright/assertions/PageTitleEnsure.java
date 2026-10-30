package net.serenitybdd.screenplay.playwright.assertions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.regex.Pattern;

/**
 * Page title assertions using Playwright's page assertions.
 */
public class PageTitleEnsure {
    private final Double timeout;

    PageTitleEnsure(Double timeout) {
        this.timeout = timeout;
    }

    public PageTitleEnsure withTimeout(Double timeout) {
        return new PageTitleEnsure(timeout);
    }

    private PageAssertions.HasTitleOptions titleOptions() {
        return timeout != null ? new PageAssertions.HasTitleOptions().setTimeout(timeout) : null;
    }

    /**
     * Check that the page title equals the expected value.
     */
    public Performable isEqualTo(String expectedTitle) {
        return Task.where("the page title should be '" + expectedTitle + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                PlaywrightAssertions.assertThat(page).hasTitle(expectedTitle, titleOptions());
            }
        );
    }

    /**
     * Check that the page title contains the specified substring.
     */
    public Performable contains(String substring) {
        return Task.where("the page title should contain '" + substring + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                String title = page.title();
                if (!title.contains(substring)) {
                    throw new AssertionError("Expected page title to contain '" + substring + "' but was: " + title);
                }
            }
        );
    }

    /**
     * Check that the page title matches the specified pattern.
     */
    public Performable matches(Pattern pattern) {
        return Task.where("the page title should match pattern '" + pattern.pattern() + "'",
            actor -> {
                Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
                PlaywrightAssertions.assertThat(page).hasTitle(pattern, titleOptions());
            }
        );
    }
}
