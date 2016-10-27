package net.serenitybdd.core.webdriver.servicepools;

import java.io.File;

public class DriverPathConfiguration {
    private final String propertyName;

    public DriverPathConfiguration(String propertyName) {
        this.propertyName = propertyName;
    }

    public static DriverPathConfiguration updateSystemProperty(String propertyName) {
        return new DriverPathConfiguration(propertyName);
    }

    public void withExecutablePath(File executablePath) {
        System.setProperty(propertyName, executablePath.getAbsolutePath());
    }
}
