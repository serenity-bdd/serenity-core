package net.serenitybdd.playwright.demo.steps;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.playwright.demo.pages.DuckDuckGoSearchPage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step library for DuckDuckGo search interactions.
 * <p>
 * Demonstrates Page Object usage with a search engine.
 * </p>
 */
public class DuckDuckGoSteps {

    private DuckDuckGoSearchPage searchPage;

    private void ensurePageObject(Page page) {
        if (searchPage == null) {
            searchPage = new DuckDuckGoSearchPage(page);
        }
    }

    @Step("Open DuckDuckGo")
    public void openDuckDuckGo(Page page) {
        ensurePageObject(page);
        searchPage.open();
    }

    @Step("Search for '{1}'")
    public void searchFor(Page page, String searchTerm) {
        ensurePageObject(page);
        searchPage.searchFor(searchTerm);
    }

    @Step("Verify search results are displayed")
    public void verifyResultsAreDisplayed(Page page) {
        ensurePageObject(page);
        assertThat(searchPage.hasResults())
                .as("Search results should be displayed")
                .isTrue();
    }

    @Step("Verify results contain '{1}'")
    public void verifyResultsContain(Page page, String expectedText) {
        ensurePageObject(page);
        assertThat(searchPage.resultsContain(expectedText))
                .as("Search results should contain '%s'", expectedText)
                .isTrue();
    }
}
