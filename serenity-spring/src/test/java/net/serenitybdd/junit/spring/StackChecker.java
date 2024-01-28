package net.serenitybdd.junit.spring;

import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks;

public class StackChecker {
	private StackChecker() {}

    public static void checkForClassRule() {
        StackChecker.checkFor(RunBeforeTestClassCallbacks.class);
        StackChecker.checkFor(RunAfterTestClassCallbacks.class);
    }

    public static void checkForMethodRule() {
        StackChecker.checkFor(RunBeforeTestMethodCallbacks.class);
        StackChecker.checkFor(RunAfterTestMethodCallbacks.class);
    }

    public static void checkFor(Class<?> wantedClass) {
        boolean encountered = false;
        String wantedClassName = wantedClass.getName();
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.getClassName().equalsIgnoreCase(wantedClassName)) {
                encountered = true;
            }
        }
        if (!encountered) {
            throw new AssertionError("Expected '" + wantedClassName + "' to be in the stack, but it was not.");
        }
    }
}
