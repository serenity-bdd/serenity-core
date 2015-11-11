package net.serenitybdd.rest.integration

import com.jayway.restassured.http.ContentType
import net.serenitybdd.core.rest.RestQuery
import net.thucydides.core.model.TestResult
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static com.jayway.restassured.RestAssured.given
import static net.serenitybdd.core.rest.RestMethod.GET
import static net.serenitybdd.rest.SerenityRest.rest
import static org.hamcrest.Matchers.equalTo

/**
 * User: YamStranger
 * Date: 11/11/15
 * Time: 2:09 AM
 */
class WhenRunningHttpsRestTestsThroughSerenity {
    @Rule
    TemporaryFolder temporaryFolder

    File outputDirectory
    BaseStepListener stepListener

    def setup() {
        outputDirectory = temporaryFolder.newFolder()
        stepListener = new BaseStepListener(outputDirectory)
        StepEventBus.eventBus.registerListener(stepListener)
    }
    def cleanup() {
        StepEventBus.eventBus.testFinished()
    }


    def "Should record RestAssured get() method calls"() {
        given:
            def mockListener = Mock(BaseStepListener)
            StepEventBus.eventBus.registerListener(mockListener)
            StepEventBus.eventBus.testStarted("rest")
            def url = "https://api.github.com/repos/serenity-bdd/serenity-core"
        when:
            rest().given().relaxedHTTPSValidation()
            .when()
            .get(url);
        then: "The JSON request should be recorded in the test steps"
            1 * mockListener.recordRestQuery({
                RestQuery query -> query.method == GET  &&
                    query.path == url &&
                    query.statusCode == 200 &&
                    query.responseBody !=null &&
                    query.responseBody.contains("26201720")
            })
    }

}
