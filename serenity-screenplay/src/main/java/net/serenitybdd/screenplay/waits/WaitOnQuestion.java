package net.serenitybdd.screenplay.waits;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import org.hamcrest.Matcher;

import java.time.Duration;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.thucydides.model.ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT;

public class WaitOnQuestion extends WaitWithTimeout {

    private final Question question;
    private final Matcher matcher;

    public WaitOnQuestion(Question question , Matcher matcher) {
        this.question = question;
        this.matcher = matcher;
        int durationInMillis = SystemEnvironmentVariables.currentEnvironmentVariables().getPropertyAsInteger(WEBDRIVER_WAIT_FOR_TIMEOUT, 3000);
        this.timeout = Duration.ofMillis(durationInMillis);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.should(eventually(seeThat(question, matcher)).waitingForNoLongerThan(timeout.toMillis()).milliseconds());
    }
}
