package net.serenitybdd.playwright.demo.steps;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.playwright.PlaywrightSerenity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step library that uses PlaywrightSerenity.getCurrentPage() to access the page.
 * <p>
 * This approach is useful when you don't want to pass the Page object to every method.
 * The page must be registered via PlaywrightSerenity.registerPage() before using these steps.
 * </p>
 */
public class PageAwareSteps {

    /**
     * Gets the current page from the Playwright Serenity registry.
     */
    private Page getPage() {
        Page page = PlaywrightSerenity.getCurrentPage();
        if (page == null) {
            throw new IllegalStateException(
                    "No Playwright page registered. Call PlaywrightSerenity.registerPage(page) first.");
        }
        return page;
    }

    @Step("Open the URL: {0}")
    public void openUrl(String url) {
        getPage().navigate(url);
    }

    @Step("Click on link with text '{0}'")
    public void clickLinkWithText(String linkText) {
        getPage().getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName(linkText)).click();
    }

    @Step("Verify page title is '{0}'")
    public void verifyPageTitle(String expectedTitle) {
        assertThat(getPage().title())
                .as("Page title")
                .isEqualTo(expectedTitle);
    }

    @Step("Verify page title contains '{0}'")
    public void verifyPageTitleContains(String expectedText) {
        assertThat(getPage().title())
                .as("Page title")
                .containsIgnoringCase(expectedText);
    }

    @Step("Verify page URL contains '{0}'")
    public void verifyUrlContains(String expectedText) {
        assertThat(getPage().url())
                .as("Page URL")
                .containsIgnoringCase(expectedText);
    }

    @Step("Type '{1}' into field with placeholder '{0}'")
    public void typeIntoFieldWithPlaceholder(String placeholder, String text) {
        getPage().getByPlaceholder(placeholder).fill(text);
    }

    @Step("Take a screenshot now")
    public void takeScreenshotNow() {
        PlaywrightSerenity.takeScreenshot();
    }

    @Step("Wait for {0} milliseconds")
    public void waitFor(int milliseconds) {
        getPage().waitForTimeout(milliseconds);
    }
}
