package net.thucydides.core.webdriver.capabilities;

import java.util.Map;

public class NestedMap {
    private final String capabilityName;

    public NestedMap(String capabilityName) {
        this.capabilityName = capabilityName;
    }

    public static NestedMap called(String capabilityName) {
        return new NestedMap(capabilityName);
    }

    public Map<String, Object> from(Object capabilities) {
        if (!(capabilities instanceof Map)) {
            throw new InvalidCapabilityException("Invalid W3C capability: " + capabilityName + " should be a map but was " + capabilities);
        }
        return (Map<String, Object>) ((Map) capabilities).get(capabilityName);
    }
}
