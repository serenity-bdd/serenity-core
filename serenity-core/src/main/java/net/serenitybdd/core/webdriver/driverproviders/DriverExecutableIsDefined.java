package net.serenitybdd.core.webdriver.driverproviders;

import org.openqa.selenium.os.ExecutableFinder;

public class DriverExecutableIsDefined {
    public static boolean bySystemProperty(String systemProperty) {
        String driverPath = System.getProperty(systemProperty);
        return (driverPath != null) && new ExecutableFinder().find(driverPath) != null;
    }
}
