package net.thucydides.core.webdriver;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_DRIVER_CAPABILITIES;

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
        Map<String,Object> capabilitiesMap = Maps.newHashMap();

        String specifiedCapabilities = THUCYDIDES_DRIVER_CAPABILITIES.from(environmentVariables);
        if (StringUtils.isNotEmpty(specifiedCapabilities)) {
            Iterable<String> capabilityValues = Splitter.on(CAPABILITY_SEPARATOR).trimResults().split(specifiedCapabilities);
            capabilitiesMap = addCapabilityMapValues(capabilityValues);
        }
        return capabilitiesMap;
    }

    private  Map<String,Object> addCapabilityMapValues(Iterable<String> capabilityValues) {
        Map<String,Object> capabilitiesMap = Maps.newHashMap();
        for(String capability : capabilityValues) {
            CapabilityToken token = new CapabilityToken(capability);
            if (token.isDefined()) {
                capabilitiesMap.put(token.getName(), asObject(token.getValue()));
            }
        }
        return capabilitiesMap;
    }

    private Object asObject(String value) {
        if (StringUtils.isNumeric(value))  {
            return Integer.parseInt(value);
        }
        if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
            return Boolean.parseBoolean(value);
        }
        if (isAList(value)) {
            return asList(value);
        }
        return value;
    }

    private List<Object> asList(String value) {
        String listContents = StringUtils.removeEnd(StringUtils.removeStart(value,"["),"]");
        List<String> items = Lists.newArrayList(Splitter.on(",").trimResults().split(listContents));
        return convert(items, toObject());
    }

    private Converter<String, Object> toObject() {
        return new Converter<String, Object>() {

            public Object convert(String from) {
                return asObject(from);
            }
        };
    }

    private boolean isAList(String value) {
        return value.startsWith("[") && value.endsWith("]");
    }

    private static class CapabilityToken {
        private final String name;
        private final String value;

        private CapabilityToken(String capability) {
            int colonIndex = capability.indexOf(":");
            if (colonIndex >= 0)  {
                name = capability.substring(0, colonIndex);
                value = capability.substring(colonIndex + 1);
            } else {
                name = capability;
                value = null;
            }
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
