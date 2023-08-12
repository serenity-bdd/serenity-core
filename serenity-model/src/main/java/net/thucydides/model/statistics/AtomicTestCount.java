package net.thucydides.model.statistics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Keeps track of the tests executed.
 * Should be managed by Guice as a singleton.
 */
public class AtomicTestCount implements TestCount {
    private final AtomicInteger count = new AtomicInteger();

    public int getNextTest() {
        return count.addAndGet(1);
    }

    @Override
    public int getCurrentTestNumber() {
        return count.get();
    }
}
