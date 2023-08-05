package net.thucydides.core.reports.adaptors.specflow

import net.thucydides.model.domain.TestResult
import net.thucydides.model.reports.adaptors.specflow.ScenarioStep
import net.thucydides.model.reports.adaptors.specflow.ScenarioStepReader
import spock.lang.Specification

class WhenReadingAScenarioResult extends Specification {

    private static final NEW_LINE = System.properties["line.separator"]

    def "should identify result lines"() {
        expect:
        ScenarioStepReader.isResult(line) == expected
        where:
            line                | expected
        "  -> done: foo"        | true
        "  -> -> error: foo"    | true
        "  -> skipped things"   | true
        "  -> missing"          | true
        "Given a precondition"  | false
    }

    def "should read step results from scenario output"() {
        when:
             def result = new ScenarioStep(resultLines).result
        then:
            result == expectedResult
        where:
            resultLines                                           | expectedResult
            ["step title", "  -> done: bla bla bla (1.0s)"]       | TestResult.SUCCESS
            ["step title", "  -> error: bla bla bla (1.0s)"]      | TestResult.FAILURE
            ["step title", "  -> skipped stuff"]                  | TestResult.SKIPPED
            ["step title", "  -> No matching step definition"]    | TestResult.PENDING
            ["step title", "  -> Something else"]                 | TestResult.UNDEFINED
    }

    def "should read error messages for failed tests"() {
        given:
            def lines = ["step title", "  -> error: bla bla bla"]
        when:
            def result = new ScenarioStep(lines)
        then:
            result.exception.get().message == "bla bla bla"
    }

    def "should read error messages for multi-linefailed tests"() {
        given:
            def lines = ["step title", "  -> error: bla bla bla", "more bla bla bla"]
        when:
            def result = new ScenarioStep(lines)
        then:
            result.exception.get().message == "bla bla bla" + NEW_LINE + "more bla bla bla"
    }

}

