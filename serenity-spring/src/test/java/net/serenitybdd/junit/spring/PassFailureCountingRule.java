package net.serenitybdd.junit.spring;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PassFailureCountingRule implements MethodRule {
    private static final Logger LOG = LoggerFactory.getLogger(PassFailureCountingRule.class);

    private final int expectedPasses;
    private final int expectedFailures;

    public PassFailureCountingRule(int expectedPasses, int expectedFailures) {
        this.expectedPasses = expectedPasses;
        this.expectedFailures = expectedFailures;
    }

    private static class State {
        private int passes = 0;
        private int failures = 0;
    }

    private static final Map<Class<?>,State> states = new HashMap<>();

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Method testMethod = method.getMethod();
                Class<?> testClass = testMethod.getDeclaringClass();
                String testMethodName = testMethod.getName();
                String testMethodClass = testClass.getSimpleName();
                String testMethodNameAndClass = "Test method '" + testMethodName + "' in class '" + testMethodClass + "'";
                State state = states.get(testClass);
                Throwable cause = null;
                String result;
                if (state == null) {
                    state = new State();
                    states.put(testClass,state);
                }
                try {
                    base.evaluate();
                    result = "passed";
                    state.passes++;
                } catch (Throwable t) {
                    if (isAssumptionViolatedException(t)) {
                        result = "skipped";
                    } else {
                        result = "failed";
                        state.failures++;
                        cause = t;
                    }                }
                if (state.passes > PassFailureCountingRule.this.expectedPasses) {
                    throw new AssertionError(this.createExceptionMessage(PassFailureCountingRule.this.expectedPasses,state.passes,"pass",testMethodName));
                }
                else if (state.failures > PassFailureCountingRule.this.expectedFailures) {
                    throw new AssertionError(this.createExceptionMessage(PassFailureCountingRule.this.expectedFailures,state.failures,"fail",testMethodName),cause);
                }
                else {
                    LOG.debug(testMethodNameAndClass + " " + result, " but the test will be marked as passed as that is expected under the rule " + PassFailureCountingRule.class.getSimpleName(),cause);
                }
            }

            private String createExceptionMessage(int expected, int actual, String type, String testName) {
                return "No more than " + expected + " test" + (expected==1?"":"s") + " in class " + testName + " should " + type + ", and this test " + type + "ing makes that " + actual + ".";
            }

            private boolean isAssumptionViolatedException(Throwable throwable) {
                try {
                    Class<?> clazz = Class.forName("org.junit.AssumptionViolatedException");
                    return clazz.isInstance(throwable);
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        };
    }
}
