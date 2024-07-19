package net.thucydides.core.webdriver.capabilities;

import net.thucydides.model.adapters.TestFramework;
import net.thucydides.model.util.NameConverter;

import java.lang.reflect.Method;
import java.util.Optional;

public class RemoteTestName {
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
            }
        }
        return Optional.empty();
    }

    private static boolean isATestMethod(Method callingMethod) {
        return TestFramework.support().isTestMethod(callingMethod);
    }

    private static boolean isASetupMethod(Method callingMethod) {
        return TestFramework.support().isTestSetupMethod(callingMethod);
    }
}
