package net.serenity.test.utils.rules;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.steps.StepListener;
import net.thucydides.core.steps.events.TestFinishedEvent;
import net.thucydides.core.steps.events.TestStartedEvent;
import net.thucydides.core.steps.session.TestSession;
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
    public final void register(final T... listeners) {
        for (T listener : listeners) {
            this.listeners.add(listener);
            StepEventBus.getParallelEventBus().registerListener(listener);
            listener.testStarted(name);
        }
    }

    public T firstListener() {
        return this.listeners.get(0);
    }

    public TestCase<T> finish() {
        if (!finished) {
            if(TestSession.isSessionStarted()) {
                TestSession.addEvent(new TestFinishedEvent());
            } else {
                StepEventBus.getParallelEventBus().testFinished();
            }
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
                    for (T listener : listeners) {
                        StepEventBus.getParallelEventBus().registerListener(listener);
                    }
                    if (TestSession.isSessionStarted()) {
                        TestSession.addEvent( new TestStartedEvent(name));
                    } else {
                        StepEventBus.getParallelEventBus().testStarted(name);
                    }

                    statement.evaluate();
                } finally {
                    finish();
                }
            }
        };
    }
}
