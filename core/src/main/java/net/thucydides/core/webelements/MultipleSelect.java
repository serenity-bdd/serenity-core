package net.thucydides.core.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A convenience class to help handle multiple select fields.
 */
public class MultipleSelect {
    private final WebElement dropdownField;

    public MultipleSelect(final WebElement dropdownField) {
        this.dropdownField = dropdownField;
    }

    public Set<String> getSelectedOptionLabels() {
        Set<String> selectedOptions = new HashSet<String>();
        List<WebElement> options = dropdownField.findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.isSelected()) {
                selectedOptions.add(option.getText());
            }
        }
        return selectedOptions;
    }

    public Set<String> getSelectedOptionValues() {
        Set<String> selectedOptions = new HashSet<String>();

        List<WebElement> options = dropdownField.findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.isSelected()) {
                selectedOptions.add(option.getAttribute("value"));
            }
        }
        return selectedOptions;
    }
}
