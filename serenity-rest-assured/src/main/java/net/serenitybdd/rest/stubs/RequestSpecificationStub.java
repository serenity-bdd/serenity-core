package net.serenitybdd.rest.stubs;

import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RequestLogSpecificationImpl;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.response.*;
import com.jayway.restassured.specification.*;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RequestSpecificationStub implements RequestSpecification {
    @Override
    public RequestSpecification body(String body) {
        return this;
    }

    @Override
    public RequestSpecification body(byte[] body) {
        return this;
    }

    @Override
    public RequestSpecification body(File body) {
        return this;
    }

    @Override
    public RequestSpecification body(InputStream body) {
        return this;
    }

    @Override
    public RequestSpecification body(Object object) {
        return this;
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapper mapper) {
        return this;
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapperType mapperType) {
        return this;
    }

    @Override
    public RequestSpecification content(String content) {
        return this;
    }

    @Override
    public RequestSpecification content(byte[] content) {
        return this;
    }

    @Override
    public RequestSpecification content(File content) {
        return this;
    }

    @Override
    public RequestSpecification content(InputStream content) {
        return this;
    }

    @Override
    public RequestSpecification content(Object object) {
        return this;
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapperType mapperType) {
        return this;
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapper mapper) {
        return this;
    }

    @Override
    public RedirectSpecificationStub redirects() {
        return new RedirectSpecificationStub();
    }

    @Override
    public RequestSpecification cookies(String firstCookieName, Object firstCookieValue, Object... cookieNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification cookies(Map<String, ?> cookies) {
        return this;
    }

    @Override
    public RequestSpecification cookies(Cookies cookies) {
        return this;
    }

    @Override
    public RequestSpecification cookie(String cookieName, Object value, Object... additionalValues) {
        return this;
    }

    @Override
    public RequestSpecification cookie(String cookieName) {
        return this;
    }

    @Override
    public RequestSpecification cookie(Cookie cookie) {
        return this;
    }

    @Override
    public RequestSpecification parameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification parameters(Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification parameter(String parameterName, Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification parameter(String parameterName, Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification params(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification params(Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification param(String parameterName, Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification param(String parameterName, Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification queryParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification queryParameters(Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification queryParameter(String parameterName, Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification queryParameter(String parameterName, Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification queryParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification queryParams(Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification formParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification formParameters(Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification formParameter(String parameterName, Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification formParameter(String parameterName, Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification formParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification formParams(Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification formParam(String parameterName, Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification formParam(String parameterName, Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification pathParameter(String parameterName, Object parameterValue) {
        return this;
    }

    @Override
    public RequestSpecification pathParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification pathParameters(Map<String, ?> parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification pathParam(String parameterName, Object parameterValue) {
        return this;
    }

    @Override
    public RequestSpecification pathParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification pathParams(Map<String, ?> parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification config(RestAssuredConfig config) {
        return this;
    }

    @Override
    public RequestSpecification keystore(String pathToJks, String password) {
        return this;
    }

    @Override
    public RequestSpecification keystore(File pathToJks, String password) {
        return this;
    }

    @Override
    public RequestSpecification trustStore(KeyStore trustStore) {
        return this;
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation() {
        return this;
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation(String protocol) {
        return this;
    }

    @Override
    public RequestSpecification headers(String firstHeaderName, Object firstHeaderValue, Object... headerNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification headers(Map<String, ?> headers) {
        return this;
    }

    @Override
    public RequestSpecification headers(Headers headers) {
        return this;
    }

    @Override
    public RequestSpecification header(String headerName, Object headerValue, Object... additionalHeaderValues) {
        return this;
    }

    @Override
    public RequestSpecification header(Header header) {
        return this;
    }

    @Override
    public RequestSpecification contentType(ContentType contentType) {
        return this;
    }

    @Override
    public RequestSpecification contentType(String contentType) {
        return this;
    }

    @Override
    public RequestSpecification accept(ContentType contentType) {
        return this;
    }

    @Override
    public RequestSpecification accept(String mediaTypes) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(MultiPartSpecification multiPartSpecification) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(File file) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file, String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, Object object) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, Object object, String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes, String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream, String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody, String mimeType) {
        return this;
    }

    @Override
    public AuthenticationSpecification authentication() {
        return new AuthenticationSpecificationStub();
    }

    @Override
    public AuthenticationSpecification auth() {
        return new AuthenticationSpecificationStub();
    }

    @Override
    public RequestSpecification port(int port) {
        return this;
    }

    @Override
    public RequestSpecification spec(RequestSpecification requestSpecificationToMerge) {
        return this;
    }

    @Override
    public RequestSpecification specification(RequestSpecification requestSpecificationToMerge) {
        return this;
    }

    @Override
    public RequestSpecification sessionId(String sessionIdValue) {
        return this;
    }

    @Override
    public RequestSpecification sessionId(String sessionIdName, String sessionIdValue) {
        return this;
    }

    @Override
    public RequestSpecification urlEncodingEnabled(boolean isEnabled) {
        return this;
    }

    @Override
    public RequestSpecification filter(Filter filter) {
        return this;
    }

    @Override
    public RequestSpecification filters(List<Filter> filters) {
        return this;
    }

    @Override
    public RequestSpecification filters(Filter filter, Filter... additionalFilter) {
        return this;
    }

    @Override
    public RequestSpecification noFilters() {
        return this;
    }

    @Override
    public <T extends Filter> RequestSpecification noFiltersOfType(Class<T> filterType) {
        return this;
    }

    @Override
    public RequestLogSpecification log() {
        return new RequestLogSpecificationImpl();
    }

    @Override
    public ResponseSpecification response() {
        return new ResponseSpecificationStub();
    }

    @Override
    public RequestSpecification and() {
        return this;
    }

    @Override
    public RequestSpecification with() {
        return this;
    }

    @Override
    public ResponseSpecification then() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification expect() {
        return new ResponseSpecificationStub();
    }

    @Override
    public RequestSpecification when() {
        return this;
    }

    @Override
    public RequestSpecification given() {
        return this;
    }

    @Override
    public RequestSpecification that() {
        return this;
    }

    @Override
    public RequestSpecification request() {
        return this;
    }

    @Override
    public RequestSpecification baseUri(String baseUri) {
        return this;
    }

    @Override
    public RequestSpecification basePath(String basePath) {
        return this;
    }

    @Override
    public RequestSpecification proxy(String host, int port) {
        return this;
    }

    @Override
    public RequestSpecification proxy(String host) {
        return this;
    }

    @Override
    public RequestSpecification proxy(int port) {
        return this;
    }

    @Override
    public RequestSpecification proxy(String host, int port, String scheme) {
        return this;
    }

    @Override
    public RequestSpecification proxy(URI uri) {
        return this;
    }

    @Override
    public RequestSpecification proxy(ProxySpecification proxySpecification) {
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
