package net.serenitybdd.core.pages;

import net.thucydides.core.webdriver.DefaultWidgetObjectInitialiser;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * Base implementation for {@link WidgetObject}.
 * 
 * @author Joe Nasca
 */
public class WidgetObjectImpl extends WebElementFacadeImpl implements WidgetObject {

	private final PageObject page;

	public WidgetObjectImpl(PageObject page, ElementLocator locator, WebElement webElement, long timeoutInMilliseconds) {
		super(page.getDriver(), locator, webElement, timeoutInMilliseconds);
		this.page = page;
		new DefaultWidgetObjectInitialiser(page.getDriver(), (int) timeoutInMilliseconds).apply(this);
	}

	public WidgetObjectImpl(PageObject page, ElementLocator locator, long timeoutInMilliseconds) {
		this(page, locator, (WebElement) null, timeoutInMilliseconds);
	}

	public PageObject getPage() {
		return page;
	}
}
