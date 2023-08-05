package net.serenitybdd.core.photography.bluring;

import net.serenitybdd.annotations.BlurScreenshots;
import net.thucydides.model.reflection.StackTraceAnalyser;
import net.serenitybdd.annotations.BlurLevel;

import java.lang.reflect.Method;

public class AnnotatedBluring {

    public static BlurLevel blurLevel() {
        return fromAnnotation();
    }

    private static BlurLevel fromAnnotation() {
        for(Method callingMethod : StackTraceAnalyser.inscopeMethodsIn(new Throwable().getStackTrace())) {
            if (callingMethod.getAnnotation(BlurScreenshots.class) != null) {
                return callingMethod.getAnnotation(BlurScreenshots.class).value();
            }
        }
        return BlurLevel.NONE;
    }
}
