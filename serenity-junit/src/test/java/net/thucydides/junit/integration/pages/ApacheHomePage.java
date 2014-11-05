package net.thucydides.junit.integration.pages;

import net.thucydides.core.annotations.At;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@At("http://www.apache.org")
public class ApacheHomePage extends PageObject {

    public ApacheHomePage(WebDriver driver) {
        super(driver);
    }
    
    public void clickOnProjects() {
        getDriver().findElement(By.linkText("Projects")).click();
    }

}
