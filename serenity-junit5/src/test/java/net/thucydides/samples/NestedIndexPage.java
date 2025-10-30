package net.thucydides.samples;


import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;

@DefaultUrl("classpath:static-site/index.html")
public class NestedIndexPage extends PageObject {

    public WebElement multiselect;

    public WebElement checkbox;

    public WebElement textfield;

    public NestedIndexPage(WebDriver driver) {
        super(driver);
    }
    
    public void selectItem(String option) {
        this.selectFromDropdown(multiselect, option);
    }

    public Set<String> getSelectedValues() {
        return this.getSelectedOptionValuesFrom(multiselect);
    }

    public void setCheckboxOption(boolean value) {
        this.setCheckbox(checkbox, value);
    }
    
    public void enterValue(String value) {
        element(textfield).type(value);
    }

}
