package net.thucydides.core.util;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.tags.Taggable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * This is an INTERNAL helper class of serenity, it should not be used directly and may be subject to refactoring.
 * <p>
 * It serves to decouple serenity-model (and serenity-core) from JUnit. If JUnit is on the classpath, JUnit
 * classes will be handled specifically. But serenity will continue to function without JUnit being on the classpath.
 * Furthermore, users can choose between JUnit 4 and JUnit 5 or even use both together.
 * <p>
 * Ideally this approach could be generalized to avoid any special treatment of JUnit as a test framework.
 * Test-framework specific strategies could be registered from the framework-specific modules. That would allow
 * to place the implementation of (and the tests for) the test-framework specific strategies into the modules that know
 * about those frameworks and also have the necessary dependencies.
 * <p>
 * But this would be a more extensive change, potentially breaking backwards compatibility. So for now this class does
 * exactly only this: It provides an adapter for JUnit 4 and JUnit 5, and makes explicit the parts of the code where
 * there has previously been a hard dependency on JUnit 4.
 * <p>
 * As such it is self-contained and should be possible to be grasped rather easily. And it marks the starting point for
 * a potential refactoring towards a more general approach.
 */
public class JUnitAdapter {

    private static List<JUnitStrategy> strategies = new ArrayList<>();

    static {
        if (isClassPresent("org.junit.runner.RunWith")) {
            strategies.add(new JUnit4Strategy());
        }
        if (isClassPresent("org.junit.jupiter.api.Test")) {
            strategies.add(new JUnit5Strategy());
        }
    }

    // used only for testing purposes
    static List<JUnitStrategy> getStrategies() {
        return Collections.unmodifiableList(strategies);
    }

    public static boolean isTestClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isTestClass(testClass));
    }

    public static boolean isTestMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isTestMethod(method));
    }

    public static boolean isTestSetupMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isTestSetupMethod(method));
    }

    public static boolean isSerenityTestCase(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isSerenityTestCase(testClass));
    }

    public static boolean isAssumptionViolatedException(final Throwable throwable) {
        return delegateToStrategies(s -> s.isAssumptionViolatedException(throwable));
    }

    public static boolean isATaggableClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isATaggableClass(testClass));
    }

    public static boolean isIgnored(final Method method) {
        if (method == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isIgnored(method));
    }

    private static boolean delegateToStrategies(
            final Function<JUnitStrategy, Boolean> jUnitStrategyBooleanFunction) {
        return strategies.stream().map(jUnitStrategyBooleanFunction).filter(Boolean::booleanValue).findFirst()
                .orElse(false);
    }

    private static boolean isClassPresent(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private interface JUnitStrategy {

        boolean isTestClass(final Class<?> testClass);

        boolean isTestMethod(final Method method);

        boolean isTestSetupMethod(final Method method);

        boolean isSerenityTestCase(final Class<?> testClass);

        boolean isAssumptionViolatedException(final Throwable throwable);

        boolean isATaggableClass(final Class<?> testClass);

        boolean isIgnored(final Method method);

    }

    private static class JUnit4Strategy implements JUnitStrategy {

        private final List<String> LEGAL_SERENITY_RUNNER_NAMES
                = NewList.of("SerenityRunner", "ThucydidesRunner", "SerenityParameterizedRunner","ThucydidesParameterizedRunner");

        @Override
        public boolean isTestClass(final Class<?> testClass) {
            return (testClass.getAnnotation(org.junit.runner.RunWith.class) != null);
        }

        @Override
        public boolean isTestMethod(final Method method) {
            return (method.getAnnotation(org.junit.Test.class) != null);
        }

        @Override
        public boolean isTestSetupMethod(final Method method) {
            return (method.getAnnotation(org.junit.Before.class) != null)
                    || (method.getAnnotation(org.junit.BeforeClass.class) != null);
        }

        @Override
        public boolean isSerenityTestCase(Class<?> testClass) {
            org.junit.runner.RunWith runWithAnnotation = testClass.getAnnotation(org.junit.runner.RunWith.class);
            if (runWithAnnotation != null) {
                return LEGAL_SERENITY_RUNNER_NAMES.contains(runWithAnnotation.value().getSimpleName());
            }
            return false;
        }

        @Override
        public boolean isAssumptionViolatedException(final Throwable throwable) {
            return (throwable instanceof org.junit.internal.AssumptionViolatedException);
        }

        @Override
        public boolean isATaggableClass(Class<?> testClass) {
            org.junit.runner.RunWith runWith = testClass.getAnnotation(org.junit.runner.RunWith.class);
            return (runWith != null && Taggable.class.isAssignableFrom(runWith.value()));
        }

        @Override
        public boolean isIgnored(final Method method) {
            // intentionally left at previous implementation based on annotation name to change as little as possible
            Annotation[] annotations = method.getAnnotations();
            return Arrays.stream(annotations).anyMatch(
                    annotation -> annotation.annotationType().getSimpleName().equals("Ignore")
            );
        }

    }

    private static class JUnit5Strategy implements JUnitStrategy {

        @Override
        public boolean isTestClass(final Class<?> testClass) {
            for (final Method method : testClass.getDeclaredMethods()) {
                if (isTestMethod(method)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isTestMethod(final Method method) {
            return (method.getAnnotation(org.junit.jupiter.api.Test.class) != null);
        }

        @Override
        public boolean isTestSetupMethod(final Method method) {
            return (method.getAnnotation(org.junit.jupiter.api.BeforeEach.class) != null)
                    || (method.getAnnotation(org.junit.jupiter.api.BeforeAll.class) != null);
        }

        @Override
        public boolean isSerenityTestCase(Class<?> testClass) {
            return hasSerenityAnnotation(testClass, new HashSet<>());
        }

        @Override
        public boolean isIgnored(final Method method) {
            return (method.getAnnotation(Disabled.class) != null);
        }

        private boolean hasSerenityAnnotation(final Class<?> clazz, final Set<Class<?>> checked) {
            checked.add(clazz);
            return Arrays.stream(clazz.getAnnotations()).anyMatch(a -> carriesSerenityExtension(a, checked));
        }

        private boolean carriesSerenityExtension(final Annotation annotation, final Set<Class<?>> checked) {
            if (annotation instanceof ExtendWith) {
                return Arrays.stream(((ExtendWith) annotation).value())
                        .anyMatch(c -> c.getSimpleName().matches("Serenity.*Extension"));
            }
            Class<? extends Annotation> annotationType = annotation.annotationType();

            if (annotationType.getPackage().getName().startsWith("java.lang") // performance optimization
                    || checked.contains(annotation.annotationType()) // avoid endless loops
            ) {
                return false;
            }

            // find meta annotations
            return hasSerenityAnnotation(annotation.annotationType(), checked);
        }

        @Override
        public boolean isAssumptionViolatedException(final Throwable throwable) {
            return (throwable instanceof org.opentest4j.TestAbortedException);
        }

        @Override
        public boolean isATaggableClass(final Class<?> testClass) {
            // serenity tagging mechanism currently not supported for JUnit 5, since JUnit 5 has its own tagging
            // feature.
            return false;
        }

    }

}
