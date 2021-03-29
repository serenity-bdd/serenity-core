package serenitycore.net.thucydides.core.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import serenitycore.net.thucydides.core.webdriver.DriverSource

public class MyDriverSource implements DriverSource {

    @Override
    public WebDriver newDriver() {
        return new HtmlUnitDriver()
    }

    @Override
    boolean takesScreenshots() {
        return true
    }
}
