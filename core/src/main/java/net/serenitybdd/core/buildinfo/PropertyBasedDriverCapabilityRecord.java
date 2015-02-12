package net.serenitybdd.core.buildinfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import net.thucydides.core.webdriver.Configuration;
import org.openqa.selenium.Capabilities;

import java.io.*;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.nio.file.Files.newInputStream;

/**
 * Created by john on 12/02/15.
 */
public class PropertyBasedDriverCapabilityRecord implements DriverCapabilityRecord {

    private Configuration configuration;

    @Inject
    public PropertyBasedDriverCapabilityRecord(Configuration configuration) {
        this.configuration = configuration;
    }

    public void registerCapabilities(String driver, Capabilities capabilities) {

        Properties properties = new Properties();
        properties.setProperty("platform", capabilities.getPlatform().name());
        for (String capability : capabilities.asMap().keySet()) {
            if (capabilities.getCapability(capability) instanceof String) {
                properties.setProperty(capability, capabilities.getCapability(capability).toString());
            }
        }
        try {
            File browserProperties = new File(configuration.getOutputDirectory(), "browser-" + driver.toLowerCase() + ".properties");
            try (Writer writer = new FileWriter(browserProperties)) {
                properties.store(writer, "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getDrivers() {
        List<String> drivers = Lists.newArrayList();
        try (DirectoryStream<Path> stream = driverCapabilityRecords()) {
            for (Path file : stream) {
                String driverName = driverNameFrom(file);
                drivers.add(driverName);
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
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
        Map<String, Properties> driverCapabilities = Maps.newHashMap();
        try (DirectoryStream<Path> stream = driverCapabilityRecords()) {
            for (Path file : stream) {
                String driverName = driverNameFrom(file);
                Properties driverProperties = new Properties();
                driverProperties.load(newInputStream(file));
                driverCapabilities.put(driverName, driverProperties);
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
        return driverCapabilities;
    }

}
