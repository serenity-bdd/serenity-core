package net.serenitybdd.screenplay.waits;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT;

public class WaitOnSupplier extends WaitWithTimeout {

    private Callable<Boolean> expectedState;

    public WaitOnSupplier(Callable<Boolean> expectedState) {
        this.expectedState = expectedState;
        int durationInMillis = Injectors.getInjector().getInstance(EnvironmentVariables.class).getPropertyAsInteger(WEBDRIVER_WAIT_FOR_TIMEOUT, 3000);
        this.timeout = Duration.ofMillis(durationInMillis);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Awaitility.await().pollInSameThread().atMost(timeout.toMillis(), TimeUnit.MILLISECONDS).until(expectedState);
    }
}
