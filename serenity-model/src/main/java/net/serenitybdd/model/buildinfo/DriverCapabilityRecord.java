package net.serenitybdd.model.buildinfo;



import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by john on 12/02/15.
 */
public interface DriverCapabilityRecord {
    void registerCapabilities(String driver, Properties capabilitiesAsProperties);
    List<String> getDrivers();
    Map<String,Properties> getDriverCapabilities();
}
