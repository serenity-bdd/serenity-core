package net.thucydides.core.requirements.annotations;


import net.thucydides.core.annotations.Narrative;

import java.util.Optional;

public class NarrativeFinder {
    public static Optional<Narrative> forClass(Class<?> annotatedClass) {
        return Optional.ofNullable(annotatedClass.getAnnotation(Narrative.class));
    }
}
