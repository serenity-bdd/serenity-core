package net.serenitybdd.playwright.integration;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.playwright.PlaywrightSerenity;

/**
 * Sample step library demonstrating Playwright integration with Serenity.
 * <p>
 * Screenshots are automatically captured after each @Step method completes.
 * </p>
 */
public class SamplePlaywrightSteps {

    @Step("Navigate to {0}")
    public void navigateTo(String url) {
        Page page = PlaywrightSerenity.getCurrentPage();
        page.navigate(url);
    }

    @Step("Navigate to {0}")
    public void navigateTo(Page page, String url) {
        page.navigate(url);
    }

    @Step("Enter '{1}' into the search field")
    public void searchFor(Page page, String query) {
        // Example interaction with a search field
        page.fill("input[name='q'], input[type='search'], #search", query);
    }

    @Step("Click on element: {0}")
    public void clickOn(Page page, String selector) {
        page.click(selector);
    }

    @Step("Verify page title contains '{0}'")
    public void verifyTitleContains(Page page, String expectedText) {
        String title = page.title();
        if (!title.contains(expectedText)) {
            throw new AssertionError("Expected title to contain '" + expectedText + "' but was '" + title + "'");
        }
    }

    @Step("Verify page has loaded")
    public void verifyPageLoaded(Page page) {
        page.waitForLoadState();
    }

    @Step("Take a manual screenshot")
    public void takeScreenshot() {
        PlaywrightSerenity.takeScreenshot();
    }
}
