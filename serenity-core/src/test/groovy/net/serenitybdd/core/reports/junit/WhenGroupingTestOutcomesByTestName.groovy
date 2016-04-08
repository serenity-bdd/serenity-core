package net.serenitybdd.core.reports.junit

import net.serenitybdd.core.reports.TestOutcomesGroupedByName
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.integration.TestStepFactory
import org.joda.time.DateTime
import spock.lang.Specification
/**
 * Created by john on 31/03/2016.
 */
class WhenGroupingTestOutcomesByTestName extends Specification {

    def "should group test outcomes by test name when only one test is present"() {
        given:
            def aTestOutcome = aTestOutcomeFor("a_simple_test_case", SomeTestScenario)
            def theTestOutcomes = [aTestOutcome]
        when:
            def testMapping = TestOutcomesGroupedByName.groupedByTestCase(theTestOutcomes)
        then:
            testMapping == ["net.serenitybdd.core.reports.junit.WhenGroupingTestOutcomesByTestName.SomeTestScenario" : [aTestOutcome]]
    }

    def "should group test outcomes by test name when several tests are present"() {
        given:
            def aTestOutcome = aTestOutcomeFor("a_simple_test_case", SomeTestScenario)
            def anotherTestOutcome = aTestOutcomeFor("should_do_this", SomeTestScenario)
            def theTestOutcomes = [aTestOutcome, anotherTestOutcome]
        when:
            def testMapping = TestOutcomesGroupedByName.groupedByTestCase(theTestOutcomes)
        then:
            testMapping == ["net.serenitybdd.core.reports.junit.WhenGroupingTestOutcomesByTestName.SomeTestScenario" : [aTestOutcome, anotherTestOutcome]]
    }

    def "should group test outcomes by test name when several test cases are present"() {
        given:
            def aTestOutcome = aTestOutcomeFor("a_simple_test_case", SomeTestScenario)
            def anotherTestOutcome = aTestOutcomeFor("should_do_this", SomeTestScenario)
        and:
            def aDifferentTestOutcome = aTestOutcomeFor("a_simple_test_case", SomeOtherTestScenario)
            def anotherDifferentTestOutcome = aTestOutcomeFor("should_do_this", SomeOtherTestScenario)
        and:
            def theTestOutcomes = [aTestOutcome, aDifferentTestOutcome, anotherTestOutcome, anotherDifferentTestOutcome]
        when:
            def testMapping = TestOutcomesGroupedByName.groupedByTestCase(theTestOutcomes)
        then:
            testMapping == ["net.serenitybdd.core.reports.junit.WhenGroupingTestOutcomesByTestName.SomeTestScenario" : [aTestOutcome, anotherTestOutcome],
                            "net.serenitybdd.core.reports.junit.WhenGroupingTestOutcomesByTestName.SomeOtherTestScenario" : [aDifferentTestOutcome, anotherDifferentTestOutcome]]
    }

    def aTestOutcomeFor(def methodName, def testClass) {
        def testOutcome = TestOutcome.forTest(methodName, testClass)
        testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(DateTime.now()))
        return testOutcome
    }

    class SomeTestScenario {
        public void a_simple_test_case() {
        }

        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    class SomeOtherTestScenario {
        public void a_simple_test_case() {
        }

        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

}