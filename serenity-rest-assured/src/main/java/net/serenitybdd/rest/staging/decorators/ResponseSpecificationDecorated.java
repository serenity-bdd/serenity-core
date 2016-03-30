package net.serenitybdd.rest.staging.decorators;

import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.ResponseParserRegistrar;
import com.jayway.restassured.internal.ResponseSpecificationImpl;
import com.jayway.restassured.internal.log.LogRepository;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.*;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.staging.decorators.request.RequestSpecificationDecorated;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
public class ResponseSpecificationDecorated implements FilterableResponseSpecification {
    private static final Logger log = LoggerFactory.getLogger(ResponseSpecificationDecorated.class);
    private final ResponseSpecificationImpl core;
    private final ReflectionHelper<ResponseSpecificationImpl> helper;

    public ResponseSpecificationDecorated(ResponseSpecificationImpl core) {
        this.core = core;
        this.helper = new ReflectionHelper(core);
    }

    @Override
    public Matcher<Integer> getStatusCode() {
        return core.getStatusCode();
    }

    @Override
    public ResponseSpecification statusCode(Matcher<? super Integer> expectedStatusCode) {
        return core.statusCode(expectedStatusCode);
    }

    @Override
    public ResponseSpecification statusCode(int expectedStatusCode) {
        return core.statusCode(expectedStatusCode);
    }

    @Override
    public Matcher<String> getStatusLine() {
        return core.getStatusLine();
    }

    @Override
    public ResponseSpecification statusLine(Matcher<? super String> expectedStatusLine) {
        return core.statusLine(expectedStatusLine);
    }

    @Override
    public ResponseSpecification statusLine(String expectedStatusLine) {
        return core.statusLine(expectedStatusLine);
    }

    @Override
    public boolean hasHeaderAssertions() {
        return core.hasHeaderAssertions();
    }

    @Override
    public boolean hasCookieAssertions() {
        return core.hasCookieAssertions();
    }

    @Override
    public String getResponseContentType() {
        return core.getResponseContentType();
    }

    @Override
    public String getRootPath() {
        return core.getRootPath();
    }

    @Override
    public ResponseSpecification noRootPath() {
        return core.noRootPath();
    }

    @Override
    public ResponseSpecification rootPath(String rootPath) {
        return core.rootPath(rootPath);
    }

    @Override
    public ResponseSpecification rootPath(String rootPath, List<Argument> arguments) {
        return core.rootPath(rootPath, arguments);
    }

    @Override
    public Response validate(Response response) {
        return core.validate(response);
    }

    @Override
    public ResponseSpecification body(String key, List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return core.body(key, arguments, matcher, additionalKeyMatcherPairs);
    }

    @Override
    public ResponseSpecification body(List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return core.body(arguments, matcher, additionalKeyMatcherPairs);
    }

    @Override
    public ResponseSpecification body(Matcher<?> matcher, Matcher<?>... additionalMatchers) {
        return core.body(matcher, additionalMatchers);
    }

    @Override
    public ResponseSpecification body(String path, Matcher<?> matcher, Object... additionalKeyMatcherPairs) {
        return core.body(path, matcher, additionalKeyMatcherPairs);
    }

    @Override
    public ResponseSpecification headers(Map<String, ?> expectedHeaders) {
        return core.headers(expectedHeaders);
    }

    @Override
    public ResponseSpecification headers(String firstExpectedHeaderName, Object firstExpectedHeaderValue, Object... expectedHeaders) {
        return core.headers(firstExpectedHeaderName, firstExpectedHeaderValue, expectedHeaders);
    }

    @Override
    public ResponseSpecification header(String headerName, Matcher<?> expectedValueMatcher) {
        return core.header(headerName, expectedValueMatcher);
    }

    @Override
    public ResponseSpecification header(String headerName, String expectedValue) {
        return core.header(headerName, expectedValue);
    }

    @Override
    public ResponseSpecification cookies(Map<String, ?> expectedCookies) {
        return core.cookies(expectedCookies);
    }

    @Override
    public ResponseSpecification cookies(String firstExpectedCookieName, Object firstExpectedCookieValue, Object... expectedCookieNameValuePairs) {
        return core.cookies(firstExpectedCookieName, firstExpectedCookieValue, expectedCookieNameValuePairs);
    }

    @Override
    public ResponseSpecification cookie(String cookieName) {
        return core.cookie(cookieName);
    }

