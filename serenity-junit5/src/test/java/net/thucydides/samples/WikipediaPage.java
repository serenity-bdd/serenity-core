package net.thucydides.samples;


import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;

@DefaultUrl("http://www.wikipedia.org")
public class WikipediaPage extends PageObject {

    public WikipediaPage(WebDriver driver) {
        super(driver);
    }
}
