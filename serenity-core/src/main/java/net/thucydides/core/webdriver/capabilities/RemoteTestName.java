package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.util.NameConverter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Optional;

class RemoteTestName {
    public static Optional<String> fromCurrentTest() {
        for (StackTraceElement elt : Thread.currentThread().getStackTrace()) {
            try {
                Class callingClass = Class.forName(elt.getClassName());
                Method callingMethod = callingClass.getMethod(elt.getMethodName());
                if (isATestMethod(callingMethod)) {
                    return Optional.of(NameConverter.humanize(elt.getMethodName()));
                } else if (isASetupMethod(callingMethod)) {
                    return Optional.of(NameConverter.humanize(callingClass.getSimpleName()));
                }
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private static boolean isATestMethod(Method callingMethod) {
        return callingMethod.getAnnotation(Test.class) != null;
    }

    private static boolean isASetupMethod(Method callingMethod) {
        return (callingMethod.getAnnotation(Before.class) != null)
                || (callingMethod.getAnnotation(BeforeClass.class) != null);
    }

}
