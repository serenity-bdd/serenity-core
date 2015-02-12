package net.serenitybdd.core.buildinfo;

import org.openqa.selenium.Capabilities;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by john on 12/02/15.
 */
public interface DriverCapabilityRecord {
    void registerCapabilities(String driver, Capabilities capabilities);
    List<String> getDrivers();
    Map<String,Properties> getDriverCapabilities();
}
