package net.serenitybdd.screenplay.waits;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.SilentInteraction;
import org.hamcrest.Matcher;

public class Wait extends SilentInteraction {

    private Question question;
    private Matcher matcher;
    private long timeout;

    private Wait(Question question, Matcher matcher, long timeout) {
        this.question = question;
        this.matcher = matcher;
        this.timeout = timeout;
    }

    public static WithTimeout until(Question question, Matcher matcher) {
        return new WaitBuilder(question, matcher);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.should(eventually(seeThat(question, matcher)).waitingForNoLongerThan(timeout).milliseconds());
    }

    public static class WaitBuilder implements WithTimeout, WithTimeUnits{

        private Question question;
        private Matcher matcher;
        private long timeout;

        public WaitBuilder(Question question, Matcher matcher) {
            this.question = question;
            this.matcher = matcher;
        }

        public WithTimeUnits forNoLongerThan(long timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public WithTimeUnits forNoLongerThan(int timeout) {
            this.timeout = (long) timeout;
            return this;
        }

        @Override
        public Wait seconds() {
            return new Wait(question, matcher, timeout * 1000);
        }

        @Override
        public Wait milliseconds() {
            return new Wait(question, matcher, timeout);

        }
    }
}
