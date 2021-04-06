package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.safari.SafariOptions;


public class OperaDriverCapabilties implements DriverCapabilitiesProvider
{
    @Override public MutableCapabilities getCapabilities() {
        return new OperaOptions();
    }
}
