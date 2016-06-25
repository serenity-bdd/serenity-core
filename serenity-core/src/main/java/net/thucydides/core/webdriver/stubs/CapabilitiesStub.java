package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;

import java.util.Map;

/**
 * Created by john on 14/06/2016.
 */
public class CapabilitiesStub implements Capabilities {
    @Override
    public String getBrowserName() {
        return null;
    }

    @Override
    public Platform getPlatform() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public boolean isJavascriptEnabled() {
        return false;
    }

    @Override
    public Map<String, ?> asMap() {
        return null;
    }

    @Override
    public Object getCapability(String capabilityName) {
        return null;
    }

    @Override
    public boolean is(String capabilityName) {
        return false;
    }
}
