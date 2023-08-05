package net.thucydides.junit.integration.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.annotations.At;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@At("http://projects.apache.org")
public class ApacheProjectPage extends PageObject {

    @FindBy(linkText="Categories")
    WebElement categoriesLink;
    
    @FindBy(linkText="DOES-NOT-EXIST")
    WebElement doesNotExistLink;

    public ApacheProjectPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickOnProjects() {
        getDriver().findElement(By.linkText("Projects")).click();
    }

    public void clickOnCategories() {
        categoriesLink.click();
    }

    public void clickOnInexistantLink() {
        categoriesLink.click();
    }
    
    public void clickOnProjectsAndCheckTitle() {
        assertThat(getTitle(), is("Not the right one"));
    }
    
}
