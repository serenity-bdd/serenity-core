package net.thucydides.core.webelements;

import com.google.common.base.Optional;
import org.openqa.selenium.WebElement;

import java.util.List;

public class RadioButtonGroup {

    private final List<WebElement> radioButtons;

    public RadioButtonGroup(List<WebElement> radioButtons) {
        this.radioButtons = radioButtons;
    }

    public Optional<String> getSelectedValue() {
        for(WebElement radioButton : radioButtons) {
            if (radioButton.isSelected()) {
                return Optional.of(radioButton.getAttribute("value"));
            }
        }
        return Optional.absent();
    }

    public void selectByValue(String value) {
        for(WebElement radioButton : radioButtons) {
            if (value.equals(radioButton.getAttribute("value"))) {
                radioButton.click();
                break;
            }
        }
    }
}
