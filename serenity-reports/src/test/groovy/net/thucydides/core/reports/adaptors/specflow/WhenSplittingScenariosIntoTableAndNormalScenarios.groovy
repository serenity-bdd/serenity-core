package net.thucydides.core.reports.adaptors.specflow

import net.thucydides.model.reports.adaptors.specflow.ScenarioSplitter
import net.thucydides.model.reports.adaptors.specflow.SpecflowScenario
import spock.lang.Specification

class WhenSplittingScenariosIntoTableAndNormalScenarios extends Specification {

    def "should split a list with one scenario into a single scenario"() {
        given:
            def output = [ "***** my.SpecFlow.Features.MyFeature.MyScenario()",
                           "Given a precondition",
                           "-> done: bla bla bla (2.3s)",
                           "When something happens",
                           "-> done: bla bla bla (1.0s)",
                           "Then some outcome should occur",
                           "-> error: bla bla bla",
                           "more bla bla bla" ]
        when:
            List<SpecflowScenario> scenarios = ScenarioSplitter.on(output).split();
        then:
            scenarios.size() == 1
        and:
            scenarios[0].titleLine == "***** my.SpecFlow.Features.MyFeature.MyScenario"
        and :
            scenarios[0].steps == ["Given a precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla" ]
    }

    def "should split a list with several scenario into several scenarios"() {
        given:
            def output = [
                    "***** my.SpecFlow.Features.MyFeature.MyScenario()",
                    "Given a precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla",
                    "***** my.SpecFlow.Features.MyFeature.MyOtherScenario()",
                    "Given another precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla" ]
        when:
            List<SpecflowScenario> scenarios = ScenarioSplitter.on(output).split();
        then:
            scenarios.size() == 2
        and:
            scenarios[0].titleLine == "***** my.SpecFlow.Features.MyFeature.MyScenario"
            scenarios[1].titleLine == "***** my.SpecFlow.Features.MyFeature.MyOtherScenario"
        and :
            scenarios[0].steps == ["Given a precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla" ]
        and :
            scenarios[1].steps == ["Given another precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla" ]
    }

    def "should split a list with several scenario in a table into a single table"() {
        given:
        def output = [
                "***** my.SpecFlow.Features.MyFeature.MyScenario(\"A\",\"B\",\"C\")",
                "Given a precondition",
                "-> done: bla bla bla (2.3s)",
                "When something happens",
                "-> done: bla bla bla (1.0s)",
                "Then some outcome should occur",
                "-> error: bla bla bla",
                "more bla bla bla",
                "***** my.SpecFlow.Features.MyFeature.MyScenario(\"D\",\"E\",\"F\")",
                "Given another precondition",
                "-> done: bla bla bla (2.3s)",
                "When something happens",
                "-> done: bla bla bla (1.0s)",
                "Then some outcome should occur",
                "-> error: bla bla bla",
                "more bla bla bla" ]
        when:
            List<SpecflowScenario> scenarios = ScenarioSplitter.on(output).split();
        then:
            scenarios.size() == 1
        and:
            scenarios[0].rows.size() == 2
        and:
            scenarios[0].rows[0].rowSteps.size() == 7 && scenarios[0].rows[1].rowSteps.size() == 7
        and:
            scenarios[0].titleLine == "***** my.SpecFlow.Features.MyFeature.MyScenario"
    }

    def "should distinguish tables from simple scenarios"() {
        given:
            def output = [
                    "***** my.SpecFlow.Features.MyFeature.MyFirstScenario()",
                    "Given a precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla",
                    "***** my.SpecFlow.Features.MyFeature.MyScenario(\"A\",\"B\",\"C\")",
                    "Given a precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla",
                    "***** my.SpecFlow.Features.MyFeature.MyScenario(\"D\",\"E\",\"F\")",
                    "Given another precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla",
                    "***** my.SpecFlow.Features.MyFeature.MyOtherScenario()",
                    "Given a precondition",
                    "-> done: bla bla bla (2.3s)",
                    "When something happens",
                    "-> done: bla bla bla (1.0s)",
                    "Then some outcome should occur",
                    "-> error: bla bla bla",
                    "more bla bla bla"]
        when:
            List<SpecflowScenario> scenarios = ScenarioSplitter.on(output).split();
        then:
            scenarios.size() == 3
        and:
            scenarios[1].rows.size() == 2
    }

}
