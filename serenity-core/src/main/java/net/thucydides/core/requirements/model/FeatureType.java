package net.thucydides.core.requirements.model;

/**
 * Created by john on 6/03/15.
 */
public enum FeatureType {
    FEATURE(".feature"), STORY(".story");

    private final String extension;

    FeatureType(String extension) {
        this.extension = extension;
    }


    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
