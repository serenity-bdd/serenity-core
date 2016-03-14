package net.serenitybdd.rest.staging.rules

import com.jayway.restassured.specification.FilterableRequestSpecification
import com.jayway.restassured.specification.ProxySpecification
import net.serenitybdd.rest.staging.SerenityRest
import org.junit.Rule
import spock.lang.Specification

import static net.serenitybdd.rest.staging.SerenityRest.getDefaultProxy
import static net.serenitybdd.rest.staging.SerenityRest.given
import static net.serenitybdd.rest.staging.SerenityRest.reset
import static net.serenitybdd.rest.staging.SerenityRest.setDefaultProxy

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 1:45 PM
 */
class WhenUsingRestConfigurationRules extends Specification {

    def String value = "someBasePath"
    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            SerenityRest.setDefaultBasePath(value)
        }
    })

    def "configured actions should be executed before this test"() {
        given: "some simple test"
        when: "some action"
        then: "configuration action from rule should be executed once"
            SerenityRest.getDefaultBasePath() == value
    }
}