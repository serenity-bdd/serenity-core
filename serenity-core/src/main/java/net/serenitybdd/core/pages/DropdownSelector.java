package net.serenitybdd.core.pages;

import org.openqa.selenium.support.ui.Select;

class DropdownSelector {

    private final WebElementFacadeImpl webElementFacade;

    public DropdownSelector(WebElementFacadeImpl webElementFacade) {

        this.webElementFacade = webElementFacade;
    }

    public WebElementFacade byVisibleText(String label) {
        if (webElementFacade.driverIsDisabled()) { return webElementFacade; }

        webElementFacade.waitUntilElementAvailable();
        Select select = new Select(webElementFacade.getElement());
        select.selectByVisibleText(label);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }

    public String visibleTextValue() {
        Select select = new Select(webElementFacade.getElement());
        return select.getFirstSelectedOption().getText();
    }


    public WebElementFacade byValue(String value) {
        if (webElementFacade.driverIsDisabled()) { return webElementFacade; }
        webElementFacade.waitUntilElementAvailable();
        Select select = new Select(webElementFacade.getElement());
        select.selectByValue(value);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }

    public String value() {
        Select select = new Select(webElementFacade.getElement());
        return select.getFirstSelectedOption().getAttribute("value");
    }

    public WebElementFacade byIndex(int indexValue) {
        if (webElementFacade.driverIsDisabled()) { return webElementFacade; }
        webElementFacade.waitUntilElementAvailable();
        Select select = new Select(webElementFacade.getElement());
        select.selectByIndex(indexValue);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }
}