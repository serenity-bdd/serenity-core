package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaOptions;




public class InternetExplorerDriverCapabilties implements DriverCapabilitiesProvider
{
    @Override public MutableCapabilities getCapabilities() {
        return new InternetExplorerOptions();
    }
}
