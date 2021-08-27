package net.serenitybdd.junit5;

import net.bytebuddy.asm.Advice;
import net.thucydides.core.steps.StepInterceptor;
import org.junit.jupiter.api.function.Executable;

/**
 * Intercepts the jUnit5 Assertion.assertThrows
 */
public class AssertThrowsAdvice {

    @Advice.OnMethodEnter
    public static void enter(Class expectedType, Executable executable) {
        StepInterceptor.setExpectedExceptionType(expectedType);
        SerenityTestExecutionListener.addExpectedException(expectedType);
    }

    @Advice.OnMethodExit
    public static void exit(Class expectedType, Executable executable) {
        StepInterceptor.resetExpectedExceptionType();
    }
}
