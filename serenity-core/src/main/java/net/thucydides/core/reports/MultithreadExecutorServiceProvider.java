package net.thucydides.core.reports;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by john on 23/07/2015.
 */
public class MultithreadExecutorServiceProvider implements ExecutorServiceProvider {

    int corePoolSize = 4;
    int maximumPoolSize = 8;
    int keepAliveTime = 60000;

    @Inject
    public MultithreadExecutorServiceProvider(EnvironmentVariables environmentVariables) {
        corePoolSize = ThucydidesSystemProperty.REPORT_THREADS.integerFrom(environmentVariables, 4);
        maximumPoolSize = ThucydidesSystemProperty.REPORT_MAX_THREADS.integerFrom(environmentVariables, 8);
        keepAliveTime = ThucydidesSystemProperty.REPORT_KEEP_ALIVE_TIME.integerFrom(environmentVariables, 60000);
    }

    private ExecutorService executorService;

    @Override
    public ExecutorService getExecutorService() {
        if (executorService == null)  {
            int corePoolSize = 4;
            int maximumPoolSize = 8;
            int keepAliveTime = 60000;

            executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                    keepAliveTime, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());

        }
        return executorService;
    }
}
