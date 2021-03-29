package serenitycore.net.thucydides.core.annotations;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class ElementIsUsable {
        public static boolean forElement(WebElement element) {
            try {
                return (element != null) && (element.isDisplayed());
            } catch (WebDriverException elementNotReady) {
                return false;
            }
        }
    }