package net.thucydides.core.requirements.model.cucumber;

import gherkin.ast.Feature;

public class AnnotatedFeature {
    private final Feature feature;
    private final String descriptionInComments;

    public AnnotatedFeature(Feature feature, String descriptionInComments) {
        this.feature = feature;
        this.descriptionInComments = descriptionInComments;
    }

    public Feature getFeature() {
        return feature;
    }

    public String getDescriptionInComments() {
        return descriptionInComments;
    }
}
