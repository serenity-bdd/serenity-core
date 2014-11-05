package com.bddinaction.flyinghigh.jbehave.pages;

import com.bddinaction.flyinghigh.jbehave.model.DestinationDeal;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomePage extends PageObject {

    @FindBy(id = "welcome-message")
    private WebElementFacade welcomeMessage;

    public String getWelcomeMessage() {
        return welcomeMessage.getText();
    }

    public List<DestinationDeal> getFeaturedDestinations() {
        List<DestinationDeal> deals = Lists.newArrayList();
        List<WebElementFacade> featuredDestinations = findAll(".featured .featured-destination");
        for (WebElement destinationEntry : featuredDestinations) {
            deals.add(destinationDealFrom(destinationEntry));
        }
        return deals;
    }

    private DestinationDeal destinationDealFrom(WebElement destinationEntry) {
        String destinationCity = $(destinationEntry).findBy(".destination-title").getText();
        String priceValue = $(destinationEntry).findBy(".destination-price").getText();
        int price = Integer.parseInt(priceValue.substring(1));
        return new DestinationDeal(destinationCity, price);
    }
}
