package serenitycore.net.thucydides.core.annotations.locators;

import serenitymodel.net.serenitybdd.core.environment.ConfiguredEnvironment;
import serenitycore.net.serenitybdd.core.pages.WidgetObject;
import serenitymodel.net.thucydides.core.webdriver.Configuration;
import serenitycore.net.thucydides.core.webdriver.ElementLocatorFactorySelector;
import serenitycore.net.thucydides.core.webdriver.WidgetProxyCreator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

/**
 * Initializes {@link WidgetObject} fields with contextual locators.
 * 
 * @author Joe Nasca
 */
public class SmartWidgetProxyCreator implements WidgetProxyCreator {

	@Override
	public void proxyElements(WidgetObject widget, WebDriver driver) {
		ElementLocatorFactory finder = getElementLocatorFactorySelector().getLocatorFor(widget, driver);
		FieldDecorator decorator = new SmartFieldDecorator(finder, driver, widget.getPage());
		PageFactory.initElements(decorator, widget);
	}
	
	@Override
	public void proxyElements(WidgetObject widget, WebDriver driver, int timeoutInSeconds) {
		ElementLocatorFactory finder = getElementLocatorFactorySelector().withTimeout(timeoutInSeconds).getLocatorFor(widget, driver);
		FieldDecorator decorator = new SmartFieldDecorator(finder, driver, widget.getPage());
		PageFactory.initElements(decorator, widget);
	}

	private ElementLocatorFactorySelector getElementLocatorFactorySelector() {
		Configuration configuration = ConfiguredEnvironment.getConfiguration();
		return new ElementLocatorFactorySelector(configuration);
	}
}
