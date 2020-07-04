package net.thucydides.core.model.screenshots;

import net.serenitybdd.markers.DisableScreenshots;
import net.serenitybdd.markers.IsSilent;
import net.thucydides.core.annotations.Screenshots;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.reflection.StackTraceAnalyser;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.Configuration;

import java.lang.reflect.Method;
import java.util.Optional;

public class ScreenshotPermission {

    private final Configuration<Configuration> configuration;

    public ScreenshotPermission(Configuration configuration) {
        this.configuration = configuration;
    }


    public boolean areDisabledForThisAction() {
        TakeScreenshots codeLevelScreenshotConfiguration
                = stepMethodOverride().orElseGet(() -> methodOverride().orElse(taskOverride().orElse(classOverride().orElse(TakeScreenshots.UNDEFINED))));

        return codeLevelScreenshotConfiguration == TakeScreenshots.DISABLED;
    }

    public boolean areAllowed(TakeScreenshots takeScreenshots) {

        TakeScreenshots configuredLevel = stepMethodOverride()
                .orElseGet(() -> methodOverride()
                        .orElse(taskOverride()
                                .orElse(classOverride()
                                        .orElse(stepDefinitionOverride()
                                                .orElse(configuration.getScreenshotLevel()
                                                        .orElse(TakeScreenshots.UNDEFINED))))));


        if (configuredLevel != TakeScreenshots.UNDEFINED) {
            return takeScreenshotLevel(takeScreenshots).isAtLeast(configuredLevel);
        } else {
            return legacyScreenshotConfiguration(takeScreenshots);
        }
    }

    private boolean legacyScreenshotConfiguration(TakeScreenshots takeScreenshots) {
        if (configuration.onlySaveFailingScreenshots()) {
            return takeScreenshotLevel(takeScreenshots).isAtLeast(TakeScreenshots.FOR_FAILURES);
        }
        if (configuration.takeVerboseScreenshots()) {
            return takeScreenshotLevel(takeScreenshots).isAtLeast(TakeScreenshots.FOR_EACH_ACTION);
        }
        return takeScreenshotLevel(takeScreenshots).isAtLeast(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP);
    }

    private Optional<TakeScreenshots> stepMethodOverride() {
        if (StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            Optional<Method> currentStepMethod = StepEventBus.getEventBus().getBaseStepListener().getCurrentStepMethod();
            if (currentStepMethod != null && currentStepMethod.isPresent()) {
                Optional<TakeScreenshots> overriddenScreenshotPreference = overriddenScreenshotPreferenceFor(currentStepMethod.get());
                if (overriddenScreenshotPreference.isPresent()) {
                    return overriddenScreenshotPreference;
                }
            }
        }
        return Optional.empty();
    }

    private Optional<TakeScreenshots> methodOverride() {
        for (Method callingMethod : StackTraceAnalyser.inscopeMethodsIn(new Throwable().getStackTrace())) {
            Optional<TakeScreenshots> overriddenScreenshotPreference = overriddenScreenshotPreferenceFor(callingMethod);
            if (overriddenScreenshotPreference.isPresent()) {
                return overriddenScreenshotPreference;
            }
        }
        return Optional.empty();
    }

    private Optional<TakeScreenshots> taskOverride() {
        for (Method callingMethod : StackTraceAnalyser.performAsMethodsIn(new Throwable().getStackTrace())) {
            Optional<TakeScreenshots> overriddenScreenshotPreference = overriddenTaskScreenshotPreferenceFor(callingMethod);
            if (overriddenScreenshotPreference.isPresent()) {
                return overriddenScreenshotPreference;
            }
        }
        return Optional.empty();
    }

    private Optional<TakeScreenshots> stepDefinitionOverride() {
        if (StepDefinitionAnnotations.getScreenshotPreferences() == TakeScreenshots.UNDEFINED) {
            return Optional.empty();
        }
        return Optional.ofNullable(StepDefinitionAnnotations.getScreenshotPreferences());
    }

    private Optional<TakeScreenshots> classOverride() {
        if (StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            Optional<Method> currentStepMethod = StepEventBus.getEventBus().getBaseStepListener().getCurrentStepMethod();
            if (currentStepMethod != null && currentStepMethod.isPresent()) {
                return overriddenScreenshotPreferenceForClass(currentStepMethod.get().getDeclaringClass());
            }
        }
        return Optional.empty();
    }

    private Optional<TakeScreenshots> overriddenScreenshotPreferenceForClass(Class<?> declaringClass) {
        java.util.Optional<TakeScreenshots> optionalScreenshotPreference
                = ScreenshotPreferencesByClass.forClass(declaringClass)
                .withEnvironmentVariables(configuration.getEnvironmentVariables()).getScreenshotPreference();

        return optionalScreenshotPreference;
    }

    private Optional<TakeScreenshots> overriddenScreenshotPreferenceFor(Method callingMethod) {
        if (callingMethod.getAnnotation(Screenshots.class) != null) {
            return Optional.of(screenshotLevelFrom(callingMethod.getAnnotation(Screenshots.class)));
        }

        return Optional.empty();
    }

    private Optional<TakeScreenshots> overriddenTaskScreenshotPreferenceFor(Method callingMethod) {
        // Is there a @Screenshots annotation on the performAs() method
        if (callingMethod.getAnnotation(Screenshots.class) != null) {
            return Optional.of(screenshotLevelFrom(callingMethod.getAnnotation(Screenshots.class)));
        }
        // Is there a @Screenshots annotation on the Performable class
        if (callingMethod.getDeclaringClass().getAnnotation(Screenshots.class) != null) {
            return Optional.of(screenshotLevelFrom(callingMethod.getDeclaringClass().getAnnotation(Screenshots.class)));
        }
        // Does the Performable have the IsSilent marker interface
        if (isSilent(callingMethod.getDeclaringClass()) || isABackendOperation(callingMethod.getDeclaringClass())) {
            return Optional.of(TakeScreenshots.DISABLED);
        }

        return Optional.empty();
    }


    private boolean isSilent(Class<?> declaringClass) {
        return IsSilent.class.isAssignableFrom(declaringClass);
    }

    private boolean isABackendOperation(Class<?> declaringClass) {
        return DisableScreenshots.class.isAssignableFrom(declaringClass);
    }

    private TakeScreenshots screenshotLevelFrom(Screenshots screenshots) {
        if (screenshots.disabled()) {
            return TakeScreenshots.DISABLED;
        } else if (screenshots.onlyOnFailures()) {
            return TakeScreenshots.FOR_FAILURES;
        } else if (screenshots.forEachAction()) {
            return TakeScreenshots.FOR_EACH_ACTION;
        } else if (screenshots.afterEachStep()) {
            return TakeScreenshots.AFTER_EACH_STEP;
        } else if (screenshots.beforeAndAfterEachStep()) {
            return TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP;
        } else {
            return TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP;
        }
    }

    private TakeScreenshotsComparer takeScreenshotLevel(TakeScreenshots takeScreenshots) {
        return new TakeScreenshotsComparer(takeScreenshots);
    }

    private static class TakeScreenshotsComparer {
        private final TakeScreenshots takeScreenshots;

        private TakeScreenshotsComparer(TakeScreenshots takeScreenshots) {
            this.takeScreenshots = takeScreenshots;
        }

        public boolean isAtLeast(TakeScreenshots requiredLevel) {
            return takeScreenshots.compareTo(requiredLevel) >= 0;
        }
    }
}
