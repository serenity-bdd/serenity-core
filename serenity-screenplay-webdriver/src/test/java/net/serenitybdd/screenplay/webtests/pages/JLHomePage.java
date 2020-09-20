package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isCurrentlyVisible;

import java.time.Duration;

public class JLHomePage extends PageObject {
    private static final String SITE = "#siteMenu";
    private static final String ADDSITE = "#createSiteMenu";

    public static Target HRFSITE = Target.the("SiteHref").locatedBy(SITE);
    public static Target MNUADDSITE = Target.the("AddSiteMenuItem").locatedBy(ADDSITE);

    @FindBy(id = "siteMenu")
    public WebElementFacade site;

    @FindBy(id = "createSiteMenu")
    public WebElementFacade addSite;

    public void navigateToPage(String pageName) {
        WebElementFacade menu = null;
        WebElementFacade menuItem = null;

        waitABit(6000);

        if (pageName == "AddSite") {
            menu = site;
            menuItem = addSite;
        }


        menu.click();
        menuItem.click();
    }
}
