package net.serenitybdd.model.buildinfo;


import net.thucydides.model.webdriver.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.Files.newInputStream;

/**
 * Created by john on 12/02/15.
 */
public class PropertyBasedDriverCapabilityRecord implements DriverCapabilityRecord {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyBasedDriverCapabilityRecord.class);

    private final Configuration configuration;

    
    public PropertyBasedDriverCapabilityRecord(Configuration configuration) {
        this.configuration = configuration;
    }

    public void registerCapabilities(String driver, Properties capabilitiesAsProperties) {

        try {
            File browserProperties = new File(configuration.getOutputDirectory(), "browser-" + driver.toLowerCase() + ".properties");
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(browserProperties), StandardCharsets.UTF_8))  {
                capabilitiesAsProperties.store(writer, "");
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to store browser configuration for " + capabilitiesAsProperties);
        }
    }

    @Override
    public List<String> getDrivers() {
        List<String> drivers = new ArrayList<>();
        try (DirectoryStream<Path> stream = driverCapabilityRecords()) {
            for (Path file : stream) {
                drivers.add(driverNameFrom(file));
            }
        } catch (IOException | DirectoryIteratorException x) {
            LOGGER.error("Exception during getting drivers", x);
        }
        return drivers;
    }

    private String driverNameFrom(Path file) {
        return file.getFileName().toString().replace("browser-","").replace(".properties","");
    }

    private DirectoryStream<Path> driverCapabilityRecords() throws IOException {
        Path outputDirectory = configuration.getOutputDirectory().toPath();
        return Files.newDirectoryStream(outputDirectory,"browser-*.properties");
    }

    @Override
    public Map<String, Properties> getDriverCapabilities() {
        Map<String, Properties> driverCapabilities = new HashMap();
        try (DirectoryStream<Path> stream = driverCapabilityRecords()) {
            for (Path file : stream) {
                String driverName = driverNameFrom(file);
                Properties driverProperties = new Properties();
                try(InputStream properties = newInputStream(file)) {
                    driverProperties.load(properties);
                }
                driverCapabilities.put(driverName, driverProperties);
            }
        } catch (IOException | DirectoryIteratorException x) {
            LOGGER.error("Exception during getting driver capabilities",x);
        }
        return driverCapabilities;
    }

}
