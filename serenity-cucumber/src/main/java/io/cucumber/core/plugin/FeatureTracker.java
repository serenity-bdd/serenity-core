package io.cucumber.core.plugin;

import io.cucumber.plugin.event.TestCaseStarted;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class FeatureTracker {
    private static ThreadLocal<Set<URI>> STARTED_FEATURES = ThreadLocal.withInitial(HashSet::new);

    public static void startNewFeature(TestCaseStarted event) {
        STARTED_FEATURES.get().add(event.getTestCase().getUri());
    }

    public static boolean isNewFeature(TestCaseStarted event) {
        return !STARTED_FEATURES.get().contains(event.getTestCase().getUri());
    }

    public static boolean isASingleBrowserScenario(TestCaseStarted event) {
        return event.getTestCase().getTags().contains("@singlebrowser");
    }
}
