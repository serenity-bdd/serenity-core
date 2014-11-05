package net.thucydides.core.pages;

import org.openqa.selenium.WebDriver;

/**
 * A page object that can represent any page, without specifiying the exact page.
 */
public class AnyPage extends PageObject {
    public AnyPage(WebDriver driver) {
        super(driver);
    }
}
