package net.serenitybdd.core.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.openqa.selenium.Keys.DELETE;

public class ClearContents {

    private static final String CONTROL_A = Keys.chord(Keys.CONTROL, "a");
    private static final String COMMAND_A = Keys.chord(Keys.COMMAND, "a");

    public static void ofElement(WebElement element) {
        element.sendKeys(COMMAND_A, DELETE);
        element.sendKeys(CONTROL_A, DELETE);
    }
}
