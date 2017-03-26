package net.serenitybdd.rest.stubs;

import com.jayway.restassured.function.RestAssuredFunction;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RestAssuredResponseOptionsImpl;
import com.jayway.restassured.internal.ValidatableResponseImpl;
import com.jayway.restassured.matcher.ResponseAwareMatcher;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.response.ValidatableResponseLogSpec;
import com.jayway.restassured.specification.Argument;
import com.jayway.restassured.specification.ResponseSpecification;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by john on 23/07/2015.
 */
public class ValidatableResponseStub implements ValidatableResponse {
    @Override
    public ValidatableResponse content(Matcher<?> matcher, Matcher<?>... additionalMatchers) {
        return this;
    }

    @Override
    public ValidatableResponse content(List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ValidatableResponse content(List<Argument> arguments, ResponseAwareMatcher<Response> responseAwareMatcher) {
        return this;
    }

    @Override
    public ValidatableResponse content(String key, Matcher<?> matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ValidatableResponse body(String path, List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ValidatableResponse body(List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ValidatableResponse body(List<Argument> arguments, ResponseAwareMatcher<Response> responseAwareMatcher) {
        return this;
    }

    @Override
    public ValidatableResponse statusCode(Matcher<? super Integer> expectedStatusCode) {
        return this;
    }

    @Override
    public ValidatableResponse statusCode(int expectedStatusCode) {
        return this;
    }

    @Override
    public ValidatableResponse statusLine(Matcher<? super String> expectedStatusLine) {
        return this;
    }

    @Override
    public ValidatableResponse statusLine(String expectedStatusLine) {
        return this;
    }

    @Override
    public ValidatableResponse headers(Map<String, ?> expectedHeaders) {
        return this;
    }

    @Override
    public ValidatableResponse headers(String firstExpectedHeaderName, Object firstExpectedHeaderValue, Object... expectedHeaders) {
        return this;
    }

    @Override
    public ValidatableResponse header(String headerName, Matcher<?> expectedValueMatcher) {
        return this;
    }

    @Override
    public <V> ValidatableResponse header(String s, RestAssuredFunction<String, V> restAssuredFunction, Matcher<? super V> matcher) {
        return this;
    }

    @Override
    public ValidatableResponse header(String headerName, String expectedValue) {
        return this;
    }

    @Override
    public ValidatableResponse cookies(Map<String, ?> expectedCookies) {
        return this;
    }

    @Override
    public ValidatableResponse cookie(String cookieName) {
        return this;
    }

    @Override
    public ValidatableResponse cookies(String firstExpectedCookieName, Object firstExpectedCookieValue, Object... expectedCookieNameValuePairs) {
        return null;
    }

    @Override
    public ValidatableResponse cookie(String cookieName, Matcher<?> expectedValueMatcher) {
        return this;
    }

    @Override
    public ValidatableResponse cookie(String cookieName, Object expectedValue) {
        return this;
    }

    @Override
    public ValidatableResponse rootPath(String rootPath) {
        return this;
    }

    @Override
    public ValidatableResponse rootPath(String rootPath, List<Argument> arguments) {
        return this;
    }

    @Override
    public ValidatableResponse root(String rootPath, List<Argument> arguments) {
        return null;
    }

    @Override
    public ValidatableResponse root(String rootPath) {
        return this;
    }

    @Override
    public ValidatableResponse noRoot() {
        return this;
    }

    @Override
    public ValidatableResponse noRootPath() {
        return this;
    }

    @Override
    public ValidatableResponse appendRoot(String pathToAppend) {
        return this;
    }

    @Override
    public ValidatableResponse appendRoot(String pathToAppend, List<Argument> arguments) {
        return this;
    }

    @Override
    public ValidatableResponse detachRoot(String pathToDetach) {
        return this;
    }

    @Override
    public ValidatableResponse contentType(ContentType contentType) {
        return this;
    }

    @Override
    public ValidatableResponse contentType(String contentType) {
        return this;
    }

    @Override
    public ValidatableResponse contentType(Matcher<? super String> contentType) {
        return this;
    }

    @Override
    public ValidatableResponse body(Matcher<?> matcher, Matcher<?>... additionalMatchers) {
        return this;
    }

    @Override
    public ValidatableResponse body(String path, List<Argument> arguments, ResponseAwareMatcher<Response> responseAwareMatcher) {
        return this;
    }

    @Override
    public ValidatableResponse body(String path, ResponseAwareMatcher<Response> responseAwareMatcher) {
        return this;
    }

    @Override
    public ValidatableResponse body(String path, Matcher<?> matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ValidatableResponse content(String path, List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ValidatableResponse content(String path, List<Argument> arguments, ResponseAwareMatcher<Response> responseAwareMatcher) {
        return this;
    }

    @Override
    public ValidatableResponse content(String path, ResponseAwareMatcher<Response> responseAwareMatcher) {
        return this;
    }

    @Override
    public ValidatableResponse and() {
        return this;
    }

    @Override
    public ValidatableResponse using() {
        return this;
    }

    @Override
    public ValidatableResponse assertThat() {
        return this;
    }

    @Override
    public ValidatableResponse spec(ResponseSpecification responseSpecificationToMerge) {
        return this;
    }

    @Override
    public ValidatableResponse specification(ResponseSpecification responseSpecificationToMerge) {
        return this;
    }

    @Override
    public ValidatableResponse parser(String contentType, Parser parser) {
        return this;
    }

    @Override
    public ValidatableResponse defaultParser(Parser parser) {
        return this;
    }

    @Override
    public ExtractableResponse<Response> extract() {
        return new RestAssuredResponseOptionsImpl();
    }

    @Override
    public ValidatableResponseLogSpec<ValidatableResponse, Response> log() {
        return new ValidatableResponseImpl("",null,null,null,null,null);
    }

    @Override
    public ValidatableResponse time(Matcher<Long> matcher) {
        return new ValidatableResponseImpl("",null,null,null,null,null);
    }

    @Override
    public ValidatableResponse time(Matcher<Long> matcher, TimeUnit timeUnit) {
        return new ValidatableResponseImpl("",null,null,null,null,null);
    }
}
