package net.serenitybdd.playwright.demo.steps;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;

/**
 * Step library for navigation-related actions.
 * <p>
 * Each method annotated with @Step will:
 * <ul>
 *   <li>Appear as a step in the Serenity report</li>
 *   <li>Trigger automatic screenshot capture at completion</li>
 *   <li>Record timing information</li>
 * </ul>
 */
public class NavigationSteps {

    @Step("Navigate to {1}")
    public void navigateTo(Page page, String url) {
        page.navigate(url);
    }

    @Step("Refresh the page")
    public void refreshPage(Page page) {
        page.reload();
    }

    @Step("Go back to previous page")
    public void goBack(Page page) {
        page.goBack();
    }

    @Step("Go forward to next page")
    public void goForward(Page page) {
        page.goForward();
    }

    @Step("Wait for page to fully load")
    public void waitForPageLoad(Page page) {
        page.waitForLoadState();
    }
}
