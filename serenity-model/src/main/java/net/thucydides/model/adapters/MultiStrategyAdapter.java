package net.thucydides.model.adapters;

import net.thucydides.model.domain.TestTag;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class MultiStrategyAdapter implements TestStrategyAdapter {

    private List<TestStrategyAdapter> strategies;

    public MultiStrategyAdapter() {}

    public MultiStrategyAdapter(List<TestStrategyAdapter> strategies) {
        this.strategies = strategies;
    }

    @Override
    public Optional<String> getTitleAnnotation(Method testMethod) {
        return strategies.stream()
                .map(strategy -> strategy.getTitleAnnotation(testMethod))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public List<TestTag> getTagsFor(Method testMethod) {
        return strategies.stream()
                .map(strategy -> strategy.getTagsFor(testMethod))
                .filter(tags -> !tags.isEmpty())
                .findFirst()
                .orElse(new ArrayList<>());
    }

    @Override
    public Double priority() {
        return 0.0;
    }

    @Override
    public boolean isTestClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isTestClass(testClass));
    }

    @Override
    public boolean isTestMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isTestMethod(method));
    }

    @Override
    public boolean isTestSetupMethod(final Method method) {
        if (method == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isTestSetupMethod(method));
    }

    @Override
    public boolean isSerenityTestCase(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isSerenityTestCase(testClass));
    }

    @Override
    public boolean isAssumptionViolatedException(final Throwable throwable) {
        return delegateToStrategies(s -> s.isAssumptionViolatedException(throwable));
    }

    @Override
    public boolean isATaggableClass(final Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isATaggableClass(testClass));
    }

    @Override
    public boolean isIgnored(final Method method) {
        if (method == null) {
            return false;
        }
        return delegateToStrategies(s -> s.isIgnored(method));
    }

    private boolean delegateToStrategies(final Function<TestStrategyAdapter, Boolean> strategyBooleanFunction) {
        return strategies.stream().map(strategyBooleanFunction).filter(Boolean::booleanValue).findFirst()
                .orElse(false);
    }
}
