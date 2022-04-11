package io.restassured.internal;

import io.restassured.http.ContentType;
import io.restassured.internal.RestAssuredResponseOptionsImpl;
import io.restassured.internal.ValidatableResponseImpl;
import io.restassured.matcher.DetailedCookieMatcher;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.response.ValidatableResponseLogSpec;
import io.restassured.specification.Argument;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by john on 23/07/2015.
 */
public class ValidatableResponseStub implements ValidatableResponse {

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
    public ValidatableResponse header(String s, ResponseAwareMatcher<Response> responseAwareMatcher) {
        return this;
    }

    @Override
    public <V> ValidatableResponse header(String s, Function<String, V> restAssuredFunction, Matcher<? super V> matcher) {
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
    public ValidatableResponse cookie(String cookieName, DetailedCookieMatcher detailedCookieMatcher) {
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
    public ValidatableResponse appendRootPath(String pathToAppend) {
        return this;
    }

    @Override
    public ValidatableResponse appendRoot(String pathToAppend) {
        return this;
    }

    @Override
    public ValidatableResponse appendRootPath(String pathToAppend, List<Argument> arguments) {
        return this;
    }

    @Override
    public ValidatableResponse appendRoot(String pathToAppend, List<Argument> arguments) {
        return this;
    }

    @Override
    public ValidatableResponse detachRootPath(String pathToDetach) {
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
