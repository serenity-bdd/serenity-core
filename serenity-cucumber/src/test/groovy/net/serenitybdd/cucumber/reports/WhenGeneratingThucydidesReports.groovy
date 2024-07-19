package net.serenitybdd.cucumber.reports

import io.cucumber.junit.CucumberRunner
import io.cucumber.plugin.event.Status
import net.serenitybdd.cucumber.integration.SimpleScenario
import net.thucydides.model.reports.OutcomeFormat
import net.thucydides.model.reports.TestOutcomeLoader
import org.assertj.core.util.Files
import spock.lang.Specification

/**
 * Created by john on 23/07/2014.
 */
class WhenGeneratingThucydidesReports extends Specification {

    File outputDirectory

    def setup() {
        outputDirectory = Files.newTemporaryFolder()
    }

    def "should generate a Thucydides report for each executed Cucumber scenario"() {
        given:
        def runtime = CucumberRunner.serenityRunnerForCucumberTestRunner(SimpleScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);

        then:
        runtime.exitStatus.results[0].status.is(Status.PASSED)

        and:
        !recordedTestOutcomes.isEmpty()
    }


}
