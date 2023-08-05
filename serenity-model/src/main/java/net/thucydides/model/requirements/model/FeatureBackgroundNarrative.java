package net.thucydides.model.requirements.model;

public class FeatureBackgroundNarrative {
    private final String title;
    private final String description;

    public FeatureBackgroundNarrative(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
