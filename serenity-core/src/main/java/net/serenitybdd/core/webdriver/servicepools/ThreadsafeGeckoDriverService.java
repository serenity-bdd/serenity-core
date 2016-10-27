package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.firefox.GeckoDriverService;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Used as a wrapper around the GeckoDriverService to make it behave in a Threadsafe manner
 * like all the other DriverService implementations.
 */
public class ThreadsafeGeckoDriverService extends GeckoDriverService {

    ThreadLocal<GeckoDriverService> threadLocalDriverService = new ThreadLocal<>();

    public ThreadsafeGeckoDriverService() throws IOException {
        super(new File(""), 0, null, null);
    }


    public static GeckoDriverService createThreadsafeService() {
        try {
            return new ThreadsafeGeckoDriverService();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create threadsafe service", e);
        }
    }

    protected GeckoDriverService getThreadlocalGeckoService() {
        if (threadLocalDriverService.get() == null) {

            GeckoDriverService newService = GeckoDriverService.createDefaultService();

            Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));

            threadLocalDriverService.set(newService);
        }
        return threadLocalDriverService.get();
    }

    @Override
    public URL getUrl() {
        return getThreadlocalGeckoService().getUrl();
    }

    @Override
    public boolean isRunning() {
        return getThreadlocalGeckoService().isRunning();
    }

    @Override
    public void start() throws IOException {
        getThreadlocalGeckoService().start();
    }

    @Override
    public void stop() {
        getThreadlocalGeckoService().stop();
    }

}
