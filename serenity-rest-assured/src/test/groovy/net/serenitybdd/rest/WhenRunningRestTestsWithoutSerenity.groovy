package net.serenitybdd.rest

import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static net.serenitybdd.rest.SerenityRest.rest

class WhenRunningRestTestsWithoutSerenity extends Specification {
    @Rule
    WireMockRule wire = new WireMockRule(0)

    def "Should allow RestAssured get() method calls without throwing NPE"() {
        given:
            def base = "http://localhost:${wire.port()}"
            def path = "/test/number"
            def url = "$base$path"

            stubFor(get(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody("test response")))
        when:
            def result = rest().get(url).then()
        then: "there should be no NPE with message 'No BaseStepListener has been registered' thrown"
            notThrown NullPointerException
        and:
            result.statusCode(200)
    }

    def "Should allow RestAssured post() method calls without throwing NPE"() {
        given:
            def base = "http://localhost:${wire.port()}"
            def path = "/test/number"
            def url = "$base$path"

            stubFor(post(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "text/plain")
                .withBody("test error response")))
        when:
            def result = rest().post(url).then()
        then: "there should be no NPE with message 'No BaseStepListener has been registered' thrown"
            notThrown NullPointerException
        and:
            result.statusCode(500)
    }

    def "Should allow RestAssured put() method calls without throwing NPE"() {
        given:
            def base = "http://localhost:${wire.port()}"
            def path = "/test/number"
            def url = "$base$path"

            stubFor(post(urlEqualTo(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withFault(Fault.RANDOM_DATA_THEN_CLOSE)))
        when:
            def result = rest().put(url).then()
        then: "there should be no NPE with message 'No BaseStepListener has been registered' thrown"
            notThrown NullPointerException
        and:
            result.statusCode(404)
    }

    def "Should allow RestAssured delete() method calls to an invalid URL without throwing NPE"() {
        given:
            def url = "this is not a valid url"
        when:
            rest().delete(url).then()
        then: "there should be no NPE with message 'No BaseStepListener has been registered' thrown"
            notThrown NullPointerException
    }
}
