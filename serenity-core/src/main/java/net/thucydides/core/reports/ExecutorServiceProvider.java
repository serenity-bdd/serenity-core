package net.thucydides.core.reports;

import java.util.concurrent.ExecutorService;

/**
 * Created by john on 23/07/2015.
 */
public interface ExecutorServiceProvider  {

    ExecutorService getExecutorService();

    void shutdown();
}
