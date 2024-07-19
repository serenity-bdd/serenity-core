package net.serenitybdd.screenplay.waits;

import net.serenitybdd.screenplay.Actor;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static net.thucydides.model.ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT;

public class WaitOnSupplier extends WaitWithTimeout {

    private final Callable<Boolean> expectedState;

    public WaitOnSupplier(Callable<Boolean> expectedState) {
        this.expectedState = expectedState;
        int durationInMillis = SystemEnvironmentVariables.currentEnvironmentVariables().getPropertyAsInteger(WEBDRIVER_WAIT_FOR_TIMEOUT, 3000);
        this.timeout = Duration.ofMillis(durationInMillis);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Awaitility.await().pollInSameThread().atMost(timeout.toMillis(), TimeUnit.MILLISECONDS).until(expectedState);
    }
}
