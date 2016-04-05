package net.serenitybdd.rest.staging.configuring

import com.jayway.restassured.specification.FilterableRequestSpecification
import net.serenitybdd.rest.staging.decorators.request.RequestSpecificationDecorated
import net.serenitybdd.rest.staging.rules.RestConfigurationAction
import net.serenitybdd.rest.staging.rules.RestConfigurationRule
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.jayway.restassured.http.ContentType.ANY
import static com.jayway.restassured.http.ContentType.JSON
import static com.jayway.restassured.http.ContentType.XML
import static net.serenitybdd.rest.staging.SerenityRest.given
import static net.serenitybdd.rest.staging.SerenityRest.reset

/**
 * User: YamStranger
 * Date: 3/30/16
 * Time: 9:57 AM
 */
class WhenConfiguringMultiPartRequests extends Specification {

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    @Rule
    def TemporaryFolder directory = new TemporaryFolder();

    def "should be returned wrapped request specification after multiPart configurations with file without mimeType"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "configuring multi part without mimeType"
            def requestAfterLog = request.multiPart(directory.newFile());
        then: "same request should be returned after multipart operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }

    def "should be returned wrapped request specification after multiPart configurations with file with controlName"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "configuring multi part with file and control name"
            def requestAfterLog = request.multiPart("controlName", directory.newFile());
        then: "same request should be returned after multipart operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }

    def "should be returned wrapped request specification after multiPart configurations with file with controlName and mimetype"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "configuring multi part with file and control name and mimeType"
            def requestAfterLog = request.multiPart("controlName", directory.newFile(), ANY.toString());
        then: "same request should be returned after multipart operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }

    def "should be returned wrapped request specification after multiPart configurations with bytes array with controlName and mimetype"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
        when: "configuring multi part with control name and mimeType"
            def requestAfterLog = request.multiPart("controlName", [1, 2, 3], ANY.toString());
        then: "same request should be returned after multipart operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }

    def "should be returned wrapped request specification after multiPart configurations with bytes input stream with controlName and mimetype"() {
        given: "request initialised"
            def request = (FilterableRequestSpecification) given();
            def file = directory.newFile()
            file.write("<object></object>")
        when: "configuring multi part with input stream and  control name and mimeType"
            def requestAfterLog = request.multiPart("controlName", "${file.getName()}", new FileInputStream(file), XML.toString());
        then: "same request should be returned after multipart operation"
            requestAfterLog == request
        and: "request should be wrapped"
            requestAfterLog instanceof RequestSpecificationDecorated
    }
}