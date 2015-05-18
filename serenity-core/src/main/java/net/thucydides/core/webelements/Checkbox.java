package net.thucydides.core.webelements;

import org.openqa.selenium.WebElement;

/**
 * A checkbox web element.
 * Simplified handling checkboxes by providing a setChecked() method
 * that will ensure the the checkbox state will finish with the requested value, 
 * and which uses clicking as a user would do.
 */
public class Checkbox {

    private final WebElement checkboxField;
    
    public Checkbox(final WebElement checkboxField) {
        this.checkboxField = checkboxField;
    }

    public void setChecked(final boolean value) {
        if (checkboxField.isSelected()) {
            clickToUnselect(value);
        } else {
            clickToSelect(value);
        }
    }
    
    private void clickToSelect(final boolean value) {
        if (value) {
            checkboxField.click();
        }
    }

    private void clickToUnselect(final boolean value) {
        if (!value) {
            checkboxField.click();
        }
    }

    public boolean isChecked() {
        return checkboxField.isSelected();
    }

}
