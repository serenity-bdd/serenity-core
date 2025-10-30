package net.thucydides.spock.acceptance

import net.thucydides.core.annotations.Steps
import net.thucydides.core.steps.StepEventBus
import net.thucydides.model.domain.TestOutcome
import net.thucydides.spock.ThucydidesEnabled
import spock.lang.Specification

/**
 * Examples of running Thucydides-enabled specifications for non-webtest tests.
 */

@ThucydidesEnabled
class WhenGeneratingThucydidesReports extends Specification {

    @Steps
    SimpleSteps steps

    def "Instantiating step libraries in a Spock specification"() {
        given: "we want to generate Thucydides reports from a Spock specification"
        when: "we run the specification"
            steps.step1()
            steps.step2()
        then: "the reports should be generated when the feature finishes"
    }

    def cleanup() {
        thucydidesReportsShouldHaveBeenGenerated()
    }

    def thucydidesReportsShouldHaveBeenGenerated() {
        def outputDirectory =  new File("target/site/serenity")
        def reportFile = new File(outputDirectory, latestTestOutcomes[0].htmlReport)
        assert reportFile.exists()
    }

    List<TestOutcome> getLatestTestOutcomes() {
        StepEventBus.eventBus.baseStepListener.testOutcomes
    }


}
