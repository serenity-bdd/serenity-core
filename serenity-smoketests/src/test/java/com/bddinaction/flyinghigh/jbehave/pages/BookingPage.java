package com.bddinaction.flyinghigh.jbehave.pages;

import com.google.common.base.Function;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementState;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class BookingPage extends PageObject {

    @FindBy(id = "welcome-message")
    private WebElement welcomeMessage;

    @FindBy(css = ".typeahead li")
    private List<WebElement> typeaheadEntries;

    WebElement from;
    WebElement to;

    @FindBy(id="depart")
    WebElement departField;

    @FindBy(id="return")
    WebElement returnField;

    WebElement search;

    public String getWelcomeMessage() {
        return welcomeMessage.getText();
    }

    public List<String> getFeaturedDestinations() {
        List<WebElement> featuredDestinationTitles
                = getDriver().findElements(By.cssSelector(".featured .destination-title"));

        return extract(featuredDestinationTitles, on(WebElement.class).getText());
    }

    public void setFlightType(String flightType) {
        find(By.cssSelector("input[name='flightType'][value='" + flightType + "']")).click();
    }

    public BookingPage setFrom(String fromCity) {
        from.sendKeys(fromCity);
        return this;
    }

    public String getFrom() {
        return from.getAttribute("value");
    }

    public BookingPage setTo(String toCity) {
        to.sendKeys(toCity);
        return this;
    }

    public String getTo() {
        return to.getAttribute("value");
    }

    public BookingPage setDepart(DateTime departDate) {
        departField.sendKeys(departDate.toString("dd/MM/yyyy"));
        return this;
    }

    public DateTime getDepart() {
        return DateTime.parse(departField.getAttribute("value"),
                              DateTimeFormat.forPattern("dd/MM/yyyy"));
    }

    public void setTravelClass(String travelClass) {
        WebElement travelClassElt = getDriver().findElement(By.id("travel-class"));
        new Select(travelClassElt).selectByVisibleText(travelClass);
    }


    public List<String> getTypeaheadEntries() {
        List<String> entries = new ArrayList<String>();
        for(WebElement typeaheadElement : typeaheadEntries) {
            entries.add(typeaheadElement.getText());
        }
        return entries;
    }

    public List<String> getFromTypeaheads() {
        waitForRenderedElementsToBePresent(By.cssSelector(".typeahead"));
        List<String> menuEntries = new ArrayList<String>();
        for(WebElement menuEntry : $("#main-navbar").thenFindAll("li")) {
            menuEntries.add(menuEntry.getText());
        }

        return extract(typeaheadEntries, on(WebElement.class).getText());
    }

    public Function<WebDriver, WebElement> typeaheadIsReady(WebDriver driver) {
        return new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver){
                return driver.findElement(By.className("typeahead"));
            }
        } ;
    }

    public Function<WebDriver, Boolean> typeaheadIsNotEmpty() {
        return new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver){
                return driver.findElements(By.cssSelector(".typeahead li")).size() > 0;
            }
        } ;
    }

    public WebElementState searchButton() {
        return $(search);
    }

    public void search() {
        search.click();
    }

    public void waitUntilDisplayed() {
        waitForPresenceOf("#book");
    }
}


