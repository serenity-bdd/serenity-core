package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.safari.SafariOptions;



public class SafariDriverCapabilities implements DriverCapabilitiesProvider
{
    @Override public MutableCapabilities getCapabilities() {
        return new SafariOptions();
    }
}
