package net.serenitybdd.rest.stubs;

import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.internal.RequestLogSpecificationImpl;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.AuthenticationSpecification;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestLogSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

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
    public RequestSpecification body(final String body) {
        return this;
    }

    @Override
    public RequestSpecification body(final byte[] body) {
        return this;
    }

    @Override
    public RequestSpecification body(final File body) {
        return this;
    }

    @Override
    public RequestSpecification body(final InputStream body) {
        return this;
    }

    @Override
    public RequestSpecification body(final Object object) {
        return this;
    }

    @Override
    public RequestSpecification body(final Object object, final ObjectMapper mapper) {
        return this;
    }

    @Override
    public RequestSpecification body(final Object object, final ObjectMapperType mapperType) {
        return this;
    }

    @Override
    public RedirectSpecificationStub redirects() {
        return new RedirectSpecificationStub();
    }

    @Override
    public RequestSpecification cookies(final String firstCookieName, final Object firstCookieValue,
                                        final Object... cookieNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification cookies(final Map<String, ?> cookies) {
        return this;
    }

    @Override
    public RequestSpecification cookies(final Cookies cookies) {
        return this;
    }

    @Override
    public RequestSpecification cookie(final String cookieName, final Object value,
                                       final Object... additionalValues) {
        return this;
    }

    @Override
    public RequestSpecification cookie(final String cookieName) {
        return this;
    }

    @Override
    public RequestSpecification cookie(final Cookie cookie) {
        return this;
    }

    @Override
    public RequestSpecification params(final String firstParameterName, final Object firstParameterValue,
                                       final Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification params(final Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification param(final String parameterName, final Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification param(final String parameterName, final Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification queryParams(final String firstParameterName, final Object firstParameterValue,
                                            final Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification queryParams(final Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification queryParam(final String parameterName, final Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification queryParam(final String parameterName, final Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification formParams(final String firstParameterName, final Object firstParameterValue,
                                           final Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification formParams(final Map<String, ?> parametersMap) {
        return this;
    }

    @Override
    public RequestSpecification formParam(final String parameterName, final Object... parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification formParam(final String parameterName, final Collection<?> parameterValues) {
        return this;
    }

    @Override
    public RequestSpecification pathParam(final String parameterName, final Object parameterValue) {
        return this;
    }

    @Override
    public RequestSpecification pathParams(final String firstParameterName, final Object firstParameterValue,
                                           final Object... parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification pathParams(final Map<String, ?> parameterNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification config(final RestAssuredConfig config) {
        return this;
    }

    @Override
    public RequestSpecification keyStore(final String pathToJks, final String password) {
        return this;
    }

    @Override
    public RequestSpecification keyStore(final File pathToJks, final String password) {
        return this;
    }

    @Override
    public RequestSpecification trustStore(String s, String s1) {
        return this;
    }

    @Override
    public RequestSpecification trustStore(File file, String s) {
        return this;
    }

    @Override
    public RequestSpecification trustStore(final KeyStore trustStore) {
        return this;
    }

    @Override
    public RequestSpecification keyStore(KeyStore keyStore) {
        return this;
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation() {
        return this;
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation(final String protocol) {
        return this;
    }

    @Override
    public RequestSpecification headers(final String firstHeaderName,
                                        final Object firstHeaderValue, final Object... headerNameValuePairs) {
        return this;
    }

    @Override
    public RequestSpecification headers(final Map<String, ?> headers) {
        return this;
    }

    @Override
    public RequestSpecification headers(final Headers headers) {
        return this;
    }

    @Override
    public RequestSpecification header(final String headerName, final Object headerValue,
                                       final Object... additionalHeaderValues) {
        return this;
    }

    @Override
    public RequestSpecification header(final Header header) {
        return this;
    }

    @Override
    public RequestSpecification contentType(final ContentType contentType) {
        return this;
    }

    @Override
    public RequestSpecification contentType(final String contentType) {
        return this;
    }

    @Override
    public RequestSpecification accept(final ContentType contentType) {
        return this;
    }

    @Override
    public RequestSpecification accept(final String mediaTypes) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final MultiPartSpecification multiPartSpecification) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final File file) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final File file) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final File file, final String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final Object object) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final Object object, final String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(String s, String s1, Object o, String s2) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final String fileName, final byte[] bytes) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final String fileName, final byte[] bytes,
                                          final String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final String fileName, final InputStream stream) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final String fileName, final InputStream stream,
                                          final String mimeType) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final String contentBody) {
        return this;
    }

    @Override
    public RequestSpecification multiPart(final String controlName, final String contentBody, final String mimeType) {
        return this;
    }

    @Override
    public AuthenticationSpecification auth() {
        return new AuthenticationSpecificationStub();
    }

    @Override
    public RequestSpecification port(final int port) {
        return this;
    }

    @Override
    public RequestSpecification spec(final RequestSpecification requestSpecificationToMerge) {
        return this;
    }

    @Override
    public RequestSpecification sessionId(final String sessionIdValue) {
        return this;
    }

    @Override
    public RequestSpecification sessionId(final String sessionIdName, final String sessionIdValue) {
        return this;
    }

    @Override
    public RequestSpecification urlEncodingEnabled(final boolean isEnabled) {
        return this;
    }

    @Override
    public RequestSpecification filter(final Filter filter) {
        return this;
    }

    @Override
    public RequestSpecification filters(final List<Filter> filters) {
        return this;
    }

    @Override
    public RequestSpecification filters(final Filter filter, final Filter... additionalFilter) {
        return this;
    }

    @Override
    public RequestSpecification noFilters() {
        return this;
    }

    @Override
    public <T extends Filter> RequestSpecification noFiltersOfType(final Class<T> filterType) {
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
    public RequestSpecification baseUri(final String baseUri) {
        return this;
    }

    @Override
    public RequestSpecification basePath(final String basePath) {
        return this;
    }

    @Override
    public RequestSpecification proxy(final String host, int port) {
        return this;
    }

    @Override
    public RequestSpecification proxy(final String host) {
        return this;
    }

    @Override
    public RequestSpecification proxy(final int port) {
        return this;
    }

    @Override
    public RequestSpecification proxy(final String host, final int port, final String scheme) {
        return this;
    }

    @Override
    public RequestSpecification proxy(final URI uri) {
        return this;
    }

    @Override
    public RequestSpecification proxy(final ProxySpecification proxySpecification) {
        return this;
    }

    @Override
    public Response get(final String path, final Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response get(final String path, final Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response post(final String path, final Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response post(final String path, final Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response put(final String path, final Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response put(final String path, final Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response delete(final String path, final Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response delete(final String path, final Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response head(final String path, final Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response head(final String path, final Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response patch(final String path, final Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response patch(final String path, final Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response options(final String path, final Object... pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response options(final String path, final Map<String, ?> pathParams) {
        return new ResponseStub();
    }

    @Override
    public Response get(final URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response post(final URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response put(final URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response delete(final URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response head(final URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response patch(final URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response options(final URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response get(final URL url) {
        return new ResponseStub();
    }

    @Override
    public Response post(final URL url) {
        return new ResponseStub();
    }

    @Override
    public Response put(final URL url) {
        return new ResponseStub();
    }

    @Override
    public Response delete(final URL url) {
        return new ResponseStub();
    }

    @Override
    public Response head(final URL url) {
        return new ResponseStub();
    }

    @Override
    public Response patch(final URL url) {
        return new ResponseStub();
    }

    @Override
    public Response options(final URL url) {
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

    @Override
    public Response request(Method method) {
        return new ResponseStub();
    }

    @Override
    public Response request(String s) {
        return new ResponseStub();
    }

    @Override
    public Response request(Method method, String s, Object... objects) {
        return new ResponseStub();
    }

    @Override
    public Response request(String s, String s1, Object... objects) {
        return new ResponseStub();
    }

    @Override
    public Response request(Method method, URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response request(Method method, URL url) {
        return new ResponseStub();
    }

    @Override
    public Response request(String s, URI uri) {
        return new ResponseStub();
    }

    @Override
    public Response request(String s, URL url) {
        return new ResponseStub();
    }
}
