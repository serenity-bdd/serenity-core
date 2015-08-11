package net.thucydides.core.requirements.annotations;


import com.google.common.base.Optional;
import net.thucydides.core.annotations.Narrative;

public class NarrativeFinder {
    public static Optional<Narrative> forClass(Class<?> annotatedClass) {
        return Optional.fromNullable((Narrative) annotatedClass.getAnnotation(Narrative.class));
    }
}
