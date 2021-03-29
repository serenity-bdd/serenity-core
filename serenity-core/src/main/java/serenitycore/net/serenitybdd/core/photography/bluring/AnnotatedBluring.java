package serenitycore.net.serenitybdd.core.photography.bluring;

import serenitycore.net.thucydides.core.annotations.BlurScreenshots;
import serenitymodel.net.thucydides.core.reflection.StackTraceAnalyser;
import serenitycore.net.thucydides.core.screenshots.BlurLevel;

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
