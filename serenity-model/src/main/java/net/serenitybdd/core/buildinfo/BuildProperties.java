package net.serenitybdd.core.buildinfo;

import net.serenitybdd.core.collect.NewMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by john on 12/02/15.
 */
public class BuildProperties {

    private final Map<String, String> generalProperties;
    private final List<String> drivers;
    private final Map<String, Properties> driverProperties;
    private final Map<String, Map<String, String>> sections;

    public BuildProperties(Map<String, String> generalProperties,
                           List<String> drivers,
                           Map<String, Properties> driverProperties,
                           Map<String, Map<String, String>> sections) {
        this.generalProperties = generalProperties;
        this.drivers = drivers;
        this.driverProperties = driverProperties;
        this.sections = sections;
    }

    public Map<String, String> getGeneralProperties() {
        return NewMap.copyOf(generalProperties);
    }

    public List<String> getDrivers() {
        return new ArrayList<>(drivers);
    }

    public Map<String, Properties> getDriverProperties() {
        return NewMap.copyOf(driverProperties);
    }


    public List<String> getSectionTitles() {
        return sections.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Map<String, Map<String, String>> getSections() {
        return sections;
    }
}
