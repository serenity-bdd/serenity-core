package net.thucydides.core.webdriver.strategies;

import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by john on 25/06/2016.
 */
public interface DriverCapabilitiesProvider {
    DesiredCapabilities getCapabilities();
}
