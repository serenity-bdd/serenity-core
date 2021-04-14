package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;




public class InternetExplorerDriverCapabilities implements DriverCapabilitiesProvider
{
    @Override public DesiredCapabilities getCapabilities() {
        return new DesiredCapabilities(new InternetExplorerOptions());
    }
}
