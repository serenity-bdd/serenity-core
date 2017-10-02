package smoketests;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenUsingStepsAsNamedPersonas {

    public static class Traveller {

        private String name;

        @Step("#name flies to {0}")
        void fliesTo(String destination) {}
    }

    @Steps(name = "Tracy")
    Traveller tracy;

    @Test
    public void shouldBeAbleToNameAPersona() {

        tracy.fliesTo("Tallinn");

        assertThat(stepDescriptionFor("shouldBeAbleToNameAPersona")).isEqualTo("Tracy flies to Tallinn");
    }

    public static class FirstClassTraveller extends Traveller {

        @Step("#name flies in First to {0}")
        void fliesTo(String destination) {}

    }

    @Steps(name = "Fred")
    Traveller fred;

    @Test
    public void shouldBeAbleToNameAPersonaInASuperclass() {

        fred.fliesTo("Frankfurt");

        assertThat(stepDescriptionFor("shouldBeAbleToNameAPersonaInASuperclass")).isEqualTo("Fred flies to Frankfurt");
    }

    @Steps
    Traveller joe_smith;

    @Test
    public void shouldUseFieldNameIfNoNameIsProvided() {

        joe_smith.fliesTo("Frankfurt");

        assertThat(stepDescriptionFor("shouldUseFieldNameIfNoNameIsProvided")).isEqualTo("Joe Smith flies to Frankfurt");
    }

    public static class NamedTraveller {

        private final String name;

        public NamedTraveller(String name) {
            this.name = name;
        }

        @Step("#name flies to {0}")
        void fliesTo(String destination) {}
    }

    @Test
    public void shouldNotBreakNormalUsesOfANameField() {

        NamedTraveller traveller = Instrumented.instanceOf(NamedTraveller.class).withProperties("Natalie");

        assertThat(traveller.name).isEqualTo("Natalie");
    }

    private String stepDescriptionFor(String testName) {
         Optional<TestOutcome> outcome = StepEventBus.getEventBus()
                .getBaseStepListener()
                .getTestOutcomes()
                .stream()
                .filter(o -> o.getName().equals(testName))
                .findFirst();

         return outcome.get().getTestSteps().get(0).getDescription();
    }
}
