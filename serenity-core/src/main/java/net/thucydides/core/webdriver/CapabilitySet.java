package net.thucydides.core.webdriver;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_DRIVER_CAPABILITIES;

/**
 * A set of user-defined capabilities to be used to configure the WebDriver driver.
 * Capabilities should be passed in as a semi-colon-separated list of key:value pairs, e.g.
 * "build:build-1234; max-duration:300; single-window:true; tags:[tag1,tag2,tag3]"
 */
class CapabilitySet {
    private final EnvironmentVariables environmentVariables;
    private static final CharMatcher CAPABILITY_SEPARATOR = CharMatcher.anyOf(";");

    CapabilitySet(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables.copy();
    }

    public Map<String,Object> getCapabilities() {
        Map<String,Object> capabilitiesMap = new HashMap<>();

        String specifiedCapabilities = SERENITY_DRIVER_CAPABILITIES.from(environmentVariables);
        if (StringUtils.isNotEmpty(specifiedCapabilities)) {
            Iterable<String> capabilityValues = Splitter.on(CAPABILITY_SEPARATOR).trimResults().split(specifiedCapabilities);
            capabilitiesMap = addCapabilityMapValues(capabilityValues);
        }
        return capabilitiesMap;
    }

    private  Map<String,Object> addCapabilityMapValues(Iterable<String> capabilityValues) {
        Map<String,Object> capabilitiesMap = new HashMap<>();
        for(String capability : capabilityValues) {
            CapabilityToken token = new CapabilityToken(capability);
            if (token.isDefined()) {
                capabilitiesMap.put(token.getName(), CapabilityValue.asObject(token.getValue()));
            }
        }
        return capabilitiesMap;
    }

    private static class CapabilityToken {
        private final String name;
        private final String value;

        private CapabilityToken(String capability) {

            int colonIndex = capability.lastIndexOf(":");
            if (colonIndex >= 0)  {
                boolean colonIndexFound = false;
                int lastIndex = capability.length();
                while(!colonIndexFound) {
                    int lastColonIndex = capability.lastIndexOf(":", lastIndex);
                    if (lastColonIndex > 0) {
                        colonIndex = lastColonIndex;
                        if ((capability.length() >= colonIndex + 1) && isFollowedByPathSeparator(capability, colonIndex)) {
                            if (lastIndex == colonIndex - 1) {
                                colonIndexFound = true;
                                //been here before, only single colon followed by a path separator found
                                break;
                            }
                            lastIndex = colonIndex - 1;
                        } else {
                            colonIndexFound = true;
                        }
                    }
                    else {
                       colonIndexFound = true;
                    }
                }
                name = capability.substring(0, colonIndex);
                value = capability.substring(colonIndex + 1);
            } else {
                name = capability;
                value = null;
            }
        }

        private boolean isFollowedByPathSeparator(String capability, int colonIndex) {
            return (capability.charAt(colonIndex+1) =='\\') || (capability.charAt(colonIndex+1) =='/');
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isDefined() {
            return (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value));
        }
    }
}
