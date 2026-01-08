package net.serenitybdd.playwright.demo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object for the Wikipedia home page.
 * <p>
 * This class encapsulates all locator strategies and page-specific actions.
 * Tests and step libraries should never need to know about CSS selectors,
 * XPath expressions, or other locator details.
 * </p>
 * <p>
 * Key principles:
 * <ul>
 *   <li>Locators are private - only this class knows how to find elements</li>
 *   <li>Actions are public - expose meaningful user interactions</li>
 *   <li>Queries are public - expose ways to check page state</li>
 * </ul>
 * </p>
 */
public class WikipediaHomePage {

    private final Page page;

    private static final String URL = "https://en.wikipedia.org";

    public WikipediaHomePage(Page page) {
        this.page = page;
    }

    // ========== Locators (private - encapsulated) ==========

    private Locator searchBox() {
        return page.getByRole(AriaRole.SEARCHBOX,
                new Page.GetByRoleOptions().setName("Search Wikipedia"));
    }

    private Locator searchButton() {
        return page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Search"));
    }

    private Locator mainLogo() {
        // Wikipedia's main logo/wordmark on the home page
        return page.locator(".central-textlogo, .wikipedia-wordmark, img[alt*='Wikipedia']");
    }

    // ========== Actions (public) ==========

    /**
     * Navigate to the Wikipedia home page.
     */
    public void open() {
        page.navigate(URL);
    }

    /**
     * Enter a search term and submit the search.
     */
    public void searchFor(String term) {
        searchBox().fill(term);
        searchBox().press("Enter");
    }

    /**
     * Enter a search term without submitting.
     */
    public void enterSearchTerm(String term) {
        searchBox().fill(term);
    }

    /**
     * Click the search button to submit.
     */
    public void clickSearch() {
        searchButton().click();
    }

    // ========== Queries (public) ==========

    /**
     * Get the page title.
     */
    public String getTitle() {
        return page.title();
    }

    /**
     * Check if the main logo is visible.
     */
    public boolean isLogoVisible() {
        return mainLogo().isVisible();
    }

    /**
     * Check if we're on the Wikipedia home page.
     */
    public boolean isDisplayed() {
        return page.url().contains("wikipedia.org") &&
               page.title().toLowerCase().contains("wikipedia");
    }
}
