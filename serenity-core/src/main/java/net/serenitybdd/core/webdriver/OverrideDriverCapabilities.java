package net.serenitybdd.core.webdriver;


import java.util.HashMap;
import java.util.Map;

/**
 * This class allows you to override driver capabilities defined in the Serenity configuration files at runtime.
 * This should be done before a new web page is opened, and will last for the duration of the test.
 * Sample usage:
 * <p>
 *     <pre>
 *         <code>OverrideDriverCapabilities.withProperty("browser").setTo("Chrome");</code>
 *     </pre>
 * </p>
 */
public class OverrideDriverCapabilities {

    private static ThreadLocal<Map<String,Object>> OVERRIDDEN_DRIVER_CAPABILITIES = ThreadLocal.withInitial(HashMap::new);

    public static OverrideDriverCapabilitiesBuilder withProperty(String propertyName) {
        return new OverrideDriverCapabilitiesBuilder(propertyName);
    }

    public static void clear() {
        OVERRIDDEN_DRIVER_CAPABILITIES.get().clear();
    }

    public static Map<String, Object> getProperties() {
        return new HashMap<>(OVERRIDDEN_DRIVER_CAPABILITIES.get());
    }

    public static class OverrideDriverCapabilitiesBuilder {
        private String propertyName;

        public OverrideDriverCapabilitiesBuilder(String propertyName) {
            this.propertyName = propertyName;
        }

        public void setTo(Object value) {
            OVERRIDDEN_DRIVER_CAPABILITIES.get().put(propertyName, value);
        }
    }
}
