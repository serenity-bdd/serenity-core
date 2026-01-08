package net.serenitybdd.playwright.demo.steps;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import net.serenitybdd.annotations.Step;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step library for search-related actions.
 * <p>
 * Demonstrates form interactions with Playwright and Serenity.
 */
public class SearchSteps {

    @Step("Search for '{1}'")
    public void searchFor(Page page, String searchTerm) {
        // Find search input and type the search term
        page.getByRole(AriaRole.SEARCHBOX).first().fill(searchTerm);
        page.getByRole(AriaRole.SEARCHBOX).first().press("Enter");
    }

    @Step("Enter search term '{1}' in the search box")
    public void enterSearchTerm(Page page, String searchTerm) {
        page.getByRole(AriaRole.SEARCHBOX).first().fill(searchTerm);
    }

    @Step("Submit the search")
    public void submitSearch(Page page) {
        page.getByRole(AriaRole.SEARCHBOX).first().press("Enter");
    }

    @Step("Verify search results contain '{1}'")
    public void verifySearchResultsContain(Page page, String expectedText) {
        String pageContent = page.content();
        assertThat(pageContent).containsIgnoringCase(expectedText);
    }

    @Step("Verify page title contains '{1}'")
    public void verifyTitleContains(Page page, String expectedText) {
        assertThat(page.title()).containsIgnoringCase(expectedText);
    }
}
