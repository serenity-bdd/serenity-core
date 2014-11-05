package sample.page;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import sample.elements.WebElementFacadeNotImplemented;

public class NotImplementedInterfacePage extends PageObject{
	
	@FindBy(css = "#lastname")
	public WebElementFacadeNotImplemented elementLast;
	
	public NotImplementedInterfacePage(WebDriver driver) {
		super(driver);
	}
	
}
