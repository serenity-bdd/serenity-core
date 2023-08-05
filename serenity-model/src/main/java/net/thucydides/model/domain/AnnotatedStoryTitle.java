package net.thucydides.model.domain;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Arrays.stream;

/**
 * Read the story name from the @DisplayName annotation if present
 */
public class AnnotatedStoryTitle {
    public static Optional<String> forClass(final Class<?> userStoryClass) {
        Optional<Annotation> displayNameAnnotation = stream(userStoryClass.getAnnotations())
                .filter(isCalledDisplayName())
                .findFirst();
        if (displayNameAnnotation.isPresent()) {
            try {
                String annotatedName = displayNameAnnotation.get().annotationType().getMethods()[0].invoke(displayNameAnnotation.get()).toString();
                return Optional.ofNullable(annotatedName);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private static Predicate<Annotation> isCalledDisplayName() {
        return annotation -> annotation.annotationType().getSimpleName().equals("DisplayName");
    }
}
