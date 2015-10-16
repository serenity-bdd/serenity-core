package net.serenitybdd.core.photography.bluring;

import net.thucydides.core.annotations.BlurScreenshots;
import net.thucydides.core.reflection.StackTraceAnalyser;
import net.thucydides.core.screenshots.BlurLevel;

import java.lang.reflect.Method;

public class AnnotatedBluring {

    public static BlurLevel blurLevel() {
        return fromAnnotation();
    }

    private static BlurLevel fromAnnotation() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            Method callingMethod = StackTraceAnalyser.forStackTraceElement(stackTraceElement).getMethod();
            if (callingMethod != null && callingMethod.getAnnotation(BlurScreenshots.class) != null) {
                return callingMethod.getAnnotation(BlurScreenshots.class).value();
            }
        }
        return BlurLevel.NONE;
    }
}
