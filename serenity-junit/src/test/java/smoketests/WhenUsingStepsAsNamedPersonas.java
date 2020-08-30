package smoketests;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.core.steps.ScenarioActor;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenUsingStepsAsNamedPersonas {

    public static class Traveller {

        private String actor;

        @Step("#actor flies to {0}")
        void fliesTo(String destination) {}
    }

    public static class TravellerWithConfigurableTitle {

        private String title = "#actor flies to {0}";
        private String actor;

        @Step("!#title")
        void fliesTo(String destination) {}
    }


    public static class Salesperson extends ScenarioActor {


        @Step("#actor makes a sale")
        void makesASale() {}
    }

    @Steps(actor = "Tracy")
    Traveller tracy;

    @Steps
    Salesperson sam;


    @Steps(actor = "Sam")
    Salesperson namedSalesman;

    @Steps
    Salesperson anonymousSalesman;

    @Steps
    TravellerWithConfigurableTitle carrie;


    @Test
    public void shouldBeAbleToNameAPersona() {

        tracy.fliesTo("Tallinn");

        assertThat(stepDescriptionFor("shouldBeAbleToNameAPersona")).isEqualTo("Tracy flies to Tallinn");
    }

    @Test
    public void shouldBeAbleToNameAPersonaWithAConfigurableTitle() {

        carrie.fliesTo("Tallinn");

        assertThat(stepDescriptionFor("shouldBeAbleToNameAPersonaWithAConfigurableTitle")).isEqualTo("Carrie flies to Tallinn");
    }

    public static class FirstClassTraveller extends Traveller {

        @Step("#actor flies in First to {0}")
        void fliesTo(String destination) {}

    }

    @Steps(actor = "Fred")
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

        private final String actor;

        public NamedTraveller(String name) {
            this.actor = name;
        }

        @Step("#actor flies to {0}")
        void fliesTo(String destination) {}
    }

    @Test
    public void shouldNotBreakNormalUsesOfANameField() {

        NamedTraveller traveller = Instrumented.instanceOf(NamedTraveller.class).withProperties("Natalie");
        assertThat(traveller.actor).isEqualTo("Natalie");
    }

    @Test
    public void shouldBeAbleToUseTheNameOfAScenarioActor() {

        sam.makesASale();

        assertThat(stepDescriptionFor("shouldBeAbleToUseTheNameOfAScenarioActor")).isEqualTo("Sam makes a sale");
    }

    @Test
    public void shouldBeAbleToUseTheAnnotatedNameOfAScenarioActor() {

        namedSalesman.makesASale();

        assertThat(stepDescriptionFor("shouldBeAbleToUseTheAnnotatedNameOfAScenarioActor")).isEqualTo("Sam makes a sale");
    }

    @Test
    public void shouldBeAbleToNameActorsAfterCreation() {

        anonymousSalesman.isCalled("Sam");
        anonymousSalesman.makesASale();

        assertThat(stepDescriptionFor("shouldBeAbleToNameActorsAfterCreation")).isEqualTo("Sam makes a sale");
    }

    @Test
    public void shouldStopTestIfAnAssumptionFails() {
        Assume.assumeTrue(false);
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
