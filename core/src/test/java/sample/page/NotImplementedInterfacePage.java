package sample.page;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import sample.elements.WebElementFacadeNotImplemented;

public class NotImplementedInterfacePage extends PageObject{

	@FindBy(css = "#lastname")
	public WebElementFacadeNotImplemented elementLast;

	public NotImplementedInterfacePage(WebDriver driver) {
		super(driver);
	}

}
