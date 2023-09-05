package net.serenitybdd.screenplay.waits;

import net.serenitybdd.markers.IsSilent;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.time.Duration;

import static net.thucydides.model.ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT;

public class WaitUntilExpectation<T> implements Interaction, IsSilent {

    private final ExpectedCondition<T> expectedCondition;
    private Duration duration;

    public WaitUntilExpectation(ExpectedCondition<T> expectedCondition) {
        this.expectedCondition = expectedCondition;
        int durationInMillis = SystemEnvironmentVariables.currentEnvironmentVariables().getPropertyAsInteger(WEBDRIVER_WAIT_FOR_TIMEOUT, 3000);
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
