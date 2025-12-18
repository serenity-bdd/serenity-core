package net.serenitybdd.cucumber.util;

import io.cucumber.core.exception.CucumberException;
import io.cucumber.tagexpressions.Expression;
import org.junit.runners.ParentRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilities for Cucumber integration, containing functionality previously accessed
 * from Cucumber's package-private classes to ensure compatibility with Java module system.
 *
 * These implementations are duplicated from Cucumber 7.31.0's internal classes to avoid
 * split package issues when Cucumber adopts Java Platform Module System (JPMS).
 */
public class CucumberInternalUtils {

    private CucumberInternalUtils() {
        // Utility class
    }

    /**
     * Original implementation from io.cucumber.junit.Assertions in Cucumber 7.31.0
     */
    public static void assertNoCucumberAnnotatedMethods(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation.annotationType().getName().startsWith("io.cucumber")) {
                    throw new CucumberException("\n\n" +
                            "Classes annotated with @RunWith(Cucumber.class) must not define any\n" +
                            "Step Definition or Hook methods. Their sole purpose is to serve as\n" +
                            "an entry point for JUnit. Step Definitions and Hooks should be defined\n" +
                            "in their own classes. This allows them to be reused across features.\n" +
                            "Offending class: " + clazz + "\n");
                }
            }
        }
    }

    /**
     * Original implementation from io.cucumber.junit.FileNameCompatibleNames in Cucumber 7.31.0
     */
    public static <V, K> Integer uniqueSuffix(Map<K, List<V>> groupedByName, V pickle, Function<V, K> nameOf) {
        List<V> withSameName = groupedByName.get(nameOf.apply(pickle));
        boolean makeNameUnique = withSameName.size() > 1;
        return makeNameUnique ? withSameName.indexOf(pickle) + 1 : null;
    }

    /**
     * Original implementation from Serenity's io.cucumber.junit.FeatureRunnerExtractors
     */
    public static String extractFeatureName(ParentRunner<?> runner) {
        String displayName = runner.getDescription().getDisplayName();
        return displayName.substring(displayName.indexOf(":") + 1).trim();
    }

    /**
     * Original implementation from Serenity's io.cucumber.junit.FeatureRunnerExtractors
     */
    public static String featurePathFor(ParentRunner<?> featureRunner) {
        try {
            Field field = featureRunner.getDescription().getClass().getDeclaredField("fUniqueId");
            field.setAccessible(true);
            return field.get(featureRunner.getDescription()).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Original implementation from Serenity's io.cucumber.junit.LiteralExpression
     */
    public static class LiteralExpression implements Expression {

        private final String value;

        public LiteralExpression(String value) {
            this.value = value;
        }

        @Override
        public boolean evaluate(List<String> variables) {
            return variables.contains(this.value);
        }

        @Override
        public String toString() {
            return this.value.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
        }
    }
}
