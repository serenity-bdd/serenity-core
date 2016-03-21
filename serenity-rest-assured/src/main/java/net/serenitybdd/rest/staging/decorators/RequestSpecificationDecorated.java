package net.serenitybdd.rest.staging.decorators;

import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.ResponseSpecificationImpl;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.response.*;
import com.jayway.restassured.specification.*;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
public class RequestSpecificationDecorated implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationDecorated.class);
    private final RequestSpecificationImpl core;
    private final ReflectionHelper<RequestSpecificationImpl> helper;

    public RequestSpecificationDecorated(RequestSpecificationImpl core) {
        this.core = core;
        this.helper = new ReflectionHelper(core);
    }

    @Override
    public RequestSpecification proxy(String host, int port, String scheme) {
        return core.proxy(host, port, scheme);
    }

    @Override
    public String getBaseUri() {
        return core.getBaseUri();
    }

    @Override
    public String getBasePath() {
        return core.getBasePath();
    }

    @Override
    public int getPort() {
        return core.getPort();
    }

    @Override
    public String getRequestContentType() {
        return core.getRequestContentType();
    }

    @Override
    public AuthenticationScheme getAuthenticationScheme() {
        return core.getAuthenticationScheme();
    }

    @Override
    public Map<String, ?> getRequestParams() {
        return core.getRequestParams();
    }

    @Override
    public Map<String, ?> getFormParams() {
        return core.getFormParams();
    }

    @Override
    public Map<String, ?> getPathParams() {
        return core.getPathParams();
    }

    @Override
    public Map<String, ?> getQueryParams() {
        return core.getQueryParams();
    }

    @Override
    public List<MultiPartSpecification> getMultiPartParams() {
        return core.getMultiPartParams();
    }

    @Override
    public Headers getHeaders() {
        return core.getHeaders();
    }

    @Override
    public Cookies getCookies() {
        return core.getCookies();
    }

    @Override
    public <T> T getBody() {
        return core.getBody();
    }

    @Override
    public List<Filter> getDefinedFilters() {
        return core.getDefinedFilters();
    }

    @Override
    public RestAssuredConfig getConfig() {
        return core.getConfig();
    }

    @Override
    public HttpClient getHttpClient() {
        return core.getHttpClient();
    }

    @Override
    public ProxySpecification getProxySpecification() {
        return core.getProxySpecification();
    }

    @Override
    public RequestSpecification body(String body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(byte[] body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(File body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(InputStream body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(Object object) {
        return core.body(object);
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapper mapper) {
        return core.body(object, mapper);
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapperType mapperType) {
        return core.body(object, mapperType);
    }

    @Override
    public RequestSpecification content(String content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(byte[] content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(File content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(InputStream content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(Object object) {
        return core.content(object);
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapperType mapperType) {
        return core.content(object, mapperType);
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapper mapper) {
        return core.content(object, mapper);
    }

    @Override
    public RedirectSpecification redirects() {
        return core.redirects();
    }

    @Override
    public RequestSpecification cookies(String firstCookieName, Object firstCookieValue, Object... cookieNameValuePairs) {
        return core.cookies(firstCookieName, firstCookieValue, cookieNameValuePairs);
    }

    @Override
    public RequestSpecification cookies(Map<String, ?> cookies) {
        return core.cookies(cookies);
    }

    @Override
    public RequestSpecification cookies(Cookies cookies) {
        return core.cookies(cookies);
    }

    @Override
    public RequestSpecification cookie(String cookieName, Object value, Object... additionalValues) {
        return core.cookie(cookieName, value, additionalValues);
    }

    @Override
    public RequestSpecification cookie(String cookieName) {
        return core.cookie(cookieName);
    }

    @Override
    public RequestSpecification cookie(Cookie cookie) {
        return core.cookie(cookie);
    }

    @Override
    public RequestSpecification parameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.parameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification parameters(Map<String, ?> parametersMap) {
        return core.parameters(parametersMap);
    }

    @Override
    public RequestSpecification parameter(String parameterName, Object... parameterValues) {
        return core.parameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification parameter(String parameterName, Collection<?> parameterValues) {
        return core.parameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification params(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.params(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification params(Map<String, ?> parametersMap) {
        return core.params(parametersMap);
    }

    @Override
    public RequestSpecification param(String parameterName, Object... parameterValues) {
        return core.param(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification param(String parameterName, Collection<?> parameterValues) {
        return core.param(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification queryParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.queryParameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification queryParameters(Map<String, ?> parametersMap) {
        return core.queryParameters(parametersMap);
    }

    @Override
    public RequestSpecification queryParameter(String parameterName, Object... parameterValues) {
        return core.queryParameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification queryParameter(String parameterName, Collection<?> parameterValues) {
        return core.queryParameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification queryParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.queryParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification queryParams(Map<String, ?> parametersMap) {
        return core.queryParams(parametersMap);
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Object... parameterValues) {
        return core.queryParam(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Collection<?> parameterValues) {
        return core.queryParam(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification formParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.formParameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification formParameters(Map<String, ?> parametersMap) {
        return core.formParameters(parametersMap);
    }

    @Override
    public RequestSpecification formParameter(String parameterName, Object... parameterValues) {
        return core.formParameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification formParameter(String parameterName, Collection<?> parameterValues) {
        return core.formParameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification formParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.formParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification formParams(Map<String, ?> parametersMap) {
        return core.formParams(parametersMap);
    }

    @Override
    public RequestSpecification formParam(String parameterName, Object... parameterValues) {
        return core.formParam(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification formParam(String parameterName, Collection<?> parameterValues) {
        return core.formParam(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification pathParameter(String parameterName, Object parameterValue) {
        return core.pathParameter(parameterName, parameterValue);
    }

    @Override
    public RequestSpecification pathParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.pathParameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification pathParameters(Map<String, ?> parameterNameValuePairs) {
        return core.pathParameters(parameterNameValuePairs);
    }

    @Override
    public RequestSpecification pathParam(String parameterName, Object parameterValue) {
        return core.pathParam(parameterName, parameterValue);
    }

    @Override
    public RequestSpecification pathParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.pathParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification pathParams(Map<String, ?> parameterNameValuePairs) {
        return core.pathParams(parameterNameValuePairs);
    }

    @Override
    public RequestSpecification config(RestAssuredConfig config) {
        return core.config(config);
    }

    @Override
    public RequestSpecification keystore(String pathToJks, String password) {
        return core.keystore(pathToJks, password);
    }

    @Override
    public RequestSpecification keystore(File pathToJks, String password) {
        return core.keystore(pathToJks, password);
    }

    @Override
    public RequestSpecification trustStore(KeyStore trustStore) {
        return core.trustStore(trustStore);
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation() {
        return core.relaxedHTTPSValidation();
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation(String protocol) {
        return core.relaxedHTTPSValidation(protocol);
    }

    @Override
    public RequestSpecification headers(String firstHeaderName, Object firstHeaderValue, Object... headerNameValuePairs) {
        return core.headers(firstHeaderName, firstHeaderValue, headerNameValuePairs);
    }

    @Override
    public RequestSpecification headers(Map<String, ?> headers) {
        return core.headers(headers);
    }

    @Override
    public RequestSpecification headers(Headers headers) {
        return core.headers(headers);
    }

    @Override
    public RequestSpecification header(String headerName, Object headerValue, Object... additionalHeaderValues) {
        return core.header(headerName, headerValue, additionalHeaderValues);
    }

    @Override
    public RequestSpecification header(Header header) {
        return core.header(header);
    }

    @Override
    public RequestSpecification contentType(ContentType contentType) {
        return core.contentType(contentType);
    }

    @Override
    public RequestSpecification contentType(String contentType) {
        return core.contentType(contentType);
    }

    @Override
    public RequestSpecification accept(ContentType contentType) {
        return core.accept(contentType);
    }

    @Override
    public RequestSpecification accept(String mediaTypes) {
        return core.accept(mediaTypes);
    }

    @Override
    public RequestSpecification multiPart(MultiPartSpecification multiPartSpecification) {
        return core.multiPart(multiPartSpecification);
    }

    @Override
    public RequestSpecification multiPart(File file) {
        return core.multiPart(file);
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file) {
        return core.multiPart(controlName, file);
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file, String mimeType) {
        return core.multiPart(controlName, file, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, Object object) {
        return core.multiPart(controlName, object);
    }

    @Override
    public RequestSpecification multiPart(String controlName, Object object, String mimeType) {
        return core.multiPart(controlName, object, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes) {
        return core.multiPart(controlName, fileName, bytes);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes, String mimeType) {
        return core.multiPart(controlName, fileName, bytes, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream) {
        return core.multiPart(controlName, fileName, stream);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream, String mimeType) {
        return core.multiPart(controlName, fileName, stream, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody) {
        return core.multiPart(controlName, contentBody);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody, String mimeType) {
        return core.multiPart(controlName, contentBody, mimeType);
    }

    @Override
    public AuthenticationSpecification authentication() {
        return core.authentication();
    }

    @Override
    public AuthenticationSpecification auth() {
        return core.auth();
    }

    @Override
    public RequestSpecification port(int port) {
        return core.port(port);
    }

    @Override
    public RequestSpecification spec(RequestSpecification requestSpecificationToMerge) {
        return core.spec(requestSpecificationToMerge);
    }

    @Override
    public RequestSpecification specification(RequestSpecification requestSpecificationToMerge) {
        return core.specification(requestSpecificationToMerge);
    }

    @Override
    public RequestSpecification sessionId(String sessionIdValue) {
        return core.sessionId(sessionIdValue);
    }

    @Override
    public RequestSpecification sessionId(String sessionIdName, String sessionIdValue) {
        return core.sessionId(sessionIdName, sessionIdValue);
    }

    @Override
    public RequestSpecification urlEncodingEnabled(boolean isEnabled) {
        return core.urlEncodingEnabled(isEnabled);
    }

    @Override
    public RequestSpecification filter(Filter filter) {
        return core.filter(filter);
    }

    @Override
    public RequestSpecification filters(List<Filter> filters) {
        return core.filters(filters);
    }

    @Override
    public RequestSpecification filters(Filter filter, Filter... additionalFilter) {
        return core.filters(filter, additionalFilter);
    }

    @Override
    public RequestSpecification noFilters() {
        return core.noFilters();
    }

    @Override
    public <T extends Filter> RequestSpecification noFiltersOfType(Class<T> filterType) {
        return core.noFiltersOfType(filterType);
    }

    @Override
    public RequestLogSpecification log() {
        return core.log();
    }

    @Override
    public ResponseSpecification response() {
        return check(core.response());
    }

    @Override
    public RequestSpecification and() {
        return core.and();
    }

    @Override
    public RequestSpecification with() {
        return core.with();
    }

    @Override
    public ResponseSpecification then() {
        return check(core.then());
    }

    @Override
    public ResponseSpecification expect() {
        return check(core.expect());
    }

    @Override
    public RequestSpecification when() {
        return core.when();
    }

    @Override
    public RequestSpecification given() {
        return core.given();
    }

    @Override
    public RequestSpecification that() {
        return core.that();
    }

    @Override
    public RequestSpecification request() {
        return core.request();
    }

    @Override
    public RequestSpecification baseUri(String baseUri) {
        return core.baseUri(baseUri);
    }

    @Override
    public RequestSpecification basePath(String basePath) {
        return core.basePath(basePath);
    }

    @Override
    public RequestSpecification proxy(String host, int port) {
        return core.proxy(host, port);
    }

    @Override
    public RequestSpecification proxy(String host) {
        return core.proxy(host);
    }

    @Override
    public RequestSpecification proxy(int port) {
        return core.proxy(port);
    }

    @Override
    public RequestSpecification proxy(URI uri) {
        return core.proxy(uri);
    }

    @Override
    public RequestSpecification proxy(ProxySpecification proxySpecification) {
        return core.proxy(proxySpecification);
    }

    @Override
    public Response get(String path, Object... pathParams) {
        return core.get(path, pathParams);
    }

    @Override
    public Response get(String path, Map<String, ?> pathParams) {
        return core.get(path, pathParams);
    }

    @Override
    public Response post(String path, Object... pathParams) {
        return core.post(path, pathParams);
    }

    @Override
    public Response post(String path, Map<String, ?> pathParams) {
        return core.post(path, pathParams);
    }

    @Override
    public Response put(String path, Object... pathParams) {
        return core.put(path, pathParams);
    }

    @Override
    public Response put(String path, Map<String, ?> pathParams) {
        return core.put(path, pathParams);
    }

    @Override
    public Response delete(String path, Object... pathParams) {
        return core.delete(path, pathParams);
    }

    @Override
    public Response delete(String path, Map<String, ?> pathParams) {
        return core.delete(path, pathParams);
    }

    @Override
    public Response head(String path, Object... pathParams) {
        return core.head(path, pathParams);
    }

    @Override
    public Response head(String path, Map<String, ?> pathParams) {
        return core.head(path, pathParams);
    }

    @Override
    public Response patch(String path, Object... pathParams) {
        return core.patch(path, pathParams);
    }

    @Override
    public Response patch(String path, Map<String, ?> pathParams) {
        return core.patch(path, pathParams);
    }

    @Override
    public Response options(String path, Object... pathParams) {
        return core.options(path, pathParams);
    }

    @Override
    public Response options(String path, Map<String, ?> pathParams) {
        return core.options(path, pathParams);
    }

    @Override
    public Response get(URI uri) {
        return core.get(uri);
    }

    @Override
    public Response post(URI uri) {
        return core.post(uri);
    }

    @Override
    public Response put(URI uri) {
        return core.put(uri);
    }

    @Override
    public Response delete(URI uri) {
        return core.delete(uri);
    }

    @Override
    public Response head(URI uri) {
        return core.head(uri);
    }

    @Override
    public Response patch(URI uri) {
        return core.patch(uri);
    }

    @Override
    public Response options(URI uri) {
        return core.options(uri);
    }

    @Override
    public Response get(URL url) {
        return core.get(url);
    }

    @Override
    public Response post(URL url) {
        return core.post(url);
    }

    @Override
    public Response put(URL url) {
        return core.put(url);
    }

    @Override
    public Response delete(URL url) {
        return core.delete(url);
    }

    @Override
    public Response head(URL url) {
        return core.head(url);
    }

    @Override
    public Response patch(URL url) {
        return core.patch(url);
    }

    @Override
    public Response options(URL url) {
        return core.options(url);
    }

    @Override
    public Response get() {
        return core.get();
    }

    @Override
    public Response post() {
        return core.post();
    }

    @Override
    public Response put() {
        return core.put();
    }

    @Override
    public Response delete() {
        return core.delete();
    }

    @Override
    public Response head() {
        return core.head();
    }

    @Override
    public Response patch() {
        return core.patch();
    }

    @Override
    public Response options() {
        return core.options();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private ResponseSpecification check(final ResponseSpecification specification) {
        if (!(specification instanceof RequestSpecificationDecorated)) {
            log.warn("returnted not decorated response, SerenityRest can work incorrectly");
        }
        return specification;
    }

    private ResponseSpecification decorate(final ResponseSpecification specification) {
        if (specification instanceof RequestSpecificationDecorated) {
            return new ResponseSpecificationDecorated((ResponseSpecificationImpl) specification);
        } else {
            return specification;
        }
    }

    public void setResponseSpecification(final ResponseSpecification specification) {
        ((RequestSpecificationImpl) core).setResponseSpecification(decorate(specification));
    }

    public void setresponseSpecification(final ResponseSpecification specification) {
        setResponseSpecification(specification);
    }
}