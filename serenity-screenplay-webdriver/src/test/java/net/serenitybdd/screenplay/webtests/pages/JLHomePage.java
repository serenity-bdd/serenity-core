package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;

public class JLHomePage extends PageObject {
    private static final String SITE = "#siteMenu";
    private static final String ADDSITE = "#createSiteMenu";

    public static Target HRFSITE = Target.the("SiteHre").locatedBy(SITE);
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

        //withTimeoutOf(Duration.ofSeconds(10)).waitForCondition().until(ExpectedConditions.elementToBeClickable(menu));
        //element(menu).waitUntilClickable();
        menu.click();
        //withTimeoutOf(Duration.ofSeconds(10)).waitForCondition().until(ExpectedConditions.elementToBeClickable(menuItem));
        menuItem.click();
    }
}
