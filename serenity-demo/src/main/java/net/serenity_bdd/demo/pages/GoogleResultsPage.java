package net.thucydides.demo.pages;

import net.thucydides.core.annotations.At;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;


@At("http://www.google\\..*")
public class GoogleResultsPage extends PageObject {

    @FindBy(name="btnG")
    WebElement searchButton;
    
    public GoogleResultsPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getResultTitles() {
        waitForRenderedElements(By.id("pnnext"));
        List<WebElement> results = getDriver().findElements(By.className("r"));
        List<String> resultTitles = new ArrayList<String>();
        for(WebElement result : results) {
            resultTitles.add(result.getText());
        }
        
        return resultTitles;
    }
}
