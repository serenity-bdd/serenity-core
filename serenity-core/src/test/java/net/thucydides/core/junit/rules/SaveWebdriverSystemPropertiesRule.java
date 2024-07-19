package net.thucydides.core.junit.rules;

import net.thucydides.model.ThucydidesSystemProperty;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * Conserves the webdriver-related system properties (webdriver.*).
 * If they were defined, they will be restored to their old values.
 * If they where not defined before a test, they will be deleted.
 * @author johnsmart
 *
 */
public class SaveWebdriverSystemPropertiesRule implements MethodRule {

    private final class RestorePropertiesStatement extends Statement {
        private final Statement statement;
        private final ThreadLocal<Map<String,String>> originalValues;

        private RestorePropertiesStatement(final Statement statement,
                                           final Map<String,String> originalValues) {
            super();
            this.statement = statement;
            this.originalValues = new ThreadLocal<Map<String,String>>();
            this.originalValues.set(originalValues);
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                statement.evaluate();
            } finally {
                restoreOldSystemProperties();
            }
        }

        private void restoreOldSystemProperties() {

            for (ThucydidesSystemProperty property : ThucydidesSystemProperty.values()) {
                restorePropertyValueFor(property);
            }
        }

        private void restorePropertyValueFor(final ThucydidesSystemProperty property) {
            String propertyName = property.getPropertyName();
            if (originalValues.get().containsKey(propertyName)) {
                String originalValue = originalValues.get().get(propertyName);
                System.setProperty(propertyName, originalValue);
            } else {
                System.clearProperty(propertyName);
            }
        }
    }

    private final Map<String,String> originalValues;

    public SaveWebdriverSystemPropertiesRule() {
        originalValues = saveWebdriverSystemProperties();
    }

    private Map<String,String> saveWebdriverSystemProperties() {
        Map<String,String> systemPropertyValues = new HashMap<String, String>();
        for (ThucydidesSystemProperty property : ThucydidesSystemProperty.values()) {
            savePropertyValueFor(property, systemPropertyValues);
        }
        return systemPropertyValues;
    }

    private static void savePropertyValueFor(final ThucydidesSystemProperty property,
                                             final Map<String,String> originalValues) {
        String propertyName = property.getPropertyName();
        String currentValue = System.getProperty(propertyName);
        if (currentValue != null) {
            originalValues.put(propertyName, currentValue);
        }
    }

    public Statement apply(final Statement statement, final FrameworkMethod method, final Object target) {
        return new RestorePropertiesStatement(statement, originalValues);
    }
}
