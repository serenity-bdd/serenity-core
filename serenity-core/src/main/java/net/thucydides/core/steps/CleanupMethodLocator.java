package net.thucydides.core.steps;

import net.thucydides.core.steps.service.CleanupMethodAnnotationProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.Arrays.stream;

public class CleanupMethodLocator {

    private final List<String> cleanupMethodsAnnotations = new ArrayList<>();

    public CleanupMethodLocator() {
        Iterable<CleanupMethodAnnotationProvider> cleanupMethodAnnotationProviders = ServiceLoader.load(CleanupMethodAnnotationProvider.class);
        for (CleanupMethodAnnotationProvider cleanupMethodAnnotationProvider : cleanupMethodAnnotationProviders) {
            cleanupMethodsAnnotations.addAll(cleanupMethodAnnotationProvider.getCleanupMethodAnnotations());
        }
    }

    public boolean currentMethodWasCalledFromACleanupMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stream(stackTrace).anyMatch(this::isAnnotatedWithAFixtureMethod);
    }

    private boolean isAnnotatedWithAFixtureMethod(StackTraceElement stackTraceElement) {
        try {
            Method method = Class.forName(stackTraceElement.getClassName()).getMethod(stackTraceElement.getMethodName());
            return (stream(method.getAnnotations()).anyMatch(
                    annotation -> (isAnAfterAnnotation(annotation.annotationType().getSimpleName())
                            || cleanupMethodsAnnotations.contains(annotation.toString()))
            ));
        } catch (Exception ignored) {
            return false;
        }
    }
    private boolean isAnAfterAnnotation(String annotationName) {
        return annotationName.startsWith("After");
    }

}
