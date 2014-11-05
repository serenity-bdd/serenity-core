package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import net.thucydides.core.annotations.BlurScreenshots;
import net.thucydides.core.reflection.StackTraceAnalyser;

import java.lang.reflect.Method;

public class ScreenshotBlurCheck {


    public Optional<BlurLevel> blurLevel() {
        return fromAnnotation();
    }

    private Optional<BlurLevel> fromAnnotation() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            Method callingMethod = StackTraceAnalyser.forStackTraceElement(stackTraceElement).getMethod();
            if (callingMethod != null && callingMethod.getAnnotation(BlurScreenshots.class) != null) {
                return Optional.of(BlurLevel.valueOf(callingMethod.getAnnotation(BlurScreenshots.class).value().toUpperCase()));
            }
        }
        return Optional.absent();
    }
}
