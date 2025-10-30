package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

interface InstantiateDriver {
    WebDriver newDriver(DriverServicePool pool, Capabilities capabilities) throws ServicePoolError;
}