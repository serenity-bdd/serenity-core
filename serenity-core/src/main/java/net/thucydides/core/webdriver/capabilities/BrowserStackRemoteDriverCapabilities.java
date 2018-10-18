package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.MutableCapabilities;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Provides BrowserStack specific capabilities
 *
 * @author Imran Khan
 */

public class BrowserStackRemoteDriverCapabilities implements RemoteDriverCapabilities {

    private final EnvironmentVariables environmentVariables;

    public BrowserStackRemoteDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    @Override
    public String getUrl() {
        return environmentVariables.injectSystemPropertiesInto(ThucydidesSystemProperty.BROWSERSTACK_URL.from(environmentVariables));
    }

    @Override
    public MutableCapabilities getCapabilities(MutableCapabilities capabilities) {
        configureBrowserStackCapabilities(capabilities);
        return capabilities;
    }

    private void configureBrowserStackCapabilities(MutableCapabilities capabilities) {
        Optional<String> guessedTestName = RemoteTestName.fromCurrentTest();
        guessedTestName.ifPresent(
                name -> capabilities.setCapability("name", name)
        );

        AddCustomCapabilities.startingWith("browserstack.").from(environmentVariables).withAndWithoutPrefixes().to(capabilities);

        String remotePlatform = environmentVariables.getProperty("remote.platform");
        if (isNotEmpty(remotePlatform)) {
            capabilities.setCapability(CapabilityType.PLATFORM, Platform.valueOf(remotePlatform));
        }
    }


}
