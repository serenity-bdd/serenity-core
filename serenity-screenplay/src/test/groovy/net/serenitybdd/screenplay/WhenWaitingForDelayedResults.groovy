package net.serenitybdd.screenplay
import net.serenitybdd.core.Serenity
import net.thucydides.core.model.TestResult
import static net.thucydides.core.model.TestResult.*
import net.thucydides.core.steps.StepEventBus
import spock.lang.Specification

import static net.serenitybdd.screenplay.EventualConsequence.eventually
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat
import static org.hamcrest.Matchers.equalTo

class WhenWaitingForDelayedResults extends Specification {

    def setup() {
        Serenity.initialize(this)
        StepEventBus.eventBus.testStarted("some test")
    }

    def cleanup() {
        Serenity.done();
    }

    def "should get a result immediately by default"() {
        given:
            Actor jane = Actor.named("Jane")
            Clicker clicker = new Clicker();
        when:
            jane.should(seeThat(TheClickerValue.of(clicker), equalTo(1)))
        then:
            theTestResult() == SUCCESS
    }

    def "should be able to wait for a result to become available if it is slow to arrive"() {
        given:
            Actor jane = Actor.named("Jane")
            Clicker clicker = new Clicker();
        when:
            jane.should(eventually(seeThat(TheClickerValue.of(clicker), equalTo(10))))
        then:
            theTestResult() == SUCCESS
    }


    def "should not wait forever if the result never arrives"() {
        given:
            Actor jane = Actor.named("Jane")
            Clicker clicker = new Clicker();
        when:
            jane.should(eventually(seeThat(TheClickerValue.of(clicker), equalTo(-1))).waitingForNoLongerThan(100).milliseconds())
        then:
            theTestResult() == FAILURE
    }

    def "should transmit an error if one happens"() {
        given:
            Actor jane = Actor.named("Jane")
            Clicker clicker = new Clicker();
        when:
            jane.should(eventually(seeThat(TheClickerValue.ofBroken(clicker), equalTo(1))).waitingForNoLongerThan(250).milliseconds())
        then:
            theTestResult() == ERROR
    }

    def "should report custom error if one happens"() {
        given:
            Actor jane = Actor.named("Jane")
            Clicker clicker = new Clicker();
        when:
            jane.should(eventually(seeThat(TheClickerValue.of(clicker), equalTo(-1)).
                                  orComplainWith(SomethingBadHappenedException)).
                        waitingForNoLongerThan(100).milliseconds())
        then:
        theFailureClass() == SomethingBadHappenedException.getCanonicalName()
    }

    def "should report custom error if one is declared outside of the eventually scope"() {
        given:
        Actor jane = Actor.named("Jane")
        Clicker clicker = new Clicker();
        when:
        jane.should(eventually(seeThat(TheClickerValue.of(clicker), equalTo(-1))).
                waitingForNoLongerThan(100).milliseconds().orComplainWith(SomethingBadHappenedException))
        then:
        theFailureClass() == SomethingBadHappenedException.getCanonicalName()
    }

    private TestResult theTestResult() {
        StepEventBus.eventBus.baseStepListener.testOutcomes[0].result
    }

    private String theFailureClass() {
        StepEventBus.eventBus.baseStepListener.testOutcomes[0].testFailureClassname
    }
}

class Clicker {
    int count = 0

    def click() {
        count++
    }
}


class TheClickerValue implements Question<Integer> {
    private final Clicker clicker;

    TheClickerValue(Clicker clicker) {
        this.clicker = clicker
    }

    public static TheClickerValue of(Clicker clicker) {
        new TheClickerValue(clicker)
    }

    public static TheClickerValue ofBroken(Clicker clicker) {
        new BrokenClickerValue(clicker)
    }

    @Override
    Integer answeredBy(Actor actor) {
        clicker.click()
        return clicker.count;
    }
}


class TheClickerValueWithAnExpectedException implements Question<Integer> {
    private final Clicker clicker;

    TheClickerValueWithAnExpectedException(Clicker clicker) {
        this.clicker = clicker
    }

    public static TheClickerValueWithAnExpectedException of(Clicker clicker) {
        new TheClickerValueWithAnExpectedException(clicker)
    }

    @Override
    Integer answeredBy(Actor actor) {
        clicker.click()
        if (clicker.count < 10) {
            throw new IllegalStateException("Ignore this")
        }
        return clicker.count;
    }
}

class BrokenClickerValue extends TheClickerValue {
    private final Clicker clicker;

    BrokenClickerValue(Clicker clicker) {
        super(clicker);
    }

    @Override
    Integer answeredBy(Actor actor) {
        throw new SomethingBadHappenedException("Oh crap");
    }
}