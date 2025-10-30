package net.thucydides.core.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

@DefaultUrl("classpath:static-site/index.html")
public class PageWithDefaultUrlOnTheClasspath extends PageObject {
    public PageWithDefaultUrlOnTheClasspath(WebDriver driver) {
        super(driver);
    }
}
