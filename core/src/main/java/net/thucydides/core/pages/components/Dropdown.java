package net.thucydides.core.pages.components;

import net.thucydides.core.webelements.MultipleSelect;
import org.openqa.selenium.By;
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
        return new Dropdown(webElement);
    }

    public void select(final String visibleLabel) {
        selectField.selectByVisibleText(visibleLabel);
    }

    public void selectMultipleItems(final String... selectedLabels) {
        for (String selectedLabel : selectedLabels) {
            String optionPath = String.format("//option[.='%s']", selectedLabel);
            WebElement option = dropdown.findElement(By.xpath(optionPath));
            option.click();
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
