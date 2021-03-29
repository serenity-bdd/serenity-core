package serenitycore.net.thucydides.core.model.screenshots;

import serenitymodel.net.thucydides.core.model.TakeScreenshots;

public class StepDefinitionAnnotations {

    private final static ThreadLocal<TakeScreenshots> screenshotOverride = new ThreadLocal<>();

    public static void setScreenshotPreferencesTo(TakeScreenshots takeScreenshotPreference) {
        screenshotOverride.set(takeScreenshotPreference);
    }

    public static TakeScreenshots getScreenshotPreferences() {
        return screenshotOverride.get();
    }

    public static void clear() { screenshotOverride.remove(); };
}
