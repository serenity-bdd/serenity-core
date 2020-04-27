package net.thucydides.core.pages.components;

import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.webelements.MultipleSelect;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 6/06/11
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class Dropdown {

    private final WebElement dropdown;
    private final Select selectField;

    private Dropdown(final WebElement dropdown) {
        this.dropdown = dropdown;
        this.selectField = new Select(dropdown);
    }

    public static Dropdown forWebElement(final WebElement webElement) {
        if (webElement instanceof WebElementFacade) {
            return new Dropdown((((WebElementFacade) webElement).getElement()));
        } else {
            return new Dropdown(webElement);
        }
    }

    public void select(final String visibleLabel) {
        selectField.selectByVisibleText(visibleLabel);
    }
    
    public void selectByValue(final String value) {
        selectField.selectByValue(value);
    }

    public void selectMultipleItems(final String... selectedLabels) {
        Select select = new Select(dropdown);
        for (String selectedLabel : selectedLabels) {
            select.selectByVisibleText(selectedLabel);
        }
    }


    public Set<String> getSelectedOptionLabels() {
        MultipleSelect multipleSelect = new MultipleSelect(dropdown);
        return multipleSelect.getSelectedOptionLabels();
    }

    public Set<String> getSelectedOptionValues() {
        MultipleSelect multipleSelect = new MultipleSelect(dropdown);
        return multipleSelect.getSelectedOptionValues();
    }

    public String getSelectedValue() {
        return selectField.getFirstSelectedOption().getAttribute("value");
    }

    public String getSelectedLabel() {
        return selectField.getFirstSelectedOption().getText();
    }
}
