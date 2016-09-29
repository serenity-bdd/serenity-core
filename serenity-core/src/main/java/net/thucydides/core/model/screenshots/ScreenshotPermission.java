package net.thucydides.core.model.screenshots;

import com.google.common.base.Optional;
import net.thucydides.core.annotations.Screenshots;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.reflection.StackTraceAnalyser;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.Configuration;

import java.lang.reflect.Method;

public class ScreenshotPermission {

    private final Configuration configuration;

    public ScreenshotPermission(Configuration configuration) {
        this.configuration = configuration;
    }


    public boolean areAllowed(TakeScreenshots takeScreenshots) {
//
//
//        if (overrideLevel.isPresent()) {
//            return takeScreenshotLevel(takeScreenshots).isAtLeast(overrideLevel.get());
//        }
//
//
//        Optional<TakeScreenshots> configuredLevel = configuration.getScreenshotLevel();

        Optional<TakeScreenshots> configuredLevel = methodOverride()
                                                    .or(classOverride())
                                                    .or(configuration.getScreenshotLevel());

        if (configuredLevel.isPresent()) {
            return takeScreenshotLevel(takeScreenshots).isAtLeast(configuredLevel.get());
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

    private Optional<TakeScreenshots> methodOverride() {
        for(Method callingMethod : StackTraceAnalyser.inscopeMethodsIn(new Throwable().getStackTrace())) {
            Optional<TakeScreenshots> overriddenScreenshotPreference = overriddenScreenshotPreferenceFor(callingMethod);
            if (overriddenScreenshotPreference.isPresent()) {
                return overriddenScreenshotPreference;
            }
        }
        return Optional.absent();
    }

    private Optional<TakeScreenshots> classOverride() {
        if (StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            Optional<Method> currentStepMethod = StepEventBus.getEventBus().getBaseStepListener().getCurrentStepMethod();
            if (currentStepMethod != null && currentStepMethod.isPresent()) {
                return overriddenScreenshotPreferenceForClass(currentStepMethod.get().getDeclaringClass());
            }
        }
        return Optional.absent();
    }

    private Optional<TakeScreenshots> overriddenScreenshotPreferenceForClass(Class<?> declaringClass) {
        return ScreenshotPreferencesByClass.forClass(declaringClass).withEnvironmentVariables(configuration.getEnvironmentVariables()).getScreenshotPreference();
    }

    private Optional<TakeScreenshots> overriddenScreenshotPreferenceFor(Method callingMethod) {
        if (callingMethod.getAnnotation(Screenshots.class) != null) {
            return Optional.of(screenshotLevelFrom(callingMethod.getAnnotation(Screenshots.class)));
        }

        return Optional.absent();
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
