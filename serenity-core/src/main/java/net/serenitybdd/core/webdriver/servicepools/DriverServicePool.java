package net.serenitybdd.core.webdriver.servicepools;

import com.google.common.collect.Maps;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.io.IOException;
import java.util.Map;

public abstract class DriverServicePool<T extends DriverService> {

    private final static Map<String, DriverService> DRIVER_SERVICES = Maps.newConcurrentMap();

    protected final EnvironmentVariables environmentVariables;

    protected abstract T newDriverService();

    protected abstract String serviceName();

    public DriverServicePool() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public DriverServicePool(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                shutdown();
            }
        });
    }

    protected T getDriverService() {
        return driverServiceWithName(serviceName());
    }

    private T driverServiceWithName(String serviceName) {
        synchronized (DRIVER_SERVICES) {
            if (!DRIVER_SERVICES.containsKey(serviceName)) {
                T newDriverService = newDriverService();
                DRIVER_SERVICES.put(serviceName(), newDriverService);
            }
            return (T) DRIVER_SERVICES.get(serviceName);
        }
    }

    public synchronized void start() throws IOException {
        if (!getDriverService().isRunning()) {
            getDriverService().start();
        }
    }

    public synchronized void shutdown() {
        if (getDriverService().isRunning()) {
            getDriverService().stop();
        }
    }

    public WebDriver newDriver(Capabilities capabilities) throws IOException {
//        return new ChromeDriver(capabilities);
//        RemoteDriverBuilder<T> remoteDriverBuilder = new RemoteDriverBuilder<>(driverService);
        return new RemoteWebDriver(getDriverService().getUrl(), capabilities);

//        return remoteDriverBuilder.tryAtMost(3).timesTo(createANewWebDriverInstanceWithCapabilities(capabilities));
    }

    public boolean isRunning() {
        return getDriverService().isRunning();
    }

    public void ensureServiceIsRunning() throws IOException {
        start();
    }
//
//    public static class RemoteDriverBuilder<T extends DriverService> {
//        private final T driverService;
//
//        public RemoteDriverBuilder(T driverService) {
//            this.driverService = driverService;
//        }
//
//        public TryBuilder tryAtMost(int maxTries) {
//            return new TryBuilder(driverService, maxTries);
//        }
//    }
//
//    public static class TryBuilder<T extends DriverService> {
//
//        private final T driverService;
//        private final int maxTries;
//
//        private final InternalSystemClock timer = new InternalSystemClock();
//
//        public TryBuilder(T driverService, int maxTries) {
//            this.driverService = driverService;
//            this.maxTries = maxTries;
//        }
//
//        public TryBuilder tryAtMost(int maxTries) {
//            return new TryBuilder(driverService, maxTries);
//        }
//
//        public WebDriver timesTo(Function<T, WebDriver> createANewWebDriverInstance) throws IOException {
//            // Last try - throw any exceptions
//            if (maxTries == 0) {
//                return createANewWebDriverInstance.apply(driverService);
//            }
//
//            // Otherwise catch exceptions and retry
//            try {
//                return createANewWebDriverInstance.apply(driverService);
//            } catch (UnreachableBrowserException serviceNotRespondingYet) {
//                timer.pauseFor(1000);
//                return tryAtMost(maxTries - 1).timesTo(createANewWebDriverInstance);
//
//            } catch (SessionNotCreatedException sessionProbablyNotClosedCorrectlyOrNotFinishedClosing) {
//                timer.pauseFor(1000);
//                driverService.stop();
//                driverService.start();
//                return tryAtMost(maxTries - 1).timesTo(createANewWebDriverInstance);
//            }
//        }
//    }
//
//    public Function<T, WebDriver> createANewWebDriverInstanceWithCapabilities(Capabilities capabilities) {
//        return new CreateANewWebDriverInstanceWithCapabilities<T>(capabilities);
//    }
//
//    public class CreateANewWebDriverInstanceWithCapabilities<T extends DriverService> implements Function<T, WebDriver> {
//
//        private final Capabilities capabilities;
//
//        public CreateANewWebDriverInstanceWithCapabilities(Capabilities capabilities) {
//            this.capabilities = capabilities;
//        }
//
//        @Override
//        public WebDriver apply(T driverService) {
//            return new RemoteWebDriver(driverService.getUrl(), capabilities);
//        }
//    }


}