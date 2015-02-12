package net.serenitybdd.core.buildinfo;

import com.beust.jcommander.internal.Maps;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.guice.ThucydidesModule;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.CoreMatchers.startsWith;

/**
 * Created by john on 12/02/15.
 */
public class BuildInfoProvider {
    private final EnvironmentVariables environmentVariables;
    private final DriverCapabilityRecord driverCapabilityRecord;

    public BuildInfoProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.driverCapabilityRecord = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    public BuildProperties getBuildProperties() {
        Map<String, String> generalProperties = Maps.newHashMap();
        generalProperties.put("Default Driver",ThucydidesSystemProperty.DRIVER.from(environmentVariables,"firefox"));
        generalProperties.put("Operating System",System.getProperty("os.name") + " version " + System.getProperty("os.version"));
        addRemoteDriverPropertiesTo(generalProperties);
        addCustomPropertiesTo(generalProperties);

        List<String> drivers = driverCapabilityRecord.getDrivers();
        Map<String, Properties> driverPropertiesMap = driverCapabilityRecord.getDriverCapabilities();

        return new BuildProperties(generalProperties, drivers, driverPropertiesMap);
    }

    private void addRemoteDriverPropertiesTo(Map<String, String> buildProperties) {
        if (ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.isDefinedIn(environmentVariables)) {
            buildProperties.put("Remote driver", ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables));
            buildProperties.put("Remote browser version", ThucydidesSystemProperty.WEBDRIVER_REMOTE_BROWSER_VERSION.from(environmentVariables));
            buildProperties.put("Remote OS", ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS.from(environmentVariables));
        }
    }

    private void addCustomPropertiesTo(Map<String, String> buildProperties) {

        List<String> sysInfoKeys = filter(startsWith("sysinfo."), environmentVariables.getKeys());
        for(String key : sysInfoKeys) {
            String simplifiedKey = key.replace("sysinfo.", "");
            String expression = environmentVariables.getProperty(key);

            String value = evaluateGroovyExpression(expression);

            buildProperties.put(simplifiedKey, value);
        }

    }

    private String evaluateGroovyExpression(String expression) {
        Binding binding = new Binding();
        binding.setVariable("env", environmentVariables);
        GroovyShell shell = new GroovyShell(binding);
        return shell.evaluate(expression).toString();
    }

}
