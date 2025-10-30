package net.thucydides.core.pages;

import net.serenitybdd.annotations.At;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

@At("http://www.apache.org")
public class ApacheHomePage extends PageObject {
    public ApacheHomePage(WebDriver driver) {
        super(driver);
    }
}
