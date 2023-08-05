package net.serenity.test.utils.rules;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.steps.StepListener;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.*;

/**
 * User: YamStranger
 * Date: 3/3/16
 * Time: 11:54 AM
 */
public class TestCase<T extends StepListener> implements MethodRule {

    private final List<T> listeners;
    private boolean finished;
    private final String name;

    @SafeVarargs
    public TestCase(T... listener) {
        this(UUID.randomUUID().toString(), listener);
    }

    @SafeVarargs
    public TestCase(final String name, final T... listener) {
        this.listeners = new ArrayList<>();
        register(listener);
        this.listeners.addAll(Arrays.asList(listener));
        this.name = name;
    }


    public Collection<T> listeners() {
        return new ArrayList<T>(this.listeners);
    }

    @SafeVarargs
    public final void register(final T... listener) {
        for (T regiter : listener) {
            this.listeners.add(regiter);
            StepEventBus.getParallelEventBus().registerListener(regiter);
            regiter.testStarted(name);
        }
    }

    public T firstListener() {
        return this.listeners.get(0);
    }

    public TestCase<T> finish() {
        if (!finished) {
            StepEventBus.getParallelEventBus().testFinished();
            this.finished = true;
        }
        return this;
    }

    public String name() {
        return this.name;
    }

    @Override
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    for (T regiter : listeners) {
                        StepEventBus.getParallelEventBus().registerListener(regiter);
                    }
                    StepEventBus.getParallelEventBus().testStarted(name);

                    statement.evaluate();
                } finally {
                    finish();
                }
            }
        };
    }
}
