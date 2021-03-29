package serenitycore.net.serenitybdd.core.pages;

import serenitycore.net.serenitybdd.core.annotations.ImplementedBy;
import org.openqa.selenium.support.FindBy;

/**
 * Represents a page fragment which occurs across pages or multiple times in a single
 * page. Instance members with {@link FindBy @FindBy} style annotations are located
 * within this context.
 * 
 * @author Joe Nasca
 */
@ImplementedBy(WidgetObjectImpl.class)
public interface WidgetObject extends WebElementFacade {

	/**
	 * Get the page containing this widget.
	 * @return the page containing this widget
	 */
	public PageObject getPage();
	
}
