package net.thucydides.core.pages;

import net.thucydides.core.annotations.ImplementedBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ImplementedBy(WebElementFacadeImpl.class)
public interface WebElementFacade extends WebElement, WrapsElement, Locatable, WebElementState {

	public abstract WebElementFacade then(String xpathOrCssSelector);

	public abstract WebElementFacade findBy(String xpathOrCssSelector);

	public abstract List<WebElementFacade> thenFindAll(
			String xpathOrCssSelector);

	public abstract WebElementFacade findBy(By selector);

	public abstract WebElementFacade find(By bySelector);

	public abstract WebElementFacade then(By bySelector);

	public abstract String getAttribute(String name);

	public abstract List<WebElementFacade> thenFindAll(By selector);

	public abstract long getTimeoutInMilliseconds();

	public abstract WebElementFacade withTimeoutOf(int timeout,
			TimeUnit unit);

    /**
	 * Convenience method to chain method calls more fluently.
	 */
	public abstract WebElementFacade and();

	/**
	 * Convenience method to chain method calls more fluently.
	 */
	public abstract WebElementFacade then();

    public abstract List<String> getSelectOptions();

    /**
	 * Type a value into a field, making sure that the field is empty first.
	 *
	 * @param value
	 */
	public abstract WebElementFacade type(String value);

	/**
	 * Type a value into a field and then press Enter, making sure that the field is empty first.
	 *
	 * @param value
	 */
	public abstract WebElementFacade typeAndEnter(String value);

	/**
	 * Type a value into a field and then press TAB, making sure that the field is empty first.
	 * This currently is not supported by all browsers, notably Firefox.
	 *
	 * @param value
	 */
	public abstract WebElementFacade typeAndTab(String value);

	public abstract void setWindowFocus();

	public abstract WebElementFacade selectByVisibleText(String label);

    public abstract WebElementFacade selectByValue(String value);

    public abstract WebElementFacade selectByIndex(int indexValue);

    public abstract WebElementFacade waitUntilVisible();

	public abstract WebElementFacade waitUntilPresent();

	public abstract Wait<WebDriver> waitForCondition();
	
	public abstract WebElementFacade waitUntilNotVisible();

	public abstract String getValue();

    public abstract String getText();

	public abstract WebElementFacade waitUntilEnabled();

	public abstract WebElementFacade waitUntilDisabled();

    /**
	 * Wait for an element to be visible and enabled, and then click on it.
	 */
	public abstract void click();

	public abstract void clear();

	public abstract String toString();

}