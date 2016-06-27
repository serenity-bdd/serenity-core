package net.thucydides.core.webdriver.strategies;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.phantomjs.PhantomJSCapabilityEnhancer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;

public class PhantomJSDriverBuilder implements DriverBuilder {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer defaultCapabilitiyEnhancer;
    private final DriverCapabilityRecord driverProperties;

    public PhantomJSDriverBuilder(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.defaultCapabilitiyEnhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        PhantomJSCapabilityEnhancer enhancer = new PhantomJSCapabilityEnhancer(environmentVariables);
        enhancer.enhanceCapabilities(capabilities);
        PhantomJSDriver driver = new PhantomJSDriver(defaultCapabilitiyEnhancer.enhanced(capabilities));
        driverProperties.registerCapabilities("phantomjs", driver.getCapabilities());
        return driver;
    }

    private void setPhantomJSPathIfNotSet() {
        if (!phantomJSIsAvailable()) {
            String phantomJSPath = environmentVariables.getProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY);
            String phantomJSPathEnvironmentProperty = System.getenv("PHANTOMJS_BINARY_PATH");
            if (StringUtils.isNotEmpty(phantomJSPath)) {
                System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJSPath);
            } else if (StringUtils.isNotEmpty(phantomJSPathEnvironmentProperty)) {
                System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJSPathEnvironmentProperty);
            }
        }
    }
    private boolean phantomJSIsAvailable() {
        try {
            return (executeCommand("phantomjs -v") != 0);
        } catch (IOException e) {
            return false;
        }
    }

    private int executeCommand(String command) throws IOException {
        Process cmdProc = Runtime.getRuntime().exec(command);
        try {
            return cmdProc.waitFor();
        } catch (InterruptedException e) {
            return -1;
        }
    }

}
