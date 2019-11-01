package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.util.JUnitAdapter;
import net.thucydides.core.util.NameConverter;

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
            }
        }
        return Optional.empty();
    }

    private static boolean isATestMethod(Method callingMethod) {
        return JUnitAdapter.isTestMethod(callingMethod);
    }

    private static boolean isASetupMethod(Method callingMethod) {
        return JUnitAdapter.isTestSetupMethod(callingMethod);
    }

}
