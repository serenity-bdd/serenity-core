package net.serenitybdd.playwright.demo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for example.com - a simple demonstration page.
 * <p>
 * This is the simplest possible Page Object, demonstrating
 * the pattern with a static page that has minimal interaction.
 * </p>
 */
public class ExampleDomainPage {

    private final Page page;

    private static final String URL = "https://www.example.com";

    public ExampleDomainPage(Page page) {
        this.page = page;
    }

    // ========== Locators ==========

    private Locator heading() {
        return page.locator("h1");
    }

    private Locator moreInfoLink() {
        return page.locator("a");
    }

    private Locator mainContent() {
        return page.locator("div > p");
    }

    // ========== Actions ==========

    public void open() {
        page.navigate(URL);
    }

    public void clickMoreInfo() {
        moreInfoLink().click();
    }

    // ========== Queries ==========

    public String getTitle() {
        return page.title();
    }

    public String getHeadingText() {
        return heading().textContent();
    }

    public String getContentText() {
        return mainContent().textContent();
    }

    public boolean isDisplayed() {
        return heading().isVisible() && getHeadingText().contains("Example Domain");
    }
}
