package net.thucydides.core.reports.json.gson

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.integration.TestStepFactory
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
            def loadedTestOutcome = converter.fromJson(stream)
        then:
            loadedTestOutcome == testOutcome
    }

    def "should load serialized test outcomes from JSON"() {
        given:
            GsonJSONConverter converter = new GsonJSONConverter(environmentVariables)
        and:
            def jsonFile = this.class.getResource("/json-reports/sample-report-1.json").getPath()
        when:
            def loadedTestOutcome = converter.fromJson(new FileInputStream(new File(jsonFile)))
        then:
            loadedTestOutcome != null
    }
}
