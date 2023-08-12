package net.thucydides.model.requirements.annotations;


import net.serenitybdd.annotations.Narrative;

import java.util.Optional;

public class NarrativeFinder {
    public static Optional<Narrative> forClass(Class<?> annotatedClass) {
        return Optional.ofNullable(annotatedClass.getAnnotation(Narrative.class));
    }
}
