package net.thucydides.core.reports.junit

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.integration.TestStepFactory
import net.thucydides.core.steps.TestFailureCause
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class WhenStoringTestOutcomesInJUnitFormat extends Specification {

    private static final DateTime FIRST_OF_JANUARY = new LocalDateTime(2013, 1, 1, 0, 0, 0, 0).toDateTime()
    private static final DateTime SECOND_OF_JANUARY = new LocalDateTime(2013, 1, 2, 0, 0, 0, 0).toDateTime()

    def JUnitXMLOutcomeReporter reporter

    File outputDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        outputDirectory = temporaryFolder.newFolder()
        reporter = new JUnitXMLOutcomeReporter(outputDirectory);
    }

    class SomeTestScenario {
        public void a_simple_test_case() {
        }

        public void should_do_this() {
        }

        public void should_do_that() {
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
        def failingStep = TestStepFactory.failingTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY);
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

    def "JUnit XML report should handle flaky tests"() {
        given:
        def testOutcome1 = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
        testOutcome1.startTime = FIRST_OF_JANUARY
        testOutcome1.description = "Some description"
        testOutcome1.addTag(TestTag.withName("Retries: 1").andType("unstable test"));
        testOutcome1.setFlakyTestFailureCause(TestFailureCause.from(new AssertionError("Flaky test assertion error")))
        testOutcome1.recordStep(TestStepFactory.flakyTestStepCalled("UNSTABLE TEST:FailureHistory").startingAt(FIRST_OF_JANUARY))
        testOutcome1.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))

        def testOutcomes = TestOutcomes.of([testOutcome1])

        when:
        reporter.generateReportsFor(testOutcomes)
        def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text

        then:
        junitXMLReport.contains '''<flakyFailure message="Flaky test assertion error" type="java.lang.AssertionError">UNSTABLE TEST:FailureHistory<system-err>Flaky test assertion error\n</system-err></flakyFailure>'''
    }
}