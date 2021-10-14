package net.serenitybdd.screenplay.waits;

import net.serenitybdd.markers.IsSilent;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.time.Duration;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT;

public class WaitUntilExpectation<T> implements Interaction, IsSilent {

    private ExpectedCondition<T> expectedCondition;
    private Duration duration;

    public WaitUntilExpectation(ExpectedCondition<T> expectedCondition) {
        this.expectedCondition = expectedCondition;
        int durationInMillis = Injectors.getInjector().getInstance(EnvironmentVariables.class).getPropertyAsInteger(WEBDRIVER_WAIT_FOR_TIMEOUT, 3000);
        this.duration = Duration.ofMillis(durationInMillis);
    }

    @Override
    public <A extends Actor> void performAs(A actor) {
        BrowseTheWeb.as(actor).withTimeoutOf(duration).waitFor(expectedCondition);
    }

    public Interaction forNoMoreThan(Duration duration) {
        this.duration = duration;
        return this;
    }
}
