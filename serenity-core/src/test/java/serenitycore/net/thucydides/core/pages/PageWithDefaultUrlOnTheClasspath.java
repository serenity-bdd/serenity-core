package serenitycore.net.thucydides.core.pages;

import serenitycore.net.serenitybdd.core.pages.PageObject;
import serenitycore.net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;

@DefaultUrl("classpath:static-site/index.html")
public class PageWithDefaultUrlOnTheClasspath extends PageObject {
    public PageWithDefaultUrlOnTheClasspath(WebDriver driver) {
        super(driver);
    }
}