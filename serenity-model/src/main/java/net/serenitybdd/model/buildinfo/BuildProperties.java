package net.serenitybdd.model.buildinfo;

import java.util.*;
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
        this.sections = sorted(sections);
    }

    private Map<String, Map<String, String>> sorted(Map<String, Map<String, String>> sections) {
        Map<String, Map<String, String>> sortedSections = new TreeMap<>();
        sections.forEach(
                (key, section) -> {
                    Map<String, String> sortedSectionProperties = new TreeMap<>(section);
                    sortedSections.put(key, sortedSectionProperties);
                }
        );
        return sortedSections;
    }

    public Map<String, String> getGeneralProperties() {
        return new TreeMap(generalProperties);
    }

    public List<String> getDrivers() {
        return new ArrayList<>(drivers);
    }

    public Map<String, Properties> getDriverProperties() {
        return new TreeMap(driverProperties);
    }


    public List<String> getSectionTitles() {
        return sections.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Map<String, Map<String, String>> getSections() {
        return sections;
    }
}