    @Override
    public ResponseSpecification cookie(String cookieName, Matcher<?> expectedValueMatcher) {
        return core.cookie(cookieName, expectedValueMatcher);
    }

    @Override
    public ResponseSpecification cookie(String cookieName, Object expectedValue) {
        return core.cookie(cookieName, expectedValue);
    }

    @Override
    public ResponseLogSpecification log() {
        return core.log();
    }

    @Override
    public ResponseSpecification root(String rootPath, List<Argument> arguments) {
        return core.root(rootPath, arguments);
    }

    @Override
    public ResponseSpecification root(String rootPath) {
        return core.root(rootPath);
    }

    @Override
    public ResponseSpecification noRoot() {
        return core.noRoot();
    }

    @Override
    public ResponseSpecification appendRoot(String pathToAppend) {
        return core.appendRoot(pathToAppend);
    }

    @Override
    public ResponseSpecification appendRoot(String pathToAppend, List<Argument> arguments) {
        return core.appendRoot(pathToAppend, arguments);
    }

    @Override
    public ResponseSpecification detachRoot(String pathToDetach) {
        return core.detachRoot(pathToDetach);
    }

    @Override
    public ResponseSpecification contentType(ContentType contentType) {
        return core.contentType(contentType);
    }

    @Override
    public ResponseSpecification contentType(String contentType) {
        return core.contentType(contentType);
    }

    @Override
    public ResponseSpecification contentType(Matcher<? super String> contentType) {
        return core.contentType(contentType);
    }

    @Override
    public ResponseSpecification content(String path, List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return core.content(path, arguments, matcher, additionalKeyMatcherPairs);
    }

    @Override
    public ResponseSpecification content(String key, Matcher<?> matcher, Object... additionalKeyMatcherPairs) {
        return core.content(key, matcher, additionalKeyMatcherPairs);
    }

    @Override
    public ResponseSpecification content(Matcher<?> matcher, Matcher<?>... additionalMatchers) {
        return core.content(matcher, additionalMatchers);
    }

    @Override
    public ResponseSpecification content(List<Argument> arguments, Matcher matcher, Object... additionalKeyMatcherPairs) {
        return core.content(arguments, matcher, additionalKeyMatcherPairs);
    }

    @Override
    public ResponseSpecification when() {
        return core.when();
    }

    @Override
    public RequestSpecification given() {
        return core.given();
    }

    @Override
    public ResponseSpecification that() {
        return core.that();
    }

    @Override
    public RequestSpecification request() {
        return core.request();
    }

    @Override
    public ResponseSpecification response() {
        return this;
    }

    @Override
    public ResponseSpecification and() {
        return core.and();
    }

    @Override
    public RequestSpecification with() {
        return core.with();
    }

    @Override
    public ResponseSpecification then() {
        return core.then();
    }

    @Override
    public ResponseSpecification expect() {
        return core.expect();
    }

    @Override
    public ResponseSpecification spec(ResponseSpecification responseSpecificationToMerge) {
        return core.spec(responseSpecificationToMerge);
    }

    @Override
    public ResponseSpecification specification(ResponseSpecification responseSpecificationToMerge) {
        return core.specification(responseSpecificationToMerge);
    }

    @Override
    public ResponseSpecification parser(String contentType, Parser parser) {
        return core.parser(contentType, parser);
    }

    @Override
    public ResponseSpecification defaultParser(Parser parser) {
        return core.defaultParser(parser);
    }

    @Override
    public Response get() {
        return core.get();
    }

    @Override
    public Response get(URL url) {
        return core.get(url);
    }

