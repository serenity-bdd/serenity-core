package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

interface InstantiateDriver {
    WebDriver newDriver(DriverServicePool pool, MutableCapabilities capabilities) throws ServicePoolError;
}