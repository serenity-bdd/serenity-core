package net.serenitybdd.rest.staging.configuring

import com.jayway.restassured.specification.FilterableRequestSpecification
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.junit.Rule
import spock.lang.Specification

import static net.serenitybdd.rest.staging.SerenityRest.*

/**
 * User: YamStranger
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenConfiguringCookiesForRequest extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should be returned wrapped request specification after adding cookies as map"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "changing cookies configuration on and getting request specification"
            def requestAfterLog = request.cookies(["value": "param"])
        then: "same request should be returned after changing cookies"
            requestAfterLog == request
    }

    def "should be returned wrapped request specification after adding cookies as name and value"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "changing cookies configuration on and getting request specification"
            def requestAfterLog = request.cookie("value", "param")
        then: "same request should be returned after changing cookies"
            requestAfterLog == request
    }

    def "should be returned wrapped request specification after setting cookies only by name"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "changing cookies configuration on and getting request specification"
            def requestAfterLog = request.cookie("value")
        then: "same request should be returned after changing cookies"
            requestAfterLog == request
    }
}