package net.thucydides.core.adapters.junit4;

import net.thucydides.model.adapters.TestStrategyAdapter;
import net.thucydides.model.domain.TestTag;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * This is an INTERNAL helper class of serenity, it should not be used directly and may be subject to refactoring.
 * <p>
 * It serves to decouple serenity-model (and serenity-core) from JUnit. If JUnit is on the classpath, JUnit
 * classes will be handled specifically. But serenity will continue to function without JUnit being on the classpath.
 * Furthermore, users can choose between JUnit 4 and JUnit 5 or even use both together.
 * <p>
 * Ideally this approach could be generalized to avoid any special treatment of JUnit as a test framework.
 * Test-framework specific strategies could be registered from the framework-specific modules. That would allow
 * to place the implementation of (and the tests for) the test-framework specific strategies into the modules that know
 * about those frameworks and also have the necessary dependencies.
 * <p>
 * But this would be a more extensive change, potentially breaking backwards compatibility. So for now this class does
 * exactly only this: It provides an adapter for JUnit 4 and JUnit 5, and makes explicit the parts of the code where
 * there has previously been a hard dependency on JUnit 4.
 * <p>
 * As such it is self-contained and should be possible to be grasped rather easily. And it marks the starting point for
 * a potential refactoring towards a more general approach.
 */
public class JUnit4Adapter implements TestStrategyAdapter {


    private final JUnit4Strategy jUnit4Strategy;

    public JUnit4Adapter() {
        jUnit4Strategy = new JUnit4Strategy();
    }

    public boolean isTestClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return jUnit4Strategy.isTestClass(testClass);
    }

    public boolean isTestMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return jUnit4Strategy.isTestMethod(method);
    }

    public boolean isTestSetupMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return jUnit4Strategy.isTestSetupMethod(method);
    }

    public boolean isSerenityTestCase(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return jUnit4Strategy.isSerenityTestCase(testClass);
    }

    public boolean isAssumptionViolatedException(final Throwable throwable) {
        return jUnit4Strategy.isAssumptionViolatedException(throwable);
    }

    public boolean isATaggableClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return jUnit4Strategy.isATaggableClass(testClass);
    }

    public boolean isIgnored(final Method method) {
        if (method == null) {
            return false;
        }
        return jUnit4Strategy.isIgnored(method);
    }

    public Optional<String> getTitleAnnotation(Method testMethod) {
        return jUnit4Strategy.getTitleAnnotation(testMethod);
    }

    public List<TestTag> getTagsFor(Method testMethod) {
        return jUnit4Strategy.getTagsFor(testMethod);
    }

    @Override
    public Double priority() {
        return 4.13;
    }
}
