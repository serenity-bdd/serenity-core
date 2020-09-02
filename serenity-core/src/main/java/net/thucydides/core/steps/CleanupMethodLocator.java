package net.thucydides.core.steps;

import net.thucydides.core.steps.service.CleanupMethodAnnotationProvider;

import java.lang.reflect.Method;
import java.util.*;

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

    private List<String> DEFAULT_CLEANUP_PACKAGES_TO_SKIP = Arrays.asList(
            "java.lang",
            "net.serenitybdd.core",
            "net.thucydides",
            "org.junit",
            "org.openqa.selenium");

    private boolean isAnnotatedWithAFixtureMethod(StackTraceElement stackTraceElement) {
        try {
            if (inPackageToSkip(stackTraceElement.getClassName())) {
                return false;
            }
            Optional<Method> fixtureMethod = stream(forName(stackTraceElement.getClassName()).getMethods())
                                                            .filter(method -> method.getName().equals(stackTraceElement.getMethodName())).findFirst();

            if (!fixtureMethod.isPresent()) {
                return false;
            }
            return (stream(fixtureMethod.get().getAnnotations()).anyMatch(
                    annotation -> (isAnAfterAnnotation(annotation.annotationType().getSimpleName())
                            || cleanupMethodsAnnotations.contains(annotation.toString()))
            ));
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private boolean inPackageToSkip(String className) {
        return DEFAULT_CLEANUP_PACKAGES_TO_SKIP.stream().anyMatch(className::startsWith);
    }

    private static Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, null);
    }

    private static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) try {
            // Check the thread's class loader
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return Class.forName(className, false, classLoader);
            }
        } catch (ClassNotFoundException e) {
            // not found, use the class' loader
            classLoader = null;
        }
        if (classLoader != null) {
            return Class.forName(className, false, classLoader);
        }
        return Class.forName(className);
    }

    private boolean isAnAfterAnnotation(String annotationName) {
        return annotationName.startsWith("After");
    }

}
