package net.thucydides.junit.rules;

import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Conserves the webdriver-related system properties (webdriver.*).
 * If they were defined, they will be restored to their old values.
 * If they where not defined before a test, they will be deleted.
 *
 * @author johnsmart
 */
public class DisableThucydidesHistoryRule implements MethodRule {

    private final class RestoreThucydidesLogLevelStatement extends Statement {
        private final Statement statement;
        private final String originalValue;

        private RestoreThucydidesLogLevelStatement(final Statement statement,
                                                   final String originalValue) {
            super();
            this.statement = statement;
            this.originalValue = originalValue;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                statement.evaluate();
            } finally {
                restoreOldLogLevel();
            }
        }

        private void restoreOldLogLevel() {
            if (originalValue != null) {
                environmentVariables.setProperty("serenity.store.history", originalValue);
            } else {
                environmentVariables.clearProperty("serenity.store.history");
            }
        }
    }

    EnvironmentVariables environmentVariables;
    private final String originalValue;

    public DisableThucydidesHistoryRule() {
        environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables() ;
        originalValue = environmentVariables.getProperty("serenity.store.history");
        environmentVariables.setProperty("serenity.store.history","false");
    }

    public Statement apply(final Statement statement, final FrameworkMethod method, final Object target) {
        return new RestoreThucydidesLogLevelStatement(statement, originalValue);
    }
}
