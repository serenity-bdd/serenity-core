package serenitycore.net.thucydides.core.pages;

import serenitycore.net.serenitybdd.core.pages.PageObject;
import serenitycore.net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;

@DefaultUrl("classpath:static-site/does-not-exist.html")
public class PageWithInvalidDefaultUrlOnTheClasspath extends PageObject {
    public PageWithInvalidDefaultUrlOnTheClasspath(WebDriver driver) {
        super(driver);
    }
}