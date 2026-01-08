package net.serenitybdd.playwright.demo.steps;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.playwright.demo.pages.WikipediaHomePage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step library for Wikipedia interactions.
 * <p>
 * This step library demonstrates the proper separation of concerns:
 * <ul>
 *   <li>Page Objects handle locators and raw page interactions</li>
 *   <li>Step libraries handle reporting and test orchestration</li>
 *   <li>Tests describe business scenarios using steps</li>
 * </ul>
 * </p>
 * <p>
 * Notice that this class has NO knowledge of CSS selectors, XPath,
 * or any locator strategies. All that is encapsulated in WikipediaHomePage.
 * </p>
 */
public class WikipediaSteps {

    private WikipediaHomePage wikipediaHomePage;

    private void ensurePageObject(Page page) {
        if (wikipediaHomePage == null) {
            wikipediaHomePage = new WikipediaHomePage(page);
        }
    }

    @Step("Open Wikipedia")
    public void openWikipedia(Page page) {
        ensurePageObject(page);
        wikipediaHomePage.open();
    }

    @Step("Search Wikipedia for '{1}'")
    public void searchFor(Page page, String searchTerm) {
        ensurePageObject(page);
        wikipediaHomePage.searchFor(searchTerm);
    }

    @Step("Enter search term '{1}'")
    public void enterSearchTerm(Page page, String searchTerm) {
        ensurePageObject(page);
        wikipediaHomePage.enterSearchTerm(searchTerm);
    }

    @Step("Submit the search")
    public void submitSearch(Page page) {
        ensurePageObject(page);
        wikipediaHomePage.clickSearch();
    }

    @Step("Verify Wikipedia home page is displayed")
    public void verifyHomePageIsDisplayed(Page page) {
        ensurePageObject(page);
        assertThat(wikipediaHomePage.isDisplayed())
                .as("Wikipedia home page should be displayed")
                .isTrue();
    }

    @Step("Verify page title contains '{1}'")
    public void verifyTitleContains(Page page, String expectedText) {
        ensurePageObject(page);
        assertThat(wikipediaHomePage.getTitle())
                .as("Page title")
                .containsIgnoringCase(expectedText);
    }
}
