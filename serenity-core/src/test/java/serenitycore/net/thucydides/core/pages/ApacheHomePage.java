package serenitycore.net.thucydides.core.pages;

import serenitycore.net.serenitybdd.core.pages.PageObject;
import serenitycore.net.thucydides.core.annotations.At;
import org.openqa.selenium.WebDriver;

@At("http://www.apache.org")
public class ApacheHomePage extends PageObject {
    public ApacheHomePage(WebDriver driver) {
        super(driver);
    }
}