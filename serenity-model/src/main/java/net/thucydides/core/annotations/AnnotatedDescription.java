package net.thucydides.core.annotations;

import java.util.Optional;

public class AnnotatedDescription {
    public static Optional<String> forClass(Class<?> clazz) {
        Description customDescription = clazz.getAnnotation(Description.class);
        if (customDescription != null) {
            return Optional.of(customDescription.value());
        }
        return Optional.empty();
    }
}
