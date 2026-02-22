package net.serenitybdd.model.buildinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
//import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.serenitybdd.model.di.ModelInfrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BuildInfo {

    private static Map<String, BuildInfoSection> buildInfoSections = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfo.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static BuildInfoSection section(String sectionName) {
        BuildInfo.load();
        return buildInfoSections.computeIfAbsent(sectionName, name -> new BuildInfoSection(name));
    }

    static void save() {
        File reportDir = ModelInfrastructure.getConfiguration().getOutputDirectory();
        // Check if the report directory exists, if not create it
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        File file = new File(reportDir, "buildInfo.json");
        try {
            objectMapper.writeValue(file, buildInfoSections);
        } catch (IOException e) {
            LOGGER.warn("Unable to save build info", e);
        }
    }

    public static void clear() {
        buildInfoSections.clear();
    }

    public static void load() {
        File reportDir = ModelInfrastructure.getConfiguration().getOutputDirectory();
        File file = new File(reportDir, "buildInfo.json");
        if (file.exists()) {
            try {
                Map<String, BuildInfoSection> loadedSections = objectMapper.readValue(file, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, BuildInfoSection.class));
                buildInfoSections.putAll(loadedSections);
            } catch (IOException e) {
                LOGGER.warn("Unable to load build info", e);
            }
        }
    }

    public static List<String> getSections() {
        return buildInfoSections.keySet().stream().sorted().collect(Collectors.toList());
    }

    public static Map<String, String> getSection(String sectionName) {
        return buildInfoSections.getOrDefault(sectionName, new BuildInfoSection(sectionName)).getValues();
    }

    public static class BuildInfoSection {
        private String sectionName;
        private Map<String, String> values = new HashMap<>();

        // No-args constructor for Jackson
        public BuildInfoSection() {
        }

        public BuildInfoSection(String sectionName) {
            this.sectionName = sectionName;
        }

        @JsonProperty("sectionName")
        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        @JsonProperty("values")
        public Map<String, String> getValues() {
            return values;
        }

        public void setValues(Map<String, String> values) {
            this.values = values;
        }

        public void setProperty(String property, String value) {
            values.put(property, value);
            BuildInfo.save();
        }
    }
}
