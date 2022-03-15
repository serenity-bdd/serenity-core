package net.serenitybdd.rest.stubs;

import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.matcher.DetailedCookieMatcher;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.Argument;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseLogSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.NotImplementedException;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by john on 23/07/2015.
 */
public class ResponseSpecificationStub implements ResponseSpecification {

    @Override
    public Response validate(final Response response) {
        return new ResponseStub();
    }

    @Override
    public ResponseSpecification time(Matcher<Long> matcher) {
        return this;
    }

    @Override
    public ResponseSpecification time(Matcher<Long> matcher, TimeUnit timeUnit) {
        return this;
    }

    @Override
    public ResponseSpecification body(final String key, final List<Argument> arguments, final Matcher matcher, final Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ResponseSpecification body(final List<Argument> arguments, final Matcher matcher, final Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ResponseSpecification statusCode(final Matcher<? super Integer> expectedStatusCode) {
        return this;
    }

    @Override
    public ResponseSpecification statusCode(final int expectedStatusCode) {
        return this;
    }

    @Override
    public ResponseSpecification statusLine(final Matcher<? super String> expectedStatusLine) {
        return this;
    }

    @Override
    public ResponseSpecification statusLine(final String expectedStatusLine) {
        return this;
    }

    @Override
    public ResponseSpecification headers(final Map<String, ?> expectedHeaders) {
        return this;
    }

    @Override
    public ResponseSpecification headers(final String firstExpectedHeaderName, final Object firstExpectedHeaderValue, final Object... expectedHeaders) {
        return this;
    }

    @Override
    public ResponseSpecification header(final String headerName, final Matcher<?> expectedValueMatcher) {
        return this;
    }

    @Override
    public <T> ResponseSpecification header(String s, Function<String, T> restAssuredFunction, Matcher<? super T> matcher) {
        return this;
    }

    @Override
    public ResponseSpecification header(final String headerName, final String expectedValue) {
        return this;
    }

    @Override
    public ResponseSpecification cookies(final Map<String, ?> expectedCookies) {
        return this;
    }

    @Override
    public ResponseSpecification cookie(final String cookieName) {
        return this;
    }

    @Override
    public ResponseSpecification cookies(final String firstExpectedCookieName, final Object firstExpectedCookieValue, final Object... expectedCookieNameValuePairs) {
        return this;
    }

    @Override
    public ResponseSpecification cookie(final String cookieName, final Matcher<?> expectedValueMatcher) {
        return this;
    }

    @Override
    public ResponseSpecification cookie(String cookieName, DetailedCookieMatcher detailedCookieMatcher) {
        return this;
    }

    @Override
    public ResponseSpecification cookie(final String cookieName, final Object expectedValue) {
        return this;
    }

    @Override
    public ResponseLogSpecification log() {
        return new ResponseLogSpecificationStub();
    }

    @Override
    public ResponseSpecification rootPath(final String rootPath) {
        return this;
    }

    @Override
    public ResponseSpecification rootPath(final String rootPath, final List<Argument> arguments) {
        return this;
    }

    @Override
    public ResponseSpecification root(final String rootPath, final List<Argument> arguments) {
        return this;
    }

    @Override
    public ResponseSpecification root(final String rootPath) {
        return this;
    }

    @Override
    public ResponseSpecification noRoot() {
        return this;
    }

    @Override
    public ResponseSpecification appendRootPath(String pathToAppend) {
        return this;
    }

    @Override
    public ResponseSpecification appendRootPath(String pathToAppend, List<Argument> arguments) {
        return this;
    }

    @Override
    public ResponseSpecification noRootPath() {
        return this;
    }

    @Override
    public ResponseSpecification appendRoot(final String pathToAppend) {
        return this;
    }

    @Override
    public ResponseSpecification detachRootPath(String pathToDetach) {
        return this;
    }

    @Override
    public ResponseSpecification appendRoot(final String pathToAppend, final List<Argument> arguments) {
        return this;
    }

    @Override
    public ResponseSpecification detachRoot(final String pathToDetach) {
        return this;
    }

    @Override
    public ResponseSpecification contentType(final ContentType contentType) {
        return this;
    }

    @Override
    public ResponseSpecification contentType(final String contentType) {
        return this;
    }

    @Override
    public ResponseSpecification contentType(final Matcher<? super String> contentType) {
        return this;
    }

    @Override
    public ResponseSpecification body(final Matcher<?> matcher, final Matcher<?>... additionalMatchers) {
        return this;
    }

    @Override
    public ResponseSpecification body(final String path, final Matcher<?> matcher, final Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public RequestSender when() {
        throw new NotImplementedException("when()");
    }

    @Override
    public RequestSpecification given() {
        return new RequestSpecificationStub();
    }

    @Override
    public ResponseSpecification that() {
        return this;
    }

    @Override
    public RequestSpecification request() {
        return new RequestSpecificationStub();
    }

    @Override
    public ResponseSpecification response() {
        return this;
    }

    @Override
    public ResponseSpecification and() {
        return this;
    }

    @Override
    public RequestSpecification with() {
        return new RequestSpecificationStub();
    }

    @Override
    public ResponseSpecification then() {
        return this;
    }

    @Override
    public ResponseSpecification expect() {
        return this;
    }

    @Override
    public ResponseSpecification spec(final ResponseSpecification responseSpecificationToMerge) {
        return this;
    }

    @Override
    public ResponseSpecification parser(final String contentType, final Parser parser) {
        return this;
    }

    @Override
    public ResponseSpecification defaultParser(final Parser parser) {
        return this;
    }

    @Override
    public ResponseSpecification logDetail(LogDetail logDetail) {
        return this;
    }

    @Override
    public ResponseSpecification onFailMessage(String s) {
        return this;
    }

}
