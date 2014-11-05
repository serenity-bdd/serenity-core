package com.bddinaction.flyinghigh.jbehave.pages.components;

import com.bddinaction.flyinghigh.jbehave.pages.BookingPage;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

public class MainMenu extends PageObject {

    public void selectMenuOption(String menuOption) {
        getDriver().manage().window().setSize(new Dimension(1440,900));
        find(By.partialLinkText(menuOption)).click();
        waitFor(100).milliseconds();
    }

    public BookingPage selectBookFlight() {
        selectMenuOption("Book");
        return switchToPage(BookingPage.class);
    }

}
