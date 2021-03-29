package sample.page;

import serenitycore.net.serenitybdd.core.annotations.findby.FindBy;
import serenitycore.net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import sample.elements.HasBadImplementer;

public class WithBadImplementerPage extends PageObject{

	@FindBy(css = "#firstname")
	public HasBadImplementer intefaceNotImplementedByClass;

	public WithBadImplementerPage(WebDriver driver) {
		super(driver);
	}

}
