package net.thucydides.core.reports.adaptors.xunit

import net.thucydides.model.reports.adaptors.xunit.BasicXUnitLoader
import net.thucydides.model.reports.adaptors.xunit.CouldNotReadXUnitFileException
import net.thucydides.model.reports.adaptors.xunit.model.TestSuite
import spock.lang.Specification

import static net.thucydides.model.util.TestResources.fileInClasspathCalled

/**
 * We want to convert xUnit outputs (possibly with some extra custom fields) to TestOutcomes
 * so that they can be used to generate viable Thucydides test reports.
 */
class WhenReadingAnXUnitFile extends Specification {

    def "should load an xUnit file as a TestSuite"() {
        given:
            def xunitFileSource = fileInClasspathCalled("/xunit/singleTestCase.xml")
            def xunitLoader = new BasicXUnitLoader()
        when:
        TestSuite testSuite = xunitLoader.loadFrom(xunitFileSource)[0]
        then:
            testSuite.name == "aTestSuite"
            testSuite.testCases.size() == 1
            testSuite.testCases[0].classname == "SomeTestClass"
            testSuite.testCases[0].name == "should_do_something"
            testSuite.testCases[0].time == 1.0
    }

    def "should load multiple tests from an xUnit file as a TestSuite"() {
        given:
            def xunitFileSource = fileInClasspathCalled("/xunit/multipleTestCases.xml")
            def xunitLoader = new BasicXUnitLoader()
        when:
            TestSuite testSuite = xunitLoader.loadFrom(xunitFileSource)[0]
        then:
            testSuite.name == "aTestSuite"
            testSuite.testCases.size() == 2
    }

    def "should load a failing xUnit file as a TestSuite"() {
        given:
            def xunitFileSource = fileInClasspathCalled("/xunit/failingTestCase.xml")
            def xunitLoader = new BasicXUnitLoader()
        when:
            TestSuite testSuite = xunitLoader.loadFrom(xunitFileSource)[0]
        then:
            def failingTestCase = testSuite.testCases[0];
            failingTestCase.failure.isPresent()
            failingTestCase.failure.get().message == "Something went wrong"
            failingTestCase.failure.get().type == "failure"
            failingTestCase.failure.get().errorOutput == "All broken"
        and:
            failingTestCase.failure.get().asAssertionFailure().message == "Something went wrong"
        }

    def "should load an xUnit file with an error as a TestSuite"() {
        given:
            def xunitFileSource = fileInClasspathCalled("/xunit/errorTestCase.xml")
            def xunitLoader = new BasicXUnitLoader()
        when:
            TestSuite testSuite = xunitLoader.loadFrom(xunitFileSource)[0]
        then:
            def errorTestCase = testSuite.testCases[0];
            errorTestCase.error.isPresent()
            errorTestCase.error.get().message == "Something exploded"
            errorTestCase.error.get().type == "error"
            errorTestCase.error.get().errorOutput == "All broken"
        and:
            errorTestCase.error.get().asException().message == "Something exploded"
    }

    def "should load a skipped xUnit file as a TestSuite"() {
        given:
            def xunitFileSource = fileInClasspathCalled("/xunit/skippedTestCase.xml")
            def xunitLoader = new BasicXUnitLoader()
        when:
            TestSuite testSuite = xunitLoader.loadFrom(xunitFileSource)[0]
        then:
            def errorTestCase = testSuite.testCases[0];
            errorTestCase.skipped.isPresent()
            errorTestCase.skipped.get().type == "UndefinedStep"
    }

    def "should throw CouldNotReadXUnitFileException if file can't be read"() {
        given:
            def xunitFileSource = fileInClasspathCalled("/xunit/badlyFormedTestCase.xml")
            def xunitLoader = new BasicXUnitLoader()
        when:
            xunitLoader.loadFrom(xunitFileSource)[0]
        then:
            thrown(CouldNotReadXUnitFileException)
    }

    def "should throw CouldNotReadXUnitFileException if file can't be found"() {
        given:
            def nonexistantFile = Mock(File)
            nonexistantFile.toURI() >> new URI("does-not-exist.xml")
            def xunitLoader = new BasicXUnitLoader()

        when:
            xunitLoader.loadFrom(nonexistantFile)[0]
        then:
            thrown(CouldNotReadXUnitFileException)
    }
}
