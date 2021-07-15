package net.serenitybdd.junit.util;

import net.serenitybdd.core.collect.NewList;
import org.junit.runners.model.RunnerScheduler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JUnit scheduler for parallel parameterized tests.
 */
public class ParameterizedRunnerScheduler implements RunnerScheduler {

    private final ExecutorService executorService;
    private final CompletionService<Void> completionService;
    private final Queue<Future<Void>> tasks;

    public ParameterizedRunnerScheduler(final Class<?> klass, final int threadCount) {
        executorService = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory(klass.getSimpleName()));
        completionService = new ExecutorCompletionService<>(executorService);
        tasks = new LinkedList<>();
    }

    public Queue<Future<Void>> getTaskQueue() {
        return new LinkedList<>(NewList.copyOf(tasks));
    }

    public void schedule(final Runnable childStatement) {
        tasks.offer(completionService.submit(childStatement, null));
    }

    public void finished() {
        try {
            while (!tasks.isEmpty()) {
                tasks.remove(completionService.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdownNow();
        }
    }

    static final class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;

        NamedThreadFactory(final String poolName) {
            group = new ThreadGroup(poolName + "-" + POOL_NUMBER.getAndIncrement());
        }

        public Thread newThread(final Runnable r) {
            return new Thread(group, r, group.getName() + "-thread-" + threadNumber.getAndIncrement(), 0);
        }
    }

}
