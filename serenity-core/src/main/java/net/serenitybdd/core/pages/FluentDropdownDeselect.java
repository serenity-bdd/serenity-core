package net.serenitybdd.core.pages;

public class FluentDropdownDeselect extends FluentDropdown {

    public FluentDropdownDeselect(WebElementFacade webElementFacade) {
       super(webElementFacade);
    }

    public void byVisibleText(String text) {
        deselectByVisibleText(text);
    }

    public void byIndex(int index) {
        deselectByIndex(index);
    }

    public void byValue(String value) {
        deselectByValue(value);
    }

    public void all() {
        deselectAll();
    }
}
