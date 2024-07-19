package net.thucydides.core.adapters.junit4;

import net.serenitybdd.annotations.*;
import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.adapters.JUnitStrategy;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.tags.Taggable;
import org.junit.runner.RunWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;

class JUnit4Strategy implements JUnitStrategy {

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

    @Override
    public boolean isSerenityTestCase(Class<?> testClass) {
        RunWith runWithAnnotation = testClass.getAnnotation(RunWith.class);
        if (runWithAnnotation != null) {
            return LEGAL_SERENITY_RUNNER_NAMES.contains(runWithAnnotation.value().getSimpleName());
        }
        return false;
    }

    @Override
    public boolean isAssumptionViolatedException(final Throwable throwable) {
        return (throwable instanceof org.junit.AssumptionViolatedException);
    }

    @Override
    public boolean isATaggableClass(Class<?> testClass) {
        RunWith runWith = testClass.getAnnotation(RunWith.class);
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
