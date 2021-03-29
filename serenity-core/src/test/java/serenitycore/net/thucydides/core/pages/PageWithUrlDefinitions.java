package serenitycore.net.thucydides.core.pages;

import serenitycore.net.serenitybdd.core.pages.PageObject;
import serenitycore.net.thucydides.core.annotations.At;
import org.openqa.selenium.WebDriver;

@At("#HOST/common/microRegistration")
public class PageWithUrlDefinitions extends PageObject {
    public PageWithUrlDefinitions(WebDriver driver) {
        super(driver);
    }
}