    @Override
    public Response get(URI uri) {
        return core.get(uri);
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
    public Response post() {
        return core.post();
    }

    @Override
    public Response post(URL url) {
        return core.post(url);
    }

    @Override
    public Response post(URI uri) {
        return core.post(uri);
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
    public Response put() {
        return core.put();
    }

    @Override
    public Response put(URL url) {
        return core.put(url);
    }

    @Override
    public Response put(URI uri) {
        return core.put(uri);
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
    public Response delete() {
        return core.delete();
    }

    @Override
    public Response delete(URL url) {
        return core.delete(url);
    }

    @Override
    public Response delete(URI uri) {
        return core.delete(uri);
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
    public Response head() {
        return core.head();
    }

    @Override
    public Response head(URL url) {
        return core.head(url);
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
    public Response head(URI uri) {
        return core.head(uri);
    }

    @Override
    public Response patch() {
        return core.patch();
    }

    @Override
    public Response patch(URL url) {
        return core.patch(url);
    }

    @Override
    public Response patch(URI uri) {
        return core.patch(uri);
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
    public Response options() {
        return core.options();
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
    public Response options(URI uri) {
        return core.options(uri);
    }

    @Override
    public Response options(URL url) {
        return core.options(url);
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
        if (specification instanceof RequestSpecificationDecorated) {
            return specification;
        } else {
            log.warn("returned not decorated response, SerenityRest can work incorrectly");
            return specification;
        }
    }

    private RequestSpecification decorate(final RequestSpecification specification) {
        if (specification instanceof RequestSpecificationDecorated) {
            return specification;
        } else {
            return new RequestSpecificationDecorated((RequestSpecificationImpl) specification);
        }
    }

    public void setrequestSpecification(final RequestSpecification specification) {
        setRequestSpecification(specification);
    }

    public void setRequestSpecification(final RequestSpecification specification) {
        try {
            this.helper.setValueTo("requestSpecification", decorate(specification));
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set specification to response, SerenityRest can work incorrectly");
        }
    }

    /**
     * This method used when in groovy 'response.assertionClosure = value' called
     *
     * @param assertionClosure HamcrestAssertionClosure value that will be set to repose specification
     */
    public void setassertionClosure(final ResponseSpecificationImpl.HamcrestAssertionClosure assertionClosure) {
        setAssertionClosure(assertionClosure);
    }

    public void setAssertionClosure(final ResponseSpecificationImpl.HamcrestAssertionClosure assertionClosure) {
        try {
            this.helper.setValueTo("assertionClosure", assertionClosure);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set assertion closure to response, SerenityRest can work incorrectly");
        }
    }


    /**
     * This method used when in groovy 'value = response.assertionClosure' called
     *
     * @return HamcrestAssertionClosure value of current object
     */
    public ResponseSpecificationImpl.HamcrestAssertionClosure getassertionClosure() {
        return getAssertionClosure();
    }

    public ResponseSpecificationImpl.HamcrestAssertionClosure getAssertionClosure() {
        try {
            return (ResponseSpecificationImpl.HamcrestAssertionClosure) this.helper.getValueFrom("assertionClosure");
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not get assertion closure from response, SerenityRest can work incorrectly");
        }
    }

    /**
     * This method used when in groovy 'response.restAssuredResponse = value' called
     *
     * @param restAssuredResponse Response value that will be set to rest assured response
     */
    public void setrestAssuredResponse(final Response restAssuredResponse) {
        setRestAssuredResponse(restAssuredResponse);
    }

    public void setRestAssuredResponse(final Response restAssuredResponse) {
        try {
            this.helper.setValueTo("restAssuredResponse", restAssuredResponse);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set assertion closure to response, SerenityRest can work incorrectly");
        }
    }

    /**
     * This method used when in groovy 'value = response.restAssuredResponse' called
     *
     * @return HamcrestAssertionClosure value of current object
     */
    public Response getrestAssuredResponse() {
        return getRestAssuredResponse();
    }

    public Response getRestAssuredResponse() {
        try {
            return (Response) this.helper.getValueFrom("restAssuredResponse");
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not get assertion closure from response, SerenityRest can work incorrectly");
        }
    }


    public LogRepository getLogRepository() {
        return core.getLogRepository();
    }

    public boolean hasAssertionsDefined() {
        return core.hasAssertionsDefined();
    }

    public void setLogRepository(LogRepository logRepository) {
        core.setLogRepository(logRepository);
    }

    public ResponseParserRegistrar getRpr() {
        return core.getRpr();
    }

    public void setConfig(RestAssuredConfig config) {
        core.setConfig(config);
    }

    public RestAssuredConfig getConfig() {
        return core.getConfig();
    }

    public void setRpr(ResponseParserRegistrar rpr) {
        core.setRpr(rpr);
    }

    public boolean hasBodyAssertionsDefined() {
        return core.hasBodyAssertionsDefined();
    }

    public void throwIllegalStateExceptionIfRootPathIsNotDefined(String description) {
        core.throwIllegalStateExceptionIfRootPathIsNotDefined(description);
    }
}