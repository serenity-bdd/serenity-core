package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import serenitycore.net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

interface InstantiateDriver {
    WebDriver newDriver(DriverServicePool pool, DesiredCapabilities capabilities) throws ServicePoolError;
}