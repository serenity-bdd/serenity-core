package serenitycore.net.serenitybdd.core.steps

import serenitycore.net.serenitybdd.core.Serenity
import serenitymodel.net.thucydides.core.annotations.Step
import serenitycore.net.serenitybdd.core.steps.ScenarioActor
import serenitycore.net.thucydides.core.annotations.Steps
import spock.lang.Specification

class WhenUsingScenarioActors extends Specification {

    static class SalespersonActor extends ScenarioActor {
        @Step("#actor makes a sale")
        public void makesASale() {}
    }

    static class AScenarioUsingActors {

        @Steps
        SalespersonActor sally;

        @Steps
        SalespersonActor salesperson;

    }

    def "a scenario actor has a name defined by default from the field name"() {

        given:
            AScenarioUsingActors scenario = new AScenarioUsingActors();
        when:
            Serenity.injectScenarioStepsInto(scenario)
        then:
            scenario.sally.getActorName() == "Sally"
    }


    def "you can override the scenario name in the test"() {

        given:
            AScenarioUsingActors scenario = new AScenarioUsingActors();
        when:
            Serenity.injectScenarioStepsInto(scenario)
        and:
            scenario.salesperson.isCalled("Sam")
        then:
            scenario.salesperson.getActorName() == "Sam"
    }

    def "the default toString() of the actor is the actor name"() {

        given:
            AScenarioUsingActors scenario = new AScenarioUsingActors();
        when:
            Serenity.injectScenarioStepsInto(scenario)
        then:
            scenario.sally.toString() == "Sally"
    }
}
