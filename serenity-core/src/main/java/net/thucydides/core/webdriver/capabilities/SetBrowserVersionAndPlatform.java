package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SetBrowserVersionAndPlatform {
    private final DesiredCapabilities capabilities;

    public SetBrowserVersionAndPlatform(DesiredCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public static SetBrowserVersionAndPlatform from(DesiredCapabilities capabilities) {
        return new SetBrowserVersionAndPlatform(capabilities);
    }

    public void in(AbstractDriverOptions options) {
        if (!capabilities.getBrowserVersion().isEmpty()) {
            options.setBrowserVersion(capabilities.getBrowserVersion());
        }
        if (capabilities.getPlatformName() != null) {
            options.setPlatformName(capabilities.getPlatformName().name());
        }
    }
}
