package net.thucydides.model.requirements.reports.cucumber;

import net.thucydides.model.requirements.model.cucumber.AnnotatedFeature;

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
