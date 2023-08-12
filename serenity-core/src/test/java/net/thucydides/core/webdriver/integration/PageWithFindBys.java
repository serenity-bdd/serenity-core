package net.thucydides.core.webdriver.integration;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@DefaultUrl("classpath:static-site/index.html")
public class PageWithFindBys extends PageObject {
    public PageWithFindBys(WebDriver driver) {
        super(driver);
    }

    @FindBy(tagName = "option")
    public List<WebElementFacade> allTheOptions;

    @FindBy(tagName = "input")
    public List<WebElementFacade> allTheInputFields;

    @FindAll({@FindBy(tagName = "option"), @FindBy(tagName = "input")})
    public List<WebElement> allTheInputAndOptionsFields;
}
