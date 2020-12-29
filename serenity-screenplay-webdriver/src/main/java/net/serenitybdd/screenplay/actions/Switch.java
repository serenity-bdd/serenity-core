package net.serenitybdd.screenplay.actions;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import net.serenitybdd.screenplay.Performable;
import org.openqa.selenium.WebElement;

/**
 * Switch to a different frame or window.
 * This wraps the WebDriver.switchTo() methods as Screenplay interactions.
 */
public class Switch {

    public static Performable toFrame(Integer frameId) {
        return new DriverTask(driver -> driver.switchTo().frame(frameId));
    }

    public static Performable toFrame(String frameName) {
        return new DriverTask(driver -> driver.switchTo().frame(frameName));
    }

    public static Performable toFrame(WebElement webElement) {
        return new DriverTask(driver -> driver.switchTo().frame(webElement));
    }

    public static Performable toParentFrame() {
        return new DriverTask(driver -> driver.switchTo().parentFrame());
    }

    public static Performable toWindow(String nameOrHandle) {
        return new DriverTask( driver -> driver.switchTo().window(nameOrHandle) );
    }

    public static Performable toNewWindow() {
        return instrumented(SwitchToNewWindow.class);
    }

    public static Performable toWindowTitled(String title) {
        return instrumented(SwitchToWindowTitle.class, title);
    }

    /**
     * Switch to the first window that is not the current window.
     *
     * @return
     */
    public static Performable toTheOtherWindow() {
        return new DriverTask(driver -> {
            String currentWindow = driver.getWindowHandle();
            driver.getWindowHandles().stream()
                    .filter(handle -> !handle.equals(currentWindow))
                    .findFirst()
                    .ifPresent(
                            otherWindowHandle -> driver.switchTo().window(otherWindowHandle)
                    );
        }
        );
    }

    public static Performable toAlert() {
        return new DriverTask(driver -> driver.switchTo().alert());
    }

    public static Performable toDefaultContext() {
        return new DriverTask(driver -> driver.switchTo().defaultContent());
    }

    public static Performable toActiveElement() {
        return new DriverTask(driver -> driver.switchTo().activeElement());
    }
}
