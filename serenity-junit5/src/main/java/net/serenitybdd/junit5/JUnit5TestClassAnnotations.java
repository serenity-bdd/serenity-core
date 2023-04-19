package net.serenitybdd.junit5;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public class JUnit5TestClassAnnotations {

    private static final Logger logger = LoggerFactory.getLogger(JUnit5TestClassAnnotations.class);
    private final Class<?> testClass;

    private JUnit5TestClassAnnotations(final Class<?> testClass) {
        this.testClass = testClass;
    }

    public static JUnit5TestClassAnnotations forTest(Class<?> testClass) {
        return new JUnit5TestClassAnnotations(testClass);
    }

    public Optional<String> getDisplayNameGeneration(Method javaMethod) {

        Annotation displayNameGenerationAnnotation = testClass.getAnnotation(DisplayNameGeneration.class);
        if (displayNameGenerationAnnotation != null) {
            DisplayNameGenerator nameGenerator = DisplayNameGenerator.getDisplayNameGenerator(testClass.getAnnotation(DisplayNameGeneration.class).value());
            return Optional.of(nameGenerator.generateDisplayNameForMethod(testClass, javaMethod));
        }
        return null;
    }
}