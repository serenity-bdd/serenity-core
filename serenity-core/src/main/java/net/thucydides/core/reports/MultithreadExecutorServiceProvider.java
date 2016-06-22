package net.thucydides.core.reports;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.concurrent.*;

/**
 * Created by john on 23/07/2015.
 */
public class MultithreadExecutorServiceProvider implements ExecutorServiceProvider {

    int corePoolSize = 4;
    int maximumPoolSize = 20;
    int keepAliveTime = 60000;

    @Inject
    public MultithreadExecutorServiceProvider(EnvironmentVariables environmentVariables) {
        corePoolSize = ThucydidesSystemProperty.REPORT_THREADS.integerFrom(environmentVariables, Runtime.getRuntime().availableProcessors());
        maximumPoolSize = ThucydidesSystemProperty.REPORT_MAX_THREADS.integerFrom(environmentVariables, Runtime.getRuntime().availableProcessors());
        keepAliveTime = ThucydidesSystemProperty.REPORT_KEEP_ALIVE_TIME.integerFrom(environmentVariables, 60000);
    }

    private ExecutorService executorService;

    @Override
    public ExecutorService getExecutorService() {
        if (executorService == null)  {
            executorService = Executors.newFixedThreadPool(maximumPoolSize);
        }
        return executorService;
    }
}
