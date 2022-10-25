package net.thucydides.core.webdriver

import net.thucydides.core.webdriver.stubs.WebDriverStub
import org.openqa.selenium.WebDriver

class MyDriverSource implements DriverSource {

    @Override
    WebDriver newDriver() {
        return new WebDriverStub();
    }

    @Override
    boolean takesScreenshots() {
        return true
    }
}
