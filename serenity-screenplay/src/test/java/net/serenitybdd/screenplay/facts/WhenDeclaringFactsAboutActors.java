package net.serenitybdd.screenplay.facts;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.exceptions.TestCompromisedException;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.model.CastMember;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.givenThat;
import static org.assertj.core.api.Assertions.assertThat;

public class WhenDeclaringFactsAboutActors {

    @Before
    public void startScerentyTest() {
        Serenity.initialize(this);
        StepEventBus.getEventBus().testSuiteStarted(Story.called("sample story"));
        StepEventBus.getEventBus().testStarted("sample test");
    }

    static List<String> knownAccounts = new ArrayList<>();

    static class AnAccount implements Fact {

        private final String accountNumber;

        public AnAccount(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public static Fact numbered(String accountNumber) {
            return new AnAccount(accountNumber);
        }

        public static Fact thatIsIllegal() {
            return actor -> { throw new TestCompromisedException("Illegal account"); };
        }

        @Override
        public void setup(Actor actor) {
            knownAccounts.add(accountNumber);
        }

        @Override
        public String toString() {
            return "An account numbered " + accountNumber;
        }
    }

    @Test
    public void facts_let_us_prepare_an_actor_for_a_scenario() {

        Actor tim = Actor.named("Tim");

        tim.has(AnAccount.numbered("123456"));

        assertThat(knownAccounts).contains("123456");
    }

    static List<String> existingSavingsAccounts = new ArrayList<>();

    static class ASavingsAccount implements Fact {

        private final String accountNumber;

        public ASavingsAccount(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public static Fact numbered(String accountNumber) {
            return new ASavingsAccount(accountNumber);
        }

        @Override
        public void setup(Actor actor) {
            knownAccounts.add(accountNumber);
            existingSavingsAccounts.add(accountNumber);
        }

        @Override
        public void teardown(Actor actor) {
            existingSavingsAccounts.remove(accountNumber);
        }
    }

    @Test
    public void facts_let_us_setup_and_teardown_test_data() {

        Actor tim = Actor.named("Tim");

        tim.has(ASavingsAccount.numbered("Savings-123456"));

        StepEventBus.getEventBus().testFinished();

        assertThat(knownAccounts).contains("Savings-123456");
        assertThat(existingSavingsAccounts).doesNotContain("Savings-123456");
    }

    @Test(expected = TestCompromisedException.class)
    public void facts_can_throw_exceptions() {
        Actor tim = Actor.named("Tim");
        tim.has(AnAccount.thatIsIllegal());
    }

    @Test
    public void facts_are_recorded_in_the_test_reports_in_the_Cast_section() {

        Actor tim = Actor.named("Tim");

        DoHisAccounts doHisAccounts = new DoHisAccounts();

        givenThat(tim).has(AnAccount.numbered("123456"));
        tim.can((Ability)doHisAccounts);

        StepEventBus.getEventBus().testFinished();

        TestOutcome testOutcome = StepEventBus.getEventBus().getBaseStepListener().latestTestOutcome().get();

        assertThat(testOutcome.getActors()).isNotEmpty();
        assertThat(testOutcome.getActors().size() == 1);

        CastMember castMember = testOutcome.getActors().get(0);

        assertThat(castMember.getName()).isEqualTo("Tim");
        assertThat(castMember.getHas()).contains("An account numbered 123456");
        assertThat(castMember.getCan()).contains("Do his accounts");
    }

}

