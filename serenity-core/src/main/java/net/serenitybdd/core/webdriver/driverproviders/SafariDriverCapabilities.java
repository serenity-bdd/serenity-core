package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;



public class SafariDriverCapabilities implements DriverCapabilitiesProvider
{
    @Override
    public DesiredCapabilities getCapabilities() {
        return new DesiredCapabilities(new SafariOptions());
    }
}
