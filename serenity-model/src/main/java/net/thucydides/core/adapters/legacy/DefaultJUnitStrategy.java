package net.thucydides.core.adapters.legacy;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.adapters.JUnitStrategy;
import net.thucydides.core.annotations.*;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.tags.Taggable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;

class DefaultJUnitStrategy implements JUnitStrategy {


    public DefaultJUnitStrategy() {}

    private final List<String> LEGAL_SERENITY_RUNNER_NAMES
            = NewList.of("SerenityRunner", "ThucydidesRunner", "SerenityParameterizedRunner", "ThucydidesParameterizedRunner");

    @Override
    public boolean isTestClass(final Class<?> testClass) {
        return containsAnnotationCalled(testClass.getAnnotations(), "RunWith") || hasMethodWithTestAnnotation(testClass);
    }

    private boolean hasMethodWithTestAnnotation(final Class<?> testClass) {
        return stream(testClass.getMethods()).anyMatch(this::isTestMethod);
    }


    @Override
    public boolean isTestMethod(final Method method) {
        return containsAnnotationCalled(method.getAnnotations(), "Test");
    }

    @Override
    public boolean isTestSetupMethod(final Method method) {
        return containsAnnotationCalled(method.getAnnotations(), "Before")
                || containsAnnotationCalled(method.getAnnotations(), "BeforeClass");
    }

    private Optional<String> runWithValue(Class<?> testClass) {
        Optional<Annotation> runWith = stream(testClass.getAnnotations())
                .filter(annotation -> annotation.annotationType().getSimpleName().equals("RunWith"))
                .findAny();

        if (runWith.isPresent()) {
            Optional<Method> valueMethod = stream(runWith.get().getClass().getMethods())
                    .filter(method -> method.getName().equals("value"))
                    .findFirst();

            if (valueMethod.isPresent()) {
                try {
                    return Optional.of(valueMethod.get().invoke(runWith.get()).toString());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isSerenityTestCase(Class<?> testClass) {
        Optional<String> runWith = runWithValue(testClass);
        if (runWith.isPresent()) {
            String[] classElts = runWith.get().split("\\.");
            String className = classElts[classElts.length - 1];
            return LEGAL_SERENITY_RUNNER_NAMES.contains(className);
        }
        return false;
    }

    @Override
    public boolean isAssumptionViolatedException(final Throwable throwable) {
        return throwable.getClass().getSimpleName().contains("AssumptionViolatedException");
//        return (throwable instanceof org.junit.internal.AssumptionViolatedException);
    }

    @Override
    public boolean isATaggableClass(Class<?> testClass) {
        Optional<String> runWithValue = runWithValue(testClass);
        if (runWithValue.isPresent()) {
            try {
                Class<?> runWithClass = Class.forName(runWithValue.get());
                return Taggable.class.isAssignableFrom(runWithClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
 //       RunWith runWith = testClass.getAnnotation(RunWith.class);
//        return (runWith != null && Taggable.class.isAssignableFrom(runWith.value()));
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
            return Optional.of(titleAnnotation.value());
        }
        return Optional.empty();
    }

    @Override
    public List<TestTag> getTagsFor(Method testMethod) {
        List<TestTag> tags = new ArrayList<>();
        TestAnnotations.addTagValues(tags, testMethod.getAnnotation(WithTagValuesOf.class));
        TestAnnotations.addTags(tags, testMethod.getAnnotation(WithTags.class));
        TestAnnotations.addTag(tags, testMethod.getAnnotation(WithTag.class));
        return tags;
    }

    private static  boolean containsAnnotationCalled(Annotation[] annotations, String annotationName) {
        return stream(annotations).anyMatch(annotation -> annotation.annotationType().getSimpleName().equals(annotationName));
    }
}