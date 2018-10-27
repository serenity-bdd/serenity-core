package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface RemoteDriverCapabilities {

    String getUrl();

    DesiredCapabilities getCapabilities(DesiredCapabilities capabilities);

}
