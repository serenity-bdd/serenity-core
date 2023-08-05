package net.thucydides.model.steps;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ScreenplayInspector {
    public static boolean isAScreenplayPerformAsMethod(final Method method) {
        return method.getName().equals("performAs")
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0].getSimpleName().equals("Actor");
    }

    public static boolean isAScreenplayClass(Class<?> stepClass) {
        return Arrays.stream(stepClass.getMethods())
                     .anyMatch(ScreenplayInspector::isAScreenplayPerformAsMethod);
    }

    public static Method performAsMethodIn(Class<?> stepClass) {
        return Arrays.stream(stepClass.getMethods())
                .filter(ScreenplayInspector::isAScreenplayPerformAsMethod)
                .findFirst().get();
    }
}
