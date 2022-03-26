package net.thucydides.core.adapters.junit5;

import net.thucydides.core.adapters.JUnitStrategy;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import net.thucydides.core.model.TestTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

class JUnit5Strategy implements JUnitStrategy {

    @Override
    public boolean isTestClass(final Class<?> testClass) {
        if (hasTestMethods(testClass)) {
            return true;
        }
        if (!hasNestedTestClasses(testClass)) {
            return false;
        }
        return stream(testClass.getDeclaredClasses()).anyMatch(this::isTestClass);

        //JUnit5 nested tests
//        for (Class innerClass : testClass.getDeclaredClasses()) {
//            if (isTestClass(innerClass) && isNestedTestClass(innerClass)) {
//                return true;
//            }
//        }
//        return false;
    }

    private boolean hasNestedTestClasses(final Class<?> testClass) {
        return stream(testClass.getDeclaredClasses()).anyMatch(this::isNestedTestClass);
    }

    private boolean hasTestMethods(final Class<?> testClass) {
        return stream(testClass.getDeclaredMethods()).anyMatch(this::isTestMethod);
    }

    private boolean isNestedTestClass(Class testClass) {
        return (testClass.getAnnotation(org.junit.jupiter.api.Nested.class) != null);
    }

    @Override
    public boolean isTestMethod(final Method method) {

        boolean testMethod = (method.getAnnotation(org.junit.jupiter.api.Test.class) != null) ||
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
            return Optional.of(displayNameAnnotation.value());
        }
        return Optional.empty();
    }

    @Override
    public List<TestTag> getTagsFor(Method testMethod) {

        List<TestTag> tags = new ArrayList<>();
        for (Annotation currentAnnotation : testMethod.getDeclaredAnnotations()) {
            if (currentAnnotation instanceof WithTag) {
                String name = ((WithTag) currentAnnotation).name();
                String type = ((WithTag) currentAnnotation).type();
                String value = ((WithTag) currentAnnotation).value();
                if (name != null) {
                    tags.add(TestTag.withName(name).andType(type));
                } else {
                    tags.add(TestTag.withValue(value));
                }
            }
            if (currentAnnotation instanceof WithTags) {
                List<TestTag> testTags = stream(((WithTags) currentAnnotation).value()).map(
                        tag -> {
                            String name = tag.name();
                            String type = tag.type();
                            String value = tag.value();
                            if (name != null) {
                                return TestTag.withName(name).andType(type);
                            } else {
                                return TestTag.withValue(value);
                            }
                        }
                ).collect(Collectors.toList());
                tags.addAll(testTags);
            }
            if (currentAnnotation instanceof Tag) {
                tags.add(TestTag.withValue(((Tag) currentAnnotation).value()));
            }
            if (currentAnnotation instanceof Tags) {
                Tag[] allTags = ((Tags) currentAnnotation).value();
                Arrays.stream(allTags).forEach(tag -> tags.add(TestTag.withValue(tag.value())));
            }
        }
        return tags;
    }
}