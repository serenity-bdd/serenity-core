package net.serenitybdd.core.pages;

import org.openqa.selenium.support.ui.Select;

class DropdownDeselector {

    private final WebElementFacadeImpl webElementFacade;

    public DropdownDeselector(WebElementFacadeImpl webElementFacade) {

        this.webElementFacade = webElementFacade;
    }

    public WebElementFacade all() {
        if (webElementFacade.driverIsDisabled()) { return webElementFacade; }

        WithRetries.on(webElementFacade).perform(
                elementFacade -> {
                    Select select = new Select(webElementFacade.getElement());
                    select.deselectAll();
                }, 12);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }

    public WebElementFacade byVisibleText(String label) {
        if (webElementFacade.driverIsDisabled()) { return webElementFacade; }

        WithRetries.on(webElementFacade).perform(
                elementFacade -> {
                    Select select = new Select(webElementFacade.getElement());
                    select.deselectByVisibleText(label);
                }, 12);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }

    public WebElementFacade byValue(String value) {
        if (webElementFacade.driverIsDisabled()) { return webElementFacade; }

        WithRetries.on(webElementFacade).perform(
                elementFacade -> {
                    Select select = new Select(webElementFacade.getElement());
                    select.deselectByValue(value);
                }, 12);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }

    public WebElementFacade byIndex(int indexValue) {
        if (webElementFacade.driverIsDisabled()) { return webElementFacade; }
        WithRetries.on(webElementFacade).perform(
                elementFacade -> {
                    Select select = new Select(webElementFacade.getElement());
                    select.deselectByIndex(indexValue);
                }, 12);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }
}
