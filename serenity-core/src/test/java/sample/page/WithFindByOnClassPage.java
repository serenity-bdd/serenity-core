package sample.page;

import serenitycore.net.serenitybdd.core.annotations.findby.FindBy;
import serenitycore.net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import sample.elements.WebElementFacadeInputImpl;

public class WithFindByOnClassPage extends PageObject{

	@FindBy(css = "#lastname")
	public WebElementFacadeInputImpl elementLast;

	public WithFindByOnClassPage(WebDriver driver) {
		super(driver);
	}


}
