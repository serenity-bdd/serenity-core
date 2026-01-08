package net.serenitybdd.playwright.demo.steps;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.playwright.demo.pages.ExampleDomainPage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step library for example.com interactions.
 * <p>
 * A simple example showing the Page Object pattern with a static page.
 * </p>
 */
public class ExampleDomainSteps {

    private ExampleDomainPage examplePage;

    private void ensurePageObject(Page page) {
        if (examplePage == null) {
            examplePage = new ExampleDomainPage(page);
        }
    }

    @Step("Open example.com")
    public void openExampleDomain(Page page) {
        ensurePageObject(page);
        examplePage.open();
    }

    @Step("Verify the page heading is visible")
    public void verifyHeadingIsVisible(Page page) {
        ensurePageObject(page);
        assertThat(examplePage.getHeadingText())
                .as("Page heading")
                .isEqualTo("Example Domain");
    }

    @Step("Verify page title contains '{1}'")
    public void verifyTitleContains(Page page, String expectedText) {
        ensurePageObject(page);
        assertThat(examplePage.getTitle())
                .as("Page title")
                .containsIgnoringCase(expectedText);
    }

    @Step("Verify the page is displayed correctly")
    public void verifyPageIsDisplayed(Page page) {
        ensurePageObject(page);
        assertThat(examplePage.isDisplayed())
                .as("Example Domain page should be displayed")
                .isTrue();
    }

    @Step("Click the 'More information' link")
    public void clickMoreInfo(Page page) {
        ensurePageObject(page);
        examplePage.clickMoreInfo();
    }
}
