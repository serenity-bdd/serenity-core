package net.thucydides.core.requirements.reports.cucumber;

import net.thucydides.core.requirements.model.cucumber.AnnotatedFeature;

import java.io.Serializable;
import java.util.Optional;

class OptionalAnnotatedFeature implements Serializable {
    private final AnnotatedFeature annotatedFeature;

    public OptionalAnnotatedFeature(Optional<AnnotatedFeature> annotatedFeature) {
        this.annotatedFeature = annotatedFeature.orElse(null);
    }

    public Optional<AnnotatedFeature> get() {
        return Optional.ofNullable(annotatedFeature);
    }
}
