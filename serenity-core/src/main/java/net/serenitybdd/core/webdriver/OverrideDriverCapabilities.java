package net.serenitybdd.core.webdriver;


import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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
    private static ThreadLocal<Boolean> OVERRIDE_DEFAULTS = ThreadLocal.withInitial(() -> FALSE);

    private String prefix;

    public OverrideDriverCapabilities(String prefix) {
        this.prefix = prefix;
    }

    public interface OverrideSetter {
        CapabilityBuilderChain setTo(Object value);
    }

    public interface CapabilityBuilderChain {
        OverrideSetter andProperty(String propertyName);
        CapabilityBuilderChain andOverrideDefaults();
    }

    public static OverrideSetter withProperty(String propertyName) {
        return new OverrideDriverCapabilitiesBuilder(propertyName);
    }

    public static void clear() {
        OVERRIDDEN_DRIVER_CAPABILITIES.get().clear();
        OVERRIDE_DEFAULTS.set(FALSE);
    }

    public static Map<String, Object> getProperties() {
        return new HashMap<>(OVERRIDDEN_DRIVER_CAPABILITIES.get());
    }

    public static boolean shouldOverrideDefaults() {
        return OVERRIDE_DEFAULTS.get();
    }

    public static class OverrideDriverCapabilitiesBuilder implements OverrideSetter, CapabilityBuilderChain {
        private String propertyName;

        OverrideDriverCapabilitiesBuilder(String propertyName) {
            this.propertyName = propertyName;
        }

        public CapabilityBuilderChain setTo(Object value) {
            OVERRIDDEN_DRIVER_CAPABILITIES.get().put(propertyName, value);
            return this;
        }

        public CapabilityBuilderChain andOverrideDefaults() {
            OVERRIDE_DEFAULTS.set(TRUE);
            return this;
        }

        public OverrideSetter andProperty(String propertyName) {
            this.propertyName = propertyName;
            return this;
        }
    }
}
