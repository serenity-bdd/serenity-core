package net.thucydides.model.adapters.junit5;

import net.thucydides.model.adapters.TestStrategyAdapter;
import net.thucydides.model.domain.TestTag;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class JUnit5Adapter implements TestStrategyAdapter {

    private final JUnit5Strategy jUnit5Strategy;

    public JUnit5Adapter() {
        jUnit5Strategy = new JUnit5Strategy();
    }

    public boolean isTestClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return jUnit5Strategy.isTestClass(testClass);
    }

    public boolean isTestMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return jUnit5Strategy.isTestMethod(method);
    }

    public boolean isTestSetupMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return jUnit5Strategy.isTestSetupMethod(method);
    }

    public boolean isSerenityTestCase(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return jUnit5Strategy.isSerenityTestCase(testClass);
    }

    public boolean isAssumptionViolatedException(final Throwable throwable) {
        return jUnit5Strategy.isAssumptionViolatedException(throwable);
    }

    public boolean isATaggableClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return jUnit5Strategy.isATaggableClass(testClass);
    }

    public boolean isIgnored(final Method method) {
        if (method == null) {
            return false;
        }
        return jUnit5Strategy.isIgnored(method);
    }

    public Optional<String> getTitleAnnotation(Method testMethod) {
        return jUnit5Strategy.getTitleAnnotation(testMethod);
    }

    public List<TestTag> getTagsFor(Method testMethod) {
        return jUnit5Strategy.getTagsFor(testMethod);
    }

    @Override
    public Double priority() {
        return 5.7;
    }
}
