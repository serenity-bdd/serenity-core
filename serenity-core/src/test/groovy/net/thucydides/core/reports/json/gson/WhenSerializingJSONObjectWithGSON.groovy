package net.thucydides.core.reports.json.gson

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.integration.TestStepFactory
import net.thucydides.core.reports.json.AScenarioHasNoNameException
import net.thucydides.core.steps.samples.SomeTestScenario
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.joda.time.DateTime
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * Created by john on 7/02/15.
 */
class WhenSerializingJSONObjectWithGSON extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def "should serialize TestOutcomes to JSON"() {
        given:
            GsonJSONConverter converter = new GsonJSONConverter(environmentVariables)
        and:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("foo")
            testOutcome.startTime = DateTime.parse("2015-01-01")

            testOutcome.description = "Some description"
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").
                    startingAt(DateTime.parse("2015-01-01")))

        when:
            OutputStream outputStream = new ByteArrayOutputStream()

            converter.toJson(testOutcome, outputStream)

            def json = outputStream.toString()

        and:
            InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            def loadedTestOutcome = converter.fromJson(stream).get()
        then:
            loadedTestOutcome == testOutcome
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
