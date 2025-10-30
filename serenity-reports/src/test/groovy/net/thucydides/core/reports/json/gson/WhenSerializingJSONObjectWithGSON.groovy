package net.thucydides.core.reports.json.gson

import net.thucydides.core.reports.integration.TestStepFactory
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.reports.json.AScenarioHasNoNameException
import net.thucydides.model.reports.json.gson.GsonJSONConverter
import net.thucydides.model.util.EnvironmentVariables
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZonedDateTime

class WhenSerializingJSONObjectWithGSON extends Specification {

    private static final ZonedDateTime FIRST_OF_JANUARY = ZonedDateTime.of(2013, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
    private static final ZonedDateTime SECOND_OF_JANUARY = ZonedDateTime.of(2013, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault())

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    class SomeTestScenario {
        void a_simple_test_case() {
        }

        void should_do_this() {
        }

        void should_do_that() {
        }
    }

    def "should serialize TestOutcomes to JSON"() {
        given:
        GsonJSONConverter converter = new GsonJSONConverter(environmentVariables)
        and:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("foo")
            testOutcome.startTime = FIRST_OF_JANUARY

            testOutcome.description = "Some description"
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                    startingAt(FIRST_OF_JANUARY))

        when:
            OutputStream outputStream = new ByteArrayOutputStream()

            converter.toJson(testOutcome, outputStream)

            def json = outputStream.toString()

        and:
            InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        def loadedTestOutcome = converter.fromJson(stream).get()
        then:
            loadedTestOutcome == testOutcome
    }

    def "should serialize TestOutcomes with context to JSON"() {
        given:
           GsonJSONConverter converter = new GsonJSONConverter(environmentVariables)
           environmentVariables.setProperty("context","chrome")
        and:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("foo")
            testOutcome.environmentVariables = environmentVariables
            testOutcome.startTime = FIRST_OF_JANUARY

            testOutcome.description = "Some description"

        when:
            OutputStream outputStream = new ByteArrayOutputStream()
            converter.toJson(testOutcome, outputStream)
            def json = outputStream.toString()
        and:
            InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
        def loadedTestOutcome = converter.fromJson(stream).get()
        then:
            loadedTestOutcome.context == "chrome"
    }

    def "should load serialized test outcomes from JSON"() {
        given:
            GsonJSONConverter converter = new GsonJSONConverter(environmentVariables)
        and:
            def jsonFile = this.class.getResource(filename).getPath()
        when:
            def loadedTestOutcome = converter.fromJson(new FileInputStream(new File(jsonFile))).get()
        then:
            loadedTestOutcome != null && loadedTestOutcome.name == name
        where:
            filename                                       | name
            "/json-reports/sample-report-1.json"           | 'should_do_this'
            "/json-reports/sample-report-2.json"           | 'should_do_this_too'
            "/json-reports/sample-report-3.json"           | 'an_acceptance_test_run'
            "/json-reports/sample-report-4.json"           | 'Another sample report with unicode'
    }

    def "should load all test outcome fields correctly from JSON"() {
        given:
            GsonJSONConverter converter = new GsonJSONConverter(environmentVariables)
        and:
            def jsonFile = this.class.getResource('/json-reports/display_product_details.json').getPath()
        when:
            def loadedTestOutcome = converter.fromJson(new FileInputStream(new File(jsonFile))).get()
        then:
            loadedTestOutcome != null  &&
            loadedTestOutcome.name == "Display product details from the search list" &&
            loadedTestOutcome.tags.size() == 4
    }

    def "should report an error if a JSON scenario has no name"() {
        given:
            GsonJSONConverter converter = new GsonJSONConverter(environmentVariables)
        and:
            def jsonFile = this.class.getResource('/invalid-json-reports/with-no-name.json').getPath()
        when:
            converter.fromJson(new FileInputStream(new File(jsonFile)))
        then:
            thrown(AScenarioHasNoNameException)
    }
}
