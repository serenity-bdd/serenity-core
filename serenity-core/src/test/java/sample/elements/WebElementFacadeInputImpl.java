package sample.elements;

import net.thucydides.core.pages.WebElementFacadeImpl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class WebElementFacadeInputImpl extends WebElementFacadeImpl implements WebElementFacadeInput{

	public WebElementFacadeInputImpl(WebDriver driver, ElementLocator locator,
			long timeoutInMilliseconds) {
		super(driver, locator, timeoutInMilliseconds);
	}

	@Override
	public void enterText(String text) {
		getElement().sendKeys(text);
		
	}
	
	
}