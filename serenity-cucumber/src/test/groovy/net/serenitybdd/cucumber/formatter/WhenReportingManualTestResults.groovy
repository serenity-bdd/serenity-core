package net.serenitybdd.cucumber.formatter

import io.cucumber.core.plugin.ManualScenarioChecker
import io.cucumber.core.plugin.TaggedScenario
import net.thucydides.core.model.TestResult
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

import io.cucumber.messages.types.Location
import io.cucumber.messages.types.Tag

class WhenReportingManualTestResults extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def gherkinLocation = new Location();
    def dateChecker = new ManualScenarioChecker(environmentVariables)

    @Unroll
    def "When the @manual-last-tested annotation matches the current.target.version property, a manual scenario is up to date"() {
        when:
        if (currentTargetVersion != null) {
            environmentVariables.setProperty("current.target.version", currentTargetVersion)
        } else {
            environmentVariables.clearProperty("current.target.version")
        }
        def gherkinTags = tags.collect() { new Tag(gherkinLocation,it,it) }


        then:
        dateChecker.scenarioResultIsUpToDate(gherkinTags) == isUpToDate

        where:
        currentTargetVersion | tags                              | isUpToDate
        //
        // Normal, correctly configured cases
        //
        "1.2.3"              | ["@manual", "@manual-last-tested:1.2.3"] | true
        "1.2.4"              | ["@manual", "@manual-last-tested:1.2.3"] | false
        "1.2.2"              | ["@manual", "@manual-last-tested:1.2.3"] | false
        //
        // If no last-tested tag is defined, a manual scenario is always up to date
        //
        "1.2.2"              | ["@manual"]                       | true
        //
        // Don't spit the dummy if the current tag version is not defined
        //
        ""                   | ["@manual", "@manual-last-tested:1.2.3"] | true
        null                 | ["@manual", "@manual-last-tested:1.2.3"] | true
        null                 | ["@manual", "@manual-last-tested:1.2.3"] | true
        null                 | ["@manual-last-tested:1.2.3"]            | true
    }


    @Unroll
    def "Manual test results can be defined with Cucumber tags"() {
        given:
        def gherkinTags = tags.collect() { new Tag(gherkinLocation,it,it) }

        when:
        def manualResult = TaggedScenario.manualResultDefinedIn(gherkinTags)

        then:
        manualResult.isPresent() && manualResult.get() == result

        where:
        tags                    | result
        ["@manual"]             | TestResult.PENDING
        ["@Manual"]             | TestResult.PENDING

        ["@manual:pass"]        | TestResult.SUCCESS
        ["@Manual:pass"]        | TestResult.SUCCESS
        ["@manual:passed"]      | TestResult.SUCCESS
        ["@manual:successful"]  | TestResult.SUCCESS

        ["@manual:failed"]      | TestResult.FAILURE
        ["@manual:failure"]     | TestResult.FAILURE
        ["@manual:fail"]        | TestResult.FAILURE

        ["@manual:compromised"] | TestResult.COMPROMISED
    }

    @Unroll
    def "Scenarios with no @manual tag are not manual"() {
        when:
        def manualResult = TaggedScenario.manualResultDefinedIn([])

        then:
        !manualResult.isPresent()
    }
}