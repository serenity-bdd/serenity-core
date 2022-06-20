package net.serenitybdd.screenplay.waits;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import org.hamcrest.Matcher;

import java.time.Duration;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT;

public class WaitOnQuestion extends WaitWithTimeout {

    private Question question;
    private Matcher matcher;

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
