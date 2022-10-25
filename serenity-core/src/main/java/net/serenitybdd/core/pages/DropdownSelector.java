package net.serenitybdd.core.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

class DropdownSelector {

    private final WebElementFacadeImpl webElementFacade;

    public DropdownSelector(WebElementFacadeImpl webElementFacade) {
        this.webElementFacade = webElementFacade;
    }

    public WebElementFacade byVisibleText(String label) {
        if (webElementFacade.driverIsDisabled()) {
            return webElementFacade;
        }

        WithRetries.on(webElementFacade).perform(
                elementFacade -> {
                    Select select = new Select(webElementFacade.getElement());
                    select.selectByVisibleText(label);
                }, 12);

        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }

    public String visibleTextValue() {
        Select select = new Select(webElementFacade.getElement());
        return select.getFirstSelectedOption().getText();
    }

    public List<String> visibleTextValues() {
        Select select = new Select(webElementFacade.getElement());
        return select.getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public WebElementFacade byValue(String value) {
        if (webElementFacade.driverIsDisabled()) {
            return webElementFacade;
        }
        WithRetries.on(webElementFacade).perform(
                elementFacade -> {
                    Select select = new Select(webElementFacade.getElement());
                    select.selectByValue(value);
                }, 12);

        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }

    public String value() {
        Select select = new Select(webElementFacade.getElement());
        return select.getFirstSelectedOption().getAttribute("value");
    }

    public List<String> values() {
        Select select = new Select(webElementFacade.getElement());
        return select.getAllSelectedOptions().stream()
                .map(option -> option.getAttribute("value"))
                .collect(Collectors.toList());
    }

    public WebElementFacade byIndex(int indexValue) {
        if (webElementFacade.driverIsDisabled()) {
            return webElementFacade;
        }
        WithRetries.on(webElementFacade).perform(
                elementFacade -> {
                    Select select = new Select(webElementFacade.getElement());
                    select.selectByIndex(indexValue);
                }, 12);
        webElementFacade.notifyScreenChange();
        return webElementFacade;
    }
}
