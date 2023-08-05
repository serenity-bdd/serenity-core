package net.serenitybdd.core.photography;

import net.thucydides.model.util.EnvironmentVariables;
import java.util.HashMap;
import java.util.Map;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_SCREENSHOOTER;

public class ScreenShooterFactory {
    private final EnvironmentVariables environmentVariables;

    private static final Map<String, String> SCREEN_SHOOTER_SHORTCUTS = new HashMap<>();
    static {
        SCREEN_SHOOTER_SHORTCUTS.put("webdriver","net.serenitybdd.core.photography.WebDriverScreenShooter");
        SCREEN_SHOOTER_SHORTCUTS.put("shutterbug","net.serenitybdd.screenshots.shutterbug.ShutterbugScreenShooter");
        SCREEN_SHOOTER_SHORTCUTS.put("shutterbug1x","net.serenitybdd.screenshots.shutterbug.Shutterbug1XScreenShooter");
    }

    public ScreenShooterFactory(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public ScreenShooter buildScreenShooter(PhotoLens lens) {
        String screenshooterClass = SERENITY_SCREENSHOOTER.from(environmentVariables,"net.serenitybdd.core.photography.WebDriverScreenShooter");

        if (SCREEN_SHOOTER_SHORTCUTS.containsKey(screenshooterClass)) {
            screenshooterClass = SCREEN_SHOOTER_SHORTCUTS.get(screenshooterClass);
        }
        return newScreenShooter(lens, screenshooterClass);
    }

    private ScreenShooter newScreenShooter(PhotoLens lens, String screenshooterClass) {
        try {
            return (ScreenShooter) Class.forName(screenshooterClass).getConstructor(PhotoLens.class).newInstance(lens);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to instantiate screen shooter " + screenshooterClass, e);
        }
    }
}
