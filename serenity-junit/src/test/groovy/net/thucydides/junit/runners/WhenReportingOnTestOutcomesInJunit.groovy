package net.thucydides.junit.runners

import net.serenitybdd.annotations.Pending
import net.serenitybdd.annotations.Steps
import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.model.domain.TestResult
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.samples.SampleScenarioSteps
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import java.nio.file.Files

import static org.assertj.core.api.Assertions.assertThat

class WhenReportingOnTestOutcomesInJunit extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()
    }


    @RunWith(SerenityRunner)
    static class APassingScenario {
        @Steps
        SampleScenarioSteps steps

        @Test
        void shouldPass() {
            steps.stepThatSucceeds()
        }
    }

    def "Passing tests should be successful"() {
        given:
            def runner = new SerenityRunner(APassingScenario)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.get(0).result == TestResult.SUCCESS
    }

    @RunWith(SerenityRunner)
    static class APassingScenarioWithoutSteps {
        @Test
        void shouldPass() {
        }
    }

    def "Passing tests without steps should be successful"() {
        given:
            def runner = new SerenityRunner(APassingScenarioWithoutSteps)
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.get(0).result == TestResult.SUCCESS
    }


    @RunWith(SerenityRunner)
    static class AFailingScenario {
        @Steps
        SampleScenarioSteps steps

        @Test
        void shouldPass() {
            steps.stepThatFails()
        }
    }

    def "Failing tests should report failure"() {
        given:
        def runner = new SerenityRunner(AFailingScenario)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.get(0).result == TestResult.FAILURE
    }


    @RunWith(SerenityRunner)
    static class AFailingScenarioWithoutSteps {
        @Test
        void shouldPass() {
            assertThat(1).isEqualTo(2)
        }
    }

    def "Failing tests without step libraries should report failure"() {
        given:
        def runner = new SerenityRunner(AFailingScenarioWithoutSteps)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.get(0).result == TestResult.FAILURE
    }




    @RunWith(SerenityRunner)
    static class AScenarioWithAPendingStep {
        @Steps
        SampleScenarioSteps steps

        @Test
        void shouldBePending() {
            steps.stepThatIsPending()
        }
    }

    def "Tests with pending should report as ignored"() {
        given:
            def runner = new SerenityRunner(AScenarioWithAPendingStep)
        when:
            runner.run(new RunNotifier())
        then:
         runner.testOutcomes.get(0).result == TestResult.PENDING
    }

    @RunWith(SerenityRunner)
    static class APendingScenario {
        @Steps
        SampleScenarioSteps steps

        @Pending
        @Test
        void shouldBePending() {
        }
    }

    def "Pending tests should report as ignored"() {
        given:
        def runner = new SerenityRunner(APendingScenario)
        when:
        runner.run(new RunNotifier())
        then:
        runner.testOutcomes.get(0).result == TestResult.PENDING
    }

}
