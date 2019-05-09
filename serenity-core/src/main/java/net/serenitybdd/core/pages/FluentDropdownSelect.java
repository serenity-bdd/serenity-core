package net.serenitybdd.core.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ISelect;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class FluentDropdownSelect extends FluentDropdown {

    public FluentDropdownSelect(WebElementFacade webElementFacade) {
       super(webElementFacade);
    }

    public void byVisibleText(String text) {
        selectByVisibleText(text);
    }

    public void byIndex(int index) {
        selectByIndex(index);
    }

    public void byValue(String value) {
        selectByValue(value);
    }

    public void all() {
        getOptions().forEach(
                option -> setSelected(option, true)
        );
    }

    private void setSelected(WebElement option, boolean select) {
        if (option.isSelected() != select) {
            option.click();
        }
    }
}
