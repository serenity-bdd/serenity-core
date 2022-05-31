package net.serenitybdd.core.pages;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.annotations.ImplementedBy;
import net.serenitybdd.core.selectors.Selectors;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ImplementedBy(WebElementFacadeImpl.class)
public interface WebElementFacade extends WebElement, WrapsElement, WebElementState, Locatable, ConfigurableTimeouts {

    <T extends WebElementFacade> T then(String xpathOrCssSelector);
    <T extends WebElementFacade> T thenFind(String xpathOrCssSelector);
    <T extends WebElementFacade> T then(String xpathOrCssSelector, Object... arguments);
    <T extends WebElementFacade> T thenFind(String xpathOrCssSelector, Object... arguments);

    <T extends WebElementFacade> T findBy(String xpathOrCssSelector);
    <T extends WebElementFacade> T findBy(String xpathOrCssSelector, Object... arguments);

    ListOfWebElementFacades thenFindAll(String xpathOrCssSelector);
    ListOfWebElementFacades thenFindAll(String xpathOrCssSelector, Object... arguments);

    <T extends WebElementFacade> T findBy(By selector);

    <T extends WebElementFacade> T find(By bySelector);

    <T extends WebElementFacade> T then(By bySelector);

    String getAttribute(String name);

    ListOfWebElementFacades thenFindAll(By... selector);

    long getImplicitTimeoutInMilliseconds();

    @Deprecated
    <T extends WebElementFacade> T withTimeoutOf(int timeout, TimeUnit unit);

    <T extends WebElementFacade> T withTimeoutOf(int timeout, TemporalUnit unit);

    <T extends WebElementFacade> T withTimeoutOf(Duration duration);

    /**
     * Convenience method to chain method calls more fluently.
     */
    <T extends WebElementFacade> T and();

    /**
     * Convenience method to chain method calls more fluently.
     */
    <T extends WebElementFacade> T then();

    List<String> getSelectOptions();
    List<String> getSelectOptionValues();

        String getFirstSelectedOptionVisibleText();
    List<String> getSelectedVisibleTexts();

    String getFirstSelectedOptionValue();
    List<String> getSelectedValues();

    /**
     * Type a value into a field, making sure that the field is empty first.
     */
    <T extends WebElementFacade> T type(CharSequence... keysToSend);

    /**
     * Type a value into a field and then press Enter, making sure that the field is empty first.
     */
    <T extends WebElementFacade> T typeAndEnter(String value);

    /**
     * Type a value into a field and then press TAB, making sure that the field is empty first.
     * This currently is not supported by all browsers, notably Firefox.
     */
    <T extends WebElementFacade> T typeAndTab(String value);

    void setWindowFocus();

    FluentDropdownSelect select();

    FluentDropdownDeselect deselect();

    <T extends WebElementFacade> T deselectAll();

    <T extends WebElementFacade> T deselectByVisibleText(String label);

    <T extends WebElementFacade> T deselectByValue(String value);

    <T extends WebElementFacade> T deselectByIndex(int indexValue);

    <T extends WebElementFacade> T selectByVisibleText(String label);

    <T extends WebElementFacade> T selectByValue(String value);

    <T extends WebElementFacade> T selectByIndex(int indexValue);

    <T extends WebElementFacade> T waitUntilVisible();

    <T extends WebElementFacade> T waitUntilPresent();

    Wait<WebDriver> waitForCondition();

    <T extends WebElementFacade> T waitUntilNotVisible();

    String getValue();

    String getText();

    String getTextContent();

    boolean isDisabled();

    <T extends WebElementFacade> T waitUntilEnabled();

    <T extends WebElementFacade> T waitUntilClickable();

    <T extends WebElementFacade> T waitUntilDisabled();

    /**
     * Wait for an element to be visible and enabled, and then click on it.
     */
    void click();

    void click(ClickStrategy clickStrategy);

    void doubleClick();

    void contextClick();

    void clear();

    String toString();

    boolean containsElements(By selector);

    boolean containsElements(String xpathOrCssSelector);

    WebElementState shouldContainElements(By selector);

    WebElementState shouldContainElements(String xpathOrCssSelector);

    boolean hasClass(String cssClassName);

    WebElement getElement();

    ListOfWebElementFacades findNestedElementsMatching(ResolvableElement nestedElement);


}
