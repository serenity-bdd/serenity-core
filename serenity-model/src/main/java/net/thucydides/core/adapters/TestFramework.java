package net.thucydides.core.adapters;

import net.thucydides.core.reflection.ClassFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestFramework {
    public static TestStrategyAdapter support() {
        List<Class<?>> testStrategies = ClassFinder.loadClasses()
                .thatImplement(TestStrategyAdapter.class)
                .fromPackage("net.thucydides.core.adapters");

        List<TestStrategyAdapter> availableStrategies = new ArrayList<>();
        for (Class<?> strategyClass : testStrategies) {
            newInstanceOf(strategyClass).ifPresent(availableStrategies::add);
        }

        availableStrategies.sort((o1, o2) -> o2.priority().compareTo(o1.priority()));

        if (availableStrategies.size() > 1) {
            return new MultiStrategyAdapter(availableStrategies);
        } else {
            return availableStrategies.get(0);
        }
    }

    private static Optional<TestStrategyAdapter> newInstanceOf(Class<?> adaptorClass) {
        try {
            TestStrategyAdapter adapter = (TestStrategyAdapter) adaptorClass.newInstance();
            if (adapter.priority() > 0) {
                return Optional.of(adapter);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
