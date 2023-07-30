package net.thucydides.core.webdriver;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WidgetObject;
import org.openqa.selenium.WebDriver;

/**
 * Initializes the {@link WebElementFacade} members of a {@link WidgetObject}.
 * @author Joe Nasca
 */
public class DefaultWidgetObjectInitialiser extends AbstractObjectInitialiser<WidgetObject> {

	private final WidgetProxyCreator widgetProxyCreator;
	
    public DefaultWidgetObjectInitialiser(WebDriver driver, int ajaxTimeoutInMilliseconds) {
        super(driver, ajaxTimeoutInMilliseconds);
        this.widgetProxyCreator = SerenityInfrastructure.getWidgetProxyCreator();
    }

    public boolean apply(WidgetObject widget) {
        widgetProxyCreator.proxyElements(widget, driver, ajaxTimeoutInSecondsWithAtLeast1Second());
        return true;
    }
}
