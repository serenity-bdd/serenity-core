package net.thucydides.model.requirements.model;

import net.serenitybdd.model.collect.NewList;

/**
 * Created by john on 6/03/15.
 */
public enum FeatureType {
    UNDEFINED(""), FEATURE(".feature"), STORY(".story");

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

    public static FeatureType forFilename(String storyPath) {
        for(FeatureType featureType : NewList.of(FEATURE, STORY)) {
            if (storyPath.toLowerCase().endsWith(featureType.extension)) {
                return featureType;
            }
        }
        return UNDEFINED;
    }

}
