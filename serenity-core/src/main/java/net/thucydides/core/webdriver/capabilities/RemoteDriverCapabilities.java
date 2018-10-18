package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.MutableCapabilities;

public interface RemoteDriverCapabilities {

    String getUrl();

    MutableCapabilities getCapabilities(MutableCapabilities capabilities);

}
