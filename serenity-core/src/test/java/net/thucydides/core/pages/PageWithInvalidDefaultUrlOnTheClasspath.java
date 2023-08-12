package net.thucydides.core.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;

@DefaultUrl("classpath:static-site/does-not-exist.html")
public class PageWithInvalidDefaultUrlOnTheClasspath extends PageObject {
    public PageWithInvalidDefaultUrlOnTheClasspath(WebDriver driver) {
        super(driver);
    }
}
