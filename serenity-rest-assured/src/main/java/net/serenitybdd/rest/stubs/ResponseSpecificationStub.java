package net.serenitybdd.rest.stubs;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.Argument;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseLogSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.hamcrest.Matcher;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 23/07/2015.
 */
public class ResponseSpecificationStub implements ResponseSpecification {
    @Override
    public ResponseSpecification content(Matcher<?> matcher, Matcher<?>... additionalMatchers) {
        return this;
    }

    @Override
    public ResponseSpecification content(List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public Response validate(Response response) {
        return new ResponseStub();
    }

    @Override
    public ResponseSpecification content(String key, Matcher<?> matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ResponseSpecification body(String key, List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ResponseSpecification body(List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ResponseSpecification statusCode(Matcher<? super Integer> expectedStatusCode) {
        return this;
    }

    @Override
    public ResponseSpecification statusCode(int expectedStatusCode) {
        return this;
    }

    @Override
    public ResponseSpecification statusLine(Matcher<? super String> expectedStatusLine) {
        return this;
    }

    @Override
    public ResponseSpecification statusLine(String expectedStatusLine) {
        return this;
    }

    @Override
    public ResponseSpecification headers(Map<String, ?> expectedHeaders) {
        return this;
    }

    @Override
    public ResponseSpecification headers(String firstExpectedHeaderName, Object firstExpectedHeaderValue, Object... expectedHeaders) {
        return this;
    }

    @Override
    public ResponseSpecification header(String headerName, Matcher<?> expectedValueMatcher) {
        return this;
    }

    @Override
    public ResponseSpecification header(String headerName, String expectedValue) {
        return this;
    }

    @Override
    public ResponseSpecification cookies(Map<String, ?> expectedCookies) {
        return this;
    }

    @Override
    public ResponseSpecification cookie(String cookieName) {
        return this;
    }

    @Override
    public ResponseSpecification cookies(String firstExpectedCookieName, Object firstExpectedCookieValue, Object... expectedCookieNameValuePairs) {
        return this;
    }

    @Override
    public ResponseSpecification cookie(String cookieName, Matcher<?> expectedValueMatcher) {
        return this;
    }

    @Override
    public ResponseSpecification cookie(String cookieName, Object expectedValue) {
        return this;
    }

    @Override
    public ResponseLogSpecification log() {
        return new ResponseLogSpecificationStub();
    }

    @Override
    public ResponseSpecification rootPath(String rootPath) {
        return this;
    }

    @Override
    public ResponseSpecification rootPath(String rootPath, List<Argument> arguments) {
        return this;
    }

    @Override
    public ResponseSpecification root(String rootPath, List<Argument> arguments) {
        return this;
    }

    @Override
    public ResponseSpecification root(String rootPath) {
        return this;
    }

    @Override
    public ResponseSpecification noRoot() {
        return this;
    }

    @Override
    public ResponseSpecification noRootPath() {
        return this;
    }

    @Override
    public ResponseSpecification appendRoot(String pathToAppend) {
        return this;
    }

    @Override
    public ResponseSpecification appendRoot(String pathToAppend, List<Argument> arguments) {
        return this;
    }

    @Override
    public ResponseSpecification detachRoot(String pathToDetach) {
        return this;
    }

    @Override
    public ResponseSpecification contentType(ContentType contentType) {
        return this;
    }

    @Override
    public ResponseSpecification contentType(String contentType) {
        return this;
    }

    @Override
    public ResponseSpecification contentType(Matcher<? super String> contentType) {
        return this;
    }

    @Override
    public ResponseSpecification body(Matcher<?> matcher, Matcher<?>... additionalMatchers) {
        return this;
    }

    @Override
    public ResponseSpecification body(String path, Matcher<?> matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ResponseSpecification content(String path, List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return this;
    }

    @Override
    public ResponseSpecification when() {
        return this;
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
    public ResponseSpecification spec(ResponseSpecification responseSpecificationToMerge) {
        return this;
    }

    @Override
    public ResponseSpecification specification(ResponseSpecification responseSpecificationToMerge) {
        return this;
    }

    @Override
    public ResponseSpecification parser(String contentType, Parser parser) {
        return this;
    }

    @Override
    public ResponseSpecification defaultParser(Parser parser) {
        return this;
    }

    @Override
    public Response get(String path, Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response get(String path, Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response post(String path, Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response post(String path, Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response put(String path, Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response put(String path, Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response delete(String path, Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response delete(String path, Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response head(String path, Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response head(String path, Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response patch(String path, Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response patch(String path, Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response options(String path, Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response options(String path, Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response get(URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response post(URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response put(URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response delete(URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response head(URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response patch(URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response options(URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response get(URL url) {
        return new ResponseStub();
    }

    @Override
    public Response post(URL url) {
        return new ResponseStub();
    }

    @Override
    public Response put(URL url) {
        return new ResponseStub();
    }

    @Override
    public Response delete(URL url) {
        return new ResponseStub();
    }

    @Override
    public Response head(URL url) {
        return new ResponseStub();
    }

    @Override
    public Response patch(URL url) {
        return new ResponseStub();
    }

    @Override
    public Response options(URL url) {
        return new ResponseStub();
    }

    @Override
    public Response get() {
        return new ResponseStub();
    }

    @Override
    public Response post() {
        return new ResponseStub();
    }

    @Override
    public Response put() {
        return new ResponseStub();
    }

    @Override
    public Response delete() {
        return new ResponseStub();
    }

    @Override
    public Response head() {
        return new ResponseStub();
    }

    @Override
    public Response patch() {
        return new ResponseStub();
    }

    @Override
    public Response options() {
        return new ResponseStub();
    }
}
