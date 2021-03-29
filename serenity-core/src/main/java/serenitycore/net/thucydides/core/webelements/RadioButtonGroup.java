package serenitycore.net.thucydides.core.webelements;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

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
        return Optional.empty();
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
