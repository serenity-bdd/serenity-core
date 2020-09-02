package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Performable;
import org.openqa.selenium.WebElement;

/**
 * Switch to a different frame or window.
 * This wraps the WebDriver.switchTo() methods as Screenplay interactions.
 */
public class Switch {

    public static Performable toFrame(Integer frameId) {
        return new DriverTask( driver -> driver.switchTo().frame(frameId) );
    }

    public static Performable toFrame(String frameName) {
        return new DriverTask( driver -> driver.switchTo().frame(frameName) );
    }

    public static Performable toFrame(WebElement webElement) {
        return new DriverTask( driver -> driver.switchTo().frame(webElement) );
    }

    public static Performable toParentFrame() {
        return new DriverTask( driver -> driver.switchTo().parentFrame() );
    }

    public static Performable toWindow(String windowTitle) {
        return new DriverTask( driver -> driver.switchTo().window(windowTitle) );
    }

    public static Performable toAlert() {
        return new DriverTask( driver -> driver.switchTo().alert() );
    }

    public static Performable toDefaultContext() {
        return new DriverTask( driver -> driver.switchTo().defaultContent() );
    }

    public static Performable toActiveElement() {
        return new DriverTask( driver -> driver.switchTo().activeElement() );
    }

}