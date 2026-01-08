package net.serenitybdd.playwright.demo.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step library for form interactions.
 * <p>
 * Demonstrates common form operations with Playwright and Serenity.
 */
public class FormSteps {

    @Step("Enter '{2}' into field '{1}'")
    public void enterIntoField(Page page, String fieldSelector, String value) {
        page.locator(fieldSelector).fill(value);
    }

    @Step("Click on element '{1}'")
    public void clickOn(Page page, String selector) {
        page.locator(selector).click();
    }

    @Step("Click the button with text '{1}'")
    public void clickButtonWithText(Page page, String buttonText) {
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(buttonText)).click();
    }

    @Step("Select option '{2}' from dropdown '{1}'")
    public void selectFromDropdown(Page page, String selector, String option) {
        page.locator(selector).selectOption(option);
    }

    @Step("Check the checkbox '{1}'")
    public void checkCheckbox(Page page, String selector) {
        page.locator(selector).check();
    }

    @Step("Uncheck the checkbox '{1}'")
    public void uncheckCheckbox(Page page, String selector) {
        page.locator(selector).uncheck();
    }

    @Step("Verify element '{1}' is visible")
    public void verifyElementIsVisible(Page page, String selector) {
        assertThat(page.locator(selector).isVisible())
                .as("Element '%s' should be visible", selector)
                .isTrue();
    }

    @Step("Verify element '{1}' contains text '{2}'")
    public void verifyElementContainsText(Page page, String selector, String expectedText) {
        String actualText = page.locator(selector).textContent();
        assertThat(actualText)
                .as("Element '%s' text content", selector)
                .contains(expectedText);
    }

    @Step("Verify element '{1}' has value '{2}'")
    public void verifyElementHasValue(Page page, String selector, String expectedValue) {
        String actualValue = page.locator(selector).inputValue();
        assertThat(actualValue)
                .as("Element '%s' input value", selector)
                .isEqualTo(expectedValue);
    }

    @Step("Wait for element '{1}' to be visible")
    public void waitForElement(Page page, String selector) {
        page.locator(selector).waitFor(new Locator.WaitForOptions().setState(
                com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }
}
