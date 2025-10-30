package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Capabilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by john on 14/06/2016.
 */
public class CapabilitiesStub implements Capabilities {
    @Override
    public String getBrowserName() {
        return null;
    }

    @Override
    public Map<String, Object> asMap() {
        return new HashMap<>();
    }

    @Override
    public Object getCapability(String capabilityName) {
        return null;
    }

    @Override
    public boolean is(String capabilityName) {
        return false;
    }

    @Override
    public Capabilities merge(Capabilities other) {
        return this;
    }

    @Override
    public Set<String> getCapabilityNames() {
        return new HashSet<>();
    }
}
