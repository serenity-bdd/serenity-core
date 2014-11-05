package net.thucydides.core.pages;

import net.thucydides.core.annotations.At;
import org.openqa.selenium.WebDriver;

@At("#HOST/common/microRegistration")
public class PageWithUrlDefinitions extends PageObject {
    public PageWithUrlDefinitions(WebDriver driver) {
        super(driver);
    }
}