package sample.csv;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;


public class TestPageObject extends PageObject {
	
	public TestPageObject(WebDriver driver) {
		super(driver);
	}
	
	@Override
	public String toString() {
		return "TestPageObject";
	}
}