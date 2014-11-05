package sample.page;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import sample.elements.WebElementFacadeInputImpl;

public class WithFindByOnClassPage extends PageObject{
			
	@FindBy(css = "#lastname")
	public WebElementFacadeInputImpl elementLast;
	
	public WithFindByOnClassPage(WebDriver driver) {
		super(driver);
	}
	

}
