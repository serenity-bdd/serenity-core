package net.thucydides.core.pages;

import net.serenitybdd.annotations.At;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

@At("#HOST/common/microRegistration")
public class PageWithUrlDefinitions extends PageObject {
    public PageWithUrlDefinitions(WebDriver driver) {
        super(driver);
    }
}
