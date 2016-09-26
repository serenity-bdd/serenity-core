package net.serenitybdd.core.pages;

import io.appium.java_client.FindsByAccessibilityId;
import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.FindsByIosUIAutomation;
import net.serenitybdd.core.annotations.ImplementedBy;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ImplementedBy(WebElementFacadeImpl.class)

public interface WebElementFacade extends WebElement, WrapsElement, Locatable, WebElementState, FindsByAccessibilityId, FindsByAndroidUIAutomator,
        FindsByIosUIAutomation, ConfigurableTimeouts {

    public <T extends WebElementFacade> T then(String xpathOrCssSelector);

    public <T extends WebElementFacade> T findBy(String xpathOrCssSelector);

    public List<WebElementFacade> thenFindAll(
            String xpathOrCssSelector);

    public <T extends WebElementFacade> T findBy(By selector);

    public <T extends WebElementFacade> T find(By bySelector);

    public <T extends WebElementFacade> T then(By bySelector);

    public String getAttribute(String name);

    public List<WebElementFacade> thenFindAll(By selector);

    public long getImplicitTimeoutInMilliseconds();

    public <T extends WebElementFacade> T withTimeoutOf(int timeout, TimeUnit unit);

    /**
     * Convenience method to chain method calls more fluently.
     */
    public <T extends WebElementFacade> T and();

    /**
     * Convenience method to chain method calls more fluently.
     */
    public <T extends WebElementFacade> T then();

    public List<String> getSelectOptions();

    /**
     * Type a value into a field, making sure that the field is empty first.
     *
     * @param value
     */
    public <T extends WebElementFacade> T type(String value);

    /**
     * Type a value into a field and then press Enter, making sure that the field is empty first.
     *
     * @param value
     */
    public <T extends WebElementFacade> T typeAndEnter(String value);

    /**
     * Type a value into a field and then press TAB, making sure that the field is empty first.
     * This currently is not supported by all browsers, notably Firefox.
     *
     * @param value
     */
    public <T extends WebElementFacade> T typeAndTab(String value);

    public void setWindowFocus();

    public <T extends WebElementFacade> T deselectAll();

    public <T extends WebElementFacade> T deselectByVisibleText(String label);

    public <T extends WebElementFacade> T deselectByValue(String value);

    public <T extends WebElementFacade> T deselectByIndex(int indexValue);

    public <T extends WebElementFacade> T selectByVisibleText(String label);

    public <T extends WebElementFacade> T selectByValue(String value);

    public <T extends WebElementFacade> T selectByIndex(int indexValue);

    public <T extends WebElementFacade> T waitUntilVisible();

    public <T extends WebElementFacade> T waitUntilPresent();

    public Wait<WebDriver> waitForCondition();

    public <T extends WebElementFacade> T waitUntilNotVisible();

    public String getValue();

    public String getText();

    public <T extends WebElementFacade> T waitUntilEnabled();

    public <T extends WebElementFacade> T waitUntilClickable();

    public <T extends WebElementFacade> T waitUntilDisabled();

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    public void click();

    public void clear();

    public String toString();

    public boolean containsElements(By selector);

    public boolean containsElements(String xpathOrCssSelector);

    public void shouldContainElements(By selector);

    public void shouldContainElements(String xpathOrCssSelector);

    public boolean hasClass(String cssClassName);

}
