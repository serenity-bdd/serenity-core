package io.cucumber.junit;

import org.junit.runners.ParentRunner;

import java.lang.reflect.Field;


public class FeatureRunnerExtractors {

    public static String extractFeatureName(ParentRunner<?> runner) {
        String displayName = runner.getDescription().getDisplayName();
        return displayName.substring(displayName.indexOf(":") + 1).trim();
    }

    public static String featurePathFor(ParentRunner<?> featureRunner) {
        try {
            Field field = featureRunner.getDescription().getClass().getDeclaredField("fUniqueId");
            field.setAccessible(true);
            return field.get(featureRunner.getDescription()).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
