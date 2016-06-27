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
        for(Method callingMethod : StackTraceAnalyser.inscopeMethodsIn(new Throwable().getStackTrace())) {
            if (callingMethod.getAnnotation(BlurScreenshots.class) != null) {
                return Optional.of(callingMethod.getAnnotation(BlurScreenshots.class).value());
            }
        }
        return Optional.absent();
    }
}
