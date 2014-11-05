package sample.elements;

import net.thucydides.core.pages.WebElementFacadeImpl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class BadImplementer extends WebElementFacadeImpl{

	public BadImplementer(WebDriver driver, ElementLocator locator,
			long timeoutInMilliseconds) {
		super(driver, locator, timeoutInMilliseconds);
	}

}
