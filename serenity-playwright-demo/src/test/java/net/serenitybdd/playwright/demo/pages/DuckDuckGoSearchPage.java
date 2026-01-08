package net.serenitybdd.playwright.demo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object for DuckDuckGo search.
 * <p>
 * Demonstrates a Page Object for a search engine with both
 * home page and results page interactions.
 * </p>
 */
public class DuckDuckGoSearchPage {

    private final Page page;

    private static final String URL = "https://duckduckgo.com";

    public DuckDuckGoSearchPage(Page page) {
        this.page = page;
    }

    // ========== Locators (private) ==========

    private Locator searchBox() {
        // DuckDuckGo's search input
        return page.locator("input[name='q']");
    }

    private Locator searchResults() {
        // Results are in article elements or list items
        return page.locator("article, [data-result]");
    }

    private Locator resultsContainer() {
        // The main results area
        return page.locator("#react-layout, #links");
    }

    // ========== Actions ==========

    public void open() {
        page.navigate(URL);
        page.waitForLoadState();
    }

    public void searchFor(String term) {
        searchBox().fill(term);
        searchBox().press("Enter");
        waitForResults();
    }

    public void waitForResults() {
        // Wait for page to load after search
        page.waitForLoadState();
        // Wait for URL to change (indicates search was performed)
        page.waitForURL(url -> url.contains("q="));
    }

    // ========== Queries ==========

    public String getTitle() {
        return page.title();
    }

    public boolean hasResults() {
        // Check if we're on a search results page
        return page.url().contains("q=") && page.content().length() > 1000;
    }

    public boolean resultsContain(String text) {
        return page.content().toLowerCase().contains(text.toLowerCase());
    }
}
