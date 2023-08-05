package net.serenitybdd.core.webdriver.servicepools;

import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.firefox.GeckoDriverService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Used as a wrapper around the GeckoDriverService to make it behave in a Threadsafe manner
 * like all the other DriverService implementations.
 */
public class ThreadsafeGeckoDriverService extends GeckoDriverService {

    ThreadLocal<GeckoDriverService> threadLocalDriverService = new ThreadLocal<>();

    private final EnvironmentVariables environmentVariables;

    public ThreadsafeGeckoDriverService(EnvironmentVariables environmentVariables) throws IOException {
        super(new File(""), 0, null, null);
        this.environmentVariables = environmentVariables;
    }


    public static GeckoDriverService createThreadsafeService(EnvironmentVariables environmentVariables) {
        try {
            return new ThreadsafeGeckoDriverService(environmentVariables);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create threadsafe service", e);
        }
    }

    protected GeckoDriverService getThreadlocalGeckoService() {
        if (threadLocalDriverService.get() == null) {

            Optional<File> geckoExecutable = GeckoDriverServiceExecutable.inEnvironment(environmentVariables);

            GeckoDriverService newService;

            if (geckoExecutable.isPresent()) {
                newService = new Builder()
                        .usingDriverExecutable(geckoExecutable.get())
                        .usingAnyFreePort()
                        .build();

                Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));
            } else {
                newService = DisabledGeckoDriverService.build();
            }

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
