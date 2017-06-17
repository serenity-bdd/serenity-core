package net.thucydides.core.reports;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by john on 23/07/2015.
 */
public class MultithreadExecutorServiceProvider implements ExecutorServiceProvider {

    int maximumPoolSize = 20;

    @Inject
    public MultithreadExecutorServiceProvider(EnvironmentVariables environmentVariables) {
        maximumPoolSize = ThucydidesSystemProperty.REPORT_MAX_THREADS.integerFrom(environmentVariables, Runtime.getRuntime().availableProcessors());
    }

    private ExecutorService executorService;

    ReentrantLock lock = new ReentrantLock();

    @Override
    public ExecutorService getExecutorService() {

        lock.lock();

        if (executorService == null)  {
            executorService = Executors.newFixedThreadPool(maximumPoolSize);
        }

        ensureTheExecutorServiceShutsdownEventually();

        lock.unlock();

        return executorService;
    }

    @Override
    public void shutdown() {
        lock.lock();
        executorService.shutdown();
        executorService = null;
        lock.unlock();
    }

    private void ensureTheExecutorServiceShutsdownEventually() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (executorService != null) {
                    executorService.shutdown();
                }
            }
        });
    }
}
