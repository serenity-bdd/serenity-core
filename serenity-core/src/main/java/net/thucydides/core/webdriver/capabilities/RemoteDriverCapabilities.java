package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface RemoteDriverCapabilities {

    String getUrl();

    MutableCapabilities getCapabilities(MutableCapabilities capabilities);

}
