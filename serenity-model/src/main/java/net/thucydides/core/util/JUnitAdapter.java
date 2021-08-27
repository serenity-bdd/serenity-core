package net.thucydides.core.util;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.annotations.*;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.tags.Taggable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.stream;

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

    private static Optional<JUnit4Strategy> jUnit4Strategy = Optional.empty();

    private static Optional<JUnit5Strategy> jUnit5Strategy = Optional.empty();

    static {
        if (isClassPresent("org.junit.runner.RunWith")) {
            JUnit4Strategy strategy = new JUnit4Strategy();
            jUnit4Strategy = Optional.of(strategy);
            strategies.add(strategy);
        }
        if (isClassPresent("org.junit.jupiter.api.Test")) {
            JUnit5Strategy strategy = new JUnit5Strategy();
            jUnit5Strategy = Optional.of(strategy);
            strategies.add(strategy);
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

    public static Optional<String> getTitleAnnotation(Method testMethod) {
        if(jUnit4Strategy.isPresent()) {
            Optional<String> junit4Title = jUnit4Strategy.flatMap(strategy -> strategy.getTitleAnnotation(testMethod));
            if (junit4Title.isPresent()) {
                return junit4Title;
            }
        }
        if(jUnit5Strategy.isPresent()) {
            return jUnit5Strategy.flatMap(strategy -> strategy.getTitleAnnotation(testMethod));
        }
        return Optional.empty();
    }

    public static List<TestTag> getTagsFor(Method testMethod) {
        List<TestTag> allTags = new ArrayList<>();
        if(jUnit4Strategy.isPresent()) {
           List<TestTag> testTagsJUnit4 = jUnit4Strategy.get().getTagsFor(testMethod);
           allTags.addAll(testTagsJUnit4);
        }
        if(jUnit5Strategy.isPresent()) {
            List<TestTag> testTagsJUnit5 = jUnit5Strategy.get().getTagsFor(testMethod);
            allTags.addAll(testTagsJUnit5);
        }
        return allTags;
    }

    private interface JUnitStrategy {

        boolean isTestClass(final Class<?> testClass);

        boolean isTestMethod(final Method method);

        boolean isTestSetupMethod(final Method method);

        boolean isSerenityTestCase(final Class<?> testClass);

        boolean isAssumptionViolatedException(final Throwable throwable);

        boolean isATaggableClass(final Class<?> testClass);

        boolean isIgnored(final Method method);

        Optional<String> getTitleAnnotation(Method testMethod);

        List<TestTag> getTagsFor(Method testMethod);
    }

    private static class JUnit4Strategy implements JUnitStrategy {

        private final List<String> LEGAL_SERENITY_RUNNER_NAMES
                = NewList.of("SerenityRunner", "ThucydidesRunner", "SerenityParameterizedRunner","ThucydidesParameterizedRunner");

        @Override
        public boolean isTestClass(final Class<?> testClass) {
            return containsAnnotationCalled(testClass.getAnnotations(), "RunWith");
//            return (testClass.getAnnotation(org.junit.runner.RunWith.class) != null);
        }


        @Override
        public boolean isTestMethod(final Method method) {
            return containsAnnotationCalled(method.getAnnotations(), "Test");
//            return (method.getAnnotation(org.junit.Test.class) != null);
        }

        @Override
        public boolean isTestSetupMethod(final Method method) {
            return containsAnnotationCalled(method.getAnnotations(), "Before")
                    || containsAnnotationCalled(method.getAnnotations(), "BeforeClass");
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
            return stream(annotations).anyMatch(
                    annotation -> annotation.annotationType().getSimpleName().equals("Ignore")
            );
        }

        @Override
        public Optional<String> getTitleAnnotation(Method testMethod) {
            Title titleAnnotation = testMethod.getAnnotation(Title.class);
            if (titleAnnotation != null) {
                return java.util.Optional.of(titleAnnotation.value());
            }
            return java.util.Optional.empty();
        }


        @Override
        public List<TestTag> getTagsFor(Method testMethod) {
            List<TestTag> tags = new ArrayList<>();
            TestAnnotations.addTagValues(tags, testMethod.getAnnotation(WithTagValuesOf.class));
            TestAnnotations.addTags(tags, testMethod.getAnnotation(WithTags.class));
            TestAnnotations.addTag(tags, testMethod.getAnnotation(WithTag.class));
            return tags;
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
            //JUnit5 nested tests
            for(Class innerClass : testClass.getDeclaredClasses()) {
                if(isTestClass(innerClass) && isNestedTestClass(innerClass)){
                    return true;
                }
            }
            return false;
        }

        private boolean isNestedTestClass(Class testClass) {
            return (testClass.getAnnotation(org.junit.jupiter.api.Nested.class) != null);
        }

        @Override
        public boolean isTestMethod(final Method method) {

            boolean testMethod =  (method.getAnnotation(org.junit.jupiter.api.Test.class) != null) ||
                       // containsAnnotationCalled(method.getAnnotations(), "ParameterizedTest");
                    (method.getAnnotation(org.junit.jupiter.params.ParameterizedTest.class) != null);
            return testMethod;
        }

        @Override
        public boolean isTestSetupMethod(final Method method) {
            return (method.getAnnotation(org.junit.jupiter.api.BeforeEach.class) != null)
                    || (method.getAnnotation(org.junit.jupiter.api.BeforeAll.class) != null);
        }

        @Override
        public boolean isSerenityTestCase(Class<?> testClass) {
            return stream(testClass.getDeclaredMethods()).anyMatch(this::isTestMethod);
        }

        @Override
        public boolean isIgnored(final Method method) {
            return stream(method.getAnnotations()).anyMatch(annotation -> annotation.annotationType().getName().contains("Disabled"));
            //return (method.getAnnotation(Disabled.class) != null);
        }


        private boolean hasSerenityAnnotation(final Class<?> clazz, final Set<Class<?>> checked) {
            checked.add(clazz);
            return stream(clazz.getAnnotations()).anyMatch(a -> carriesSerenityExtension(a, checked));
        }

        private boolean carriesSerenityExtension(final Annotation annotation, final Set<Class<?>> checked) {
            if (annotation instanceof ExtendWith) {
                return stream(((ExtendWith) annotation).value())
                        .anyMatch(c -> c.getSimpleName().matches("Serenity.*Extension"));
//            if (annotation.annotationType().getName().contains("ExtendsWith")) {
//                if (hasValueMethod(annotation)) {
//                    return (valueOf(annotation).matches("Serenity.*Extension"));
//                }
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

        @Override
        public Optional<String> getTitleAnnotation(Method testMethod) {
            DisplayName displayNameAnnotation = testMethod.getAnnotation(DisplayName.class);
            if (displayNameAnnotation != null) {
                return java.util.Optional.of(displayNameAnnotation.value());
            }
            return java.util.Optional.empty();
        }

        @Override public List<TestTag> getTagsFor(Method testMethod) {

            List<TestTag> tags = new ArrayList<>();
            for(Annotation currentAnnotation : testMethod.getDeclaredAnnotations()) {
                if(currentAnnotation instanceof Tag) {
                    tags.add(TestTag.withValue(((Tag)currentAnnotation).value()));
                }
                if(currentAnnotation instanceof Tags) {
                    Tag[] allTags = ((Tags) currentAnnotation).value();
                    Arrays.stream(allTags).forEach(tag->tags.add(TestTag.withValue(tag.value())));
                }
            }
            return tags;
        }
    }

    private static  boolean containsAnnotationCalled(Annotation[] annotations, String annotationName) {
            return stream(annotations).anyMatch(annotation -> annotation.annotationType().getSimpleName().equals(annotationName));
    }
}
