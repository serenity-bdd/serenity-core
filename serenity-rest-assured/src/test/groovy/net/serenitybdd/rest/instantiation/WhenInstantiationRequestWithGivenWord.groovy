package net.serenitybdd.rest.instantiation

import io.restassured.RestAssured
import io.restassured.specification.FilterableRequestSpecification
import net.serenitybdd.core.webdriver.driverproviders.ChromeDriverCapabilities
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import static net.serenitybdd.rest.SerenityRest.*


/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenInstantiationRequestWithGivenWord extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    def "should return wrapped request and response if used given method"() {
        given: "provided implementation of Rest Assurance"
        when: "creating new request"
            def request = given();
            def response = request.response();
        then: "created request and response should be decorated"
            request instanceof RequestSpecificationDecorated
            response instanceof ResponseSpecificationDecorated
    }

    def "should return wrapped request and response if they initialised separately"() {
        given: "initialised Request"
            def request = RestAssured.given()
            request = request.proxy(10)
        when: "creating new request"
            def generated = given(request)
        then: "created response should be decorated"
            generated instanceof RequestSpecificationDecorated
        and: "parameters should be merged"
            ((FilterableRequestSpecification) generated).getProxySpecification().getPort() == 10
    }

    @Unroll
    def "Chrome experimental options should be added to chrome Capabilities"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty(optionText, optionValue.toString())
            def chromeCap = new ChromeDriverCapabilities(environmentVariables, "");
        when:
            def options = chromeCap.getCapabilities()
        then:
            def exp_opt = optionText.replaceAll("chrome_experimental_options.", "")
            options.getCapability("goog:chromeOptions")[exp_opt] == optionValue
        where:
            optionText                                           | optionValue                   | shouldContain
            "chrome_experimental_options.useAutomationExtension" | false                         | true
            "chrome_experimental_options.test1"                  | "String"                      | true
            "chrome_experimental_options.test2"                  | "Other String that is bigger" | true
            "chrome_experimental_options.test3"                  | true                          | true
            "chrome_experimental_options.test4.test1"            | 4                             | true
    }

    @Unroll
    def "Chrome experimental options should be not include Chrome preference values"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("chrome_preferences.should_not_be_added" , "someValue")
            def chromeCap = new ChromeDriverCapabilities(environmentVariables, "");
        when:
            def options = chromeCap.getCapabilities()
        then:
            !((TreeMap<String, Object>) options.getCapability("goog:chromeOptions")).containsKey("should_not_be_added")
    }
}
