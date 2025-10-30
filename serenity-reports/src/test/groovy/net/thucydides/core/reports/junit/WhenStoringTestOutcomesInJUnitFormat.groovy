package net.thucydides.core.reports.junit

import net.thucydides.core.reports.integration.TestStepFactory
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.junit.JUnitXMLOutcomeReporter
import spock.lang.Specification

import java.nio.file.Files
import java.time.ZoneId
import java.time.ZonedDateTime

class WhenStoringTestOutcomesInJUnitFormat extends Specification {

    private static final ZonedDateTime FIRST_OF_JANUARY = ZonedDateTime.of(2013, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
    private static final ZonedDateTime SECOND_OF_JANUARY = ZonedDateTime.of(2013, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault())

    JUnitXMLOutcomeReporter reporter

    File outputDirectory

    def setup() {
        outputDirectory = Files.createTempDirectory("junit").toFile()
        reporter = new JUnitXMLOutcomeReporter(outputDirectory)
    }

    class SomeTestScenario {
        void a_simple_test_case() {
        }

        void should_do_this() {
        }

        void should_do_that() {
        }
    }

    class AUserStory {
    }


    def "should generate a JUnit XML report for an acceptance test run"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.description = "Some description"
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))

            def testOutcomes = TestOutcomes.of([testOutcome])
        when:
            reporter.generateReportsFor(testOutcomes)
        then:
            outputDirectory.list()
    }


    def "JUnit XML report should contain a testsuite element with a summary of the test results"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.description = "Some description"
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))

            def testOutcomes = TestOutcomes.of([testOutcome])
        when:
            reporter.generateReportsFor(testOutcomes)

            def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text
        then:
            junitXMLReport.contains '<testsuite errors="0" failures="0" name="net.thucydides.core.reports.junit.WhenStoringTestOutcomesInJUnitFormat.SomeTestScenario" skipped="0" tests="1" time="0.0"'
        and:
            junitXMLReport.contains '<testcase classname="net.thucydides.core.reports.junit.WhenStoringTestOutcomesInJUnitFormat$SomeTestScenario" name="Should do this"/>'
    }

    def "JUnit XML report should handle multiple test results in a test suite"() {
        given:
            def testOutcome1 = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
            testOutcome1.startTime = FIRST_OF_JANUARY
            testOutcome1.description = "Some description"
            testOutcome1.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))

            def testOutcome2 = TestOutcome.forTest("should_do_that", SomeTestScenario.class)
            testOutcome2.startTime = FIRST_OF_JANUARY
            testOutcome2.description = "Some description"
            testOutcome2.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
            testOutcome2.recordStep(TestStepFactory.successfulTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY))

            def testOutcomes = TestOutcomes.of([testOutcome1, testOutcome2])
        when:
            reporter.generateReportsFor(testOutcomes)

         def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text
        then:
            junitXMLReport.contains '<testsuite errors="0" failures="0" name="net.thucydides.core.reports.junit.WhenStoringTestOutcomesInJUnitFormat.SomeTestScenario" skipped="0" tests="2" time="0.0"'
        and:
            junitXMLReport.contains '<testcase classname="net.thucydides.core.reports.junit.WhenStoringTestOutcomesInJUnitFormat$SomeTestScenario" name="Should do that"/>'
    }


    def "JUnit XML report should handle failing tests"() {
        given:
        def testOutcome1 = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome1.startTime = FIRST_OF_JANUARY
        testOutcome1.description = "Some description"
        testOutcome1.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))

        def testOutcome2 = TestOutcome.forTest("should_do_that", SomeTestScenario.class)
        testOutcome2.startTime = FIRST_OF_JANUARY
        testOutcome2.description = "Some description"
        testOutcome2.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
        def failingStep = TestStepFactory.failingTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY)
        failingStep.failedWith(new AssertionError("Oh noses!"))
        testOutcome2.recordStep(failingStep)

        def testOutcomes = TestOutcomes.of([testOutcome1, testOutcome2])
        when:
        reporter.generateReportsFor(testOutcomes)

        def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text
        then:
        junitXMLReport.contains '<failure message="Oh noses!" type="java.lang.AssertionError">Oh noses!</failure>'
    }

    def "JUnit XML report should handle skipped tests"() {
        given:
        def testOutcome1 = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome1.startTime = FIRST_OF_JANUARY
        testOutcome1.description = "Some description"
        testOutcome1.recordStep(TestStepFactory.skippedTestStepCalled("Skipped ").startingAt(FIRST_OF_JANUARY))

        def testOutcomes = TestOutcomes.of([testOutcome1])

        when:
        reporter.generateReportsFor(testOutcomes)
        def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text

        then:
        junitXMLReport.contains '''<skipped/>'''
    }
}
