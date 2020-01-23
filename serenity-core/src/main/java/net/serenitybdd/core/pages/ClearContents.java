package net.serenitybdd.core.pages;

import net.serenitybdd.core.CurrentOS;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static net.serenitybdd.core.CurrentOS.OSType.*;

public class ClearContents {

    private static final Consumer<WebElement> CONTROL_A_DELETE
            = element -> element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

    private static final Consumer<WebElement> COMMAND_A_DELETE
            = element -> element.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.DELETE);

    private static Map<CurrentOS.OSType, Consumer<WebElement>> CLEAR_ELEMENT_PER_OS = new HashMap<>();

    static {
        CLEAR_ELEMENT_PER_OS.put(linux, CONTROL_A_DELETE);
        CLEAR_ELEMENT_PER_OS.put(windows, CONTROL_A_DELETE);
        CLEAR_ELEMENT_PER_OS.put(other, CONTROL_A_DELETE);
        CLEAR_ELEMENT_PER_OS.put(mac, COMMAND_A_DELETE);
    }

    public static void ofElement(WebElement element) {
        CLEAR_ELEMENT_PER_OS.get(CurrentOS.getType()).accept(element);
    }
}
