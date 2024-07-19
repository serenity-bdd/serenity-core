package net.thucydides.model.adapters;

import net.thucydides.model.reflection.ClassFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class TestFramework {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestFramework.class);

    private static TestStrategyAdapter selectedTestStrategyAdapter;

    static {
        List<Class<?>> testStrategies = ClassFinder.loadClasses()
                .thatImplement(TestStrategyAdapter.class)
                .fromPackage("net.thucydides.model.adapters");

        List<TestStrategyAdapter> availableStrategies = new ArrayList<>();
        for (Class<?> strategyClass : testStrategies) {
            newInstanceOf(strategyClass).ifPresent(availableStrategies::add);
        }
        availableStrategies.sort((o1, o2) -> o2.priority().compareTo(o1.priority()));
        if (availableStrategies.size() > 1) {
            selectedTestStrategyAdapter =  new MultiStrategyAdapter(availableStrategies);
        } else if (availableStrategies.size() == 1) {
            selectedTestStrategyAdapter = availableStrategies.get(0);
        } else {
            throw new IllegalStateException("No Test Strategy Adapter found: To run JUnit tests in Serenity make sure that either the Serenity JUnit 4 (serenity-junit) or JUnit 5 (serenity-junit5) dependency is available.");
        }
    }

    public static TestStrategyAdapter support() {
        return selectedTestStrategyAdapter;
    }

    private static Optional<TestStrategyAdapter> newInstanceOf(Class<?> adaptorClass) {
        try {
            TestStrategyAdapter adapter = (TestStrategyAdapter) adaptorClass.getDeclaredConstructor().newInstance();
            if (adapter.priority() > 0) {
                return Optional.of(adapter);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error("Cannot instantiate test framework adapter class " + adaptorClass, e);
        }
        return Optional.empty();
    }
}
