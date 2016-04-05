package net.serenitybdd.rest.staging;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.*;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.internal.*;
import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.*;
import net.serenitybdd.rest.staging.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.staging.decorators.ResponseSpecificationDecorated;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.specification.ProxySpecification.host;

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 8:51 AM
 */
public class SerenityRest {
    public static String setDefaultBasePath(final String basePath) {
        return RestAssured.basePath = basePath;
    }

    public static String getDefaultBasePath() {
        return RestAssured.basePath;
    }

    public static int getDefaultPort() {
        return RestAssured.port;
    }

    public static int setDefaultPort(final int port) {
        return RestAssured.port = port;
    }

    public static boolean isUrlEncodingEnabled() {
        return RestAssured.urlEncodingEnabled;
    }

    public static boolean setUrlEncodingEnabled(final boolean urlEncodingEnabled) {
        return RestAssured.urlEncodingEnabled = urlEncodingEnabled;
    }

    public static String getDefaultRootPath() {
        return RestAssured.rootPath;
    }

    public static String setDefaultRootPath(final String rootPath) {
        if (rootPath == null) {
            throw new IllegalArgumentException("RootPath can not be null");
        }
        return RestAssured.rootPath = rootPath;
    }

    public static String getDefaultSessionId() {
        return RestAssured.sessionId;
    }

    public static String setDefaultSessionId(final String sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session id can not be null");
        }
        return RestAssured.sessionId = sessionId;
    }

    public static AuthenticationScheme getDefaultAuthentication() {
        return RestAssured.authentication;
    }

    public static AuthenticationScheme setDefaultAuthentication(final AuthenticationScheme authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("AuthenticationScheme can not be null");
        }
        return RestAssured.authentication = authentication;
    }

    public static RequestSpecification getDefaultRequestSpecification() {
        return RestAssured.requestSpecification;
    }

    public static RequestSpecification setDefaultRequestSpecification(RequestSpecification requestSpecification) {
        return RestAssured.requestSpecification = requestSpecification;
    }

    public static Parser getDefaultParser() {
        return RestAssured.defaultParser;
    }

    public static Parser setDefaultParser(final Parser defaultParser) {
        return RestAssured.defaultParser = defaultParser;
    }

    public static ResponseSpecification getDefaultResponseSpecification() {
        return RestAssured.responseSpecification;
    }

    public static ResponseSpecification setDefaultResponseSpecification(ResponseSpecification responseSpecification) {
        return RestAssured.responseSpecification = responseSpecification;
    }

    //todo should be implemented reset of configs if they used in serenity reports
    public static void reset() {
        RestAssured.reset();
    }

    public static void filters(final List<Filter> filters) {
        RestAssured.filters(filters);
    }

    public static void filters(final Filter filter, final Filter... additionalFilters) {
        RestAssured.filters(filter, additionalFilters);
    }

    public static void replaceFiltersWith(final List<Filter> filters) {
        RestAssured.replaceFiltersWith(filters);
    }

    public static void replaceFiltersWith(final Filter filter, final Filter... additionalFilters) {
        RestAssured.replaceFiltersWith(filter, additionalFilters);
    }

    public static List<Filter> filters() {
        return RestAssured.filters();
    }

    public static void objectMapper(ObjectMapper objectMapper) {
        RestAssured.objectMapper(objectMapper);
    }

    public static ResponseSpecification expect() {
        return given().response();
    }

    public static RequestSpecification with() {
        return given();
    }

    public static List<Argument> withArguments(Object firstArgument, Object... additionalArguments) {
        return RestAssured.withArguments(firstArgument, additionalArguments);
    }

    public static List<Argument> withNoArguments() {
        return RestAssured.withNoArguments();
    }

    public static List<Argument> withArgs(Object firstArgument, Object... additionalArguments) {
        return withArguments(firstArgument, additionalArguments);
    }

    public static List<Argument> withNoArgs() {
        return withNoArguments();
    }

    private static TestSpecificationImpl createTestSpecification() {
        try {
            Method method = RestAssured.class.getDeclaredMethod("createTestSpecification");
            method.setAccessible(true);
            return (TestSpecificationImpl) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalStateException("Can not initialize rest assurance");
        }
    }

    public static RequestSpecification given() {
        final RequestSpecificationImpl generated = (RequestSpecificationImpl) RestAssured.given();
        final RequestSpecificationDecorated request = new RequestSpecificationDecorated(generated);
        final ResponseSpecificationDecorated response = new ResponseSpecificationDecorated(
                (ResponseSpecificationImpl) generated.response());
        return ((TestSpecificationImpl) given(request, response)).getRequestSpecification();
    }

    public static RequestSender when() {
        return given();
    }

    public static RequestSender given(final RequestSpecification request, final ResponseSpecification response) {
        RequestSpecification requestDecorated = null;
        ResponseSpecification responseDecorated = null;
        if (request instanceof RequestSpecificationDecorated) {
            requestDecorated = request;
        } else if (request instanceof RequestSpecificationImpl) {
            requestDecorated = new RequestSpecificationDecorated((RequestSpecificationImpl) request);
        } else {
            throw new IllegalArgumentException("Can not be used custom Request Specification Implementation");
        }

        if (response instanceof ResponseSpecificationDecorated) {
            responseDecorated = response;
        } else if (response instanceof ResponseSpecificationImpl) {
            responseDecorated = new ResponseSpecificationDecorated((ResponseSpecificationImpl) response);
        } else {
            throw new IllegalArgumentException("Can not be used custom Response Specification Implementation");
        }
        return RestAssured.given(requestDecorated, responseDecorated);
    }

    public static RequestSpecification given(RequestSpecification requestSpecification) {
        final RequestSpecificationImpl generated = (RequestSpecificationImpl) RestAssured.given(requestSpecification);
        final RequestSpecificationDecorated request = new RequestSpecificationDecorated(generated);
        final ResponseSpecificationDecorated response = new ResponseSpecificationDecorated(
                (ResponseSpecificationImpl) generated.response());
        return ((TestSpecificationImpl) given(request, response)).getRequestSpecification();
    }

    public static void useRelaxedHTTPSValidation() {
        SerenityRest.useRelaxedHTTPSValidation("SSL");
    }

    public static void useRelaxedHTTPSValidation(String protocol) {
        RestAssured.useRelaxedHTTPSValidation(protocol);
    }

    public static void registerParser(String contentType, Parser parser) {
        RestAssured.registerParser(contentType, parser);
    }

    public static AuthenticationScheme oauth2(String accessToken) {
        return RestAssured.oauth2(accessToken);
    }

    public static void trustStore(KeyStore truststore) {
        RestAssured.trustStore(truststore);
    }

    public static AuthenticationScheme certificate(String certURL, String password) {
        return RestAssured.certificate(certURL, password);
    }

    public static void enableLoggingOfRequestAndResponseIfValidationFails() {
        SerenityRest.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    public static void enableLoggingOfRequestAndResponseIfValidationFails(LogDetail logDetail) {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(logDetail);
    }

    public static AuthenticationScheme certificate(String certURL, String password, CertificateAuthSettings certificateAuthSettings) {
        return RestAssured.certificate(certURL, password, certificateAuthSettings);
    }

    public static AuthenticationScheme form(String userName, String password) {
        return RestAssured.form(userName, password);
    }

    public static PreemptiveAuthProvider preemptive() {
        return RestAssured.preemptive();
    }

    public static AuthenticationScheme form(String userName, String password, FormAuthConfig config) {
        return RestAssured.form(userName, password, config);
    }

    public static AuthenticationScheme oauth2(String accessToken, OAuthSignature signature) {
        return RestAssured.oauth2(accessToken, signature);
    }

    public static AuthenticationScheme basic(String userName, String password) {
        return RestAssured.basic(userName, password);
    }

    public static void proxy(String host, int port, String scheme) {
        setDefaultProxy(host, port, scheme);
    }

    public static void proxy(String host, int port) {
        setDefaultProxy(host, port);
    }

    public static void proxy(int port) {
        setDefaultProxy(port);
    }

    public static void proxy(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Proxy URI cannot be null");
        }
        SerenityRest.setDefaultProxy(new ProxySpecification(uri.getHost(), uri.getPort(), uri.getScheme()));
    }

    public static void proxy(String host) {
        if (UriValidator.isUri(host)) {
            try {
                setDefaultProxy(new URI(host));
            } catch (URISyntaxException e) {
                throw new RuntimeException("Internal error in REST Assured when constructing URI for Proxy.", e);
            }
        } else {
            setDefaultProxy(host(host));
        }
    }

    public static void proxy(ProxySpecification proxySpecification) {
        SerenityRest.setDefaultProxy(proxySpecification);
    }

    public static void setDefaultProxy(String host, int port, String scheme) {
        setDefaultProxy(new ProxySpecification(host, port, scheme));
    }

    public static ProxySpecification setDefaultProxy(final ProxySpecification proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("ProxySpecification can not be null");
        }
        return RestAssured.proxy = proxy;
    }

    public static void setDefaultProxy(String host, int port) {
        setDefaultProxy(ProxySpecification.host(host).withPort(port));
    }

    public static void setDefaultProxy(int port) {
        setDefaultProxy(ProxySpecification.port(port));
    }

    public static void setDefaultProxy(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Proxy URI cannot be null");
        }
        setDefaultProxy(new ProxySpecification(uri.getHost(), uri.getPort(), uri.getScheme()));
    }

    public static ProxySpecification getDefaultProxy() {
        return RestAssured.proxy;
    }

    public static void keystore(File pathToJks, String password) {
        RestAssured.keystore(pathToJks, password);
    }

    public static void keystore(String password) {
        RestAssured.keystore(password);
    }

    public static void keystore(String pathToJks, String password) {
        RestAssured.keystore(pathToJks, password);
    }

    public static Response head(URI uri) {
        return given().head(uri);
    }

    public static Response head() {
        return given().head();
    }

    public static Response head(String path, Object... pathParams) {
        return given().head(path, pathParams);
    }

    public static Response head(String path, Map<String, ?> pathParams) {
        return given().head(path, pathParams);
    }

    public static Response head(URL url) {
        return given().head(url);
    }

    public static RestAssuredConfig config() {
        return RestAssured.config();
    }

    public static RestAssuredConfig getDefaultConfig() {
        return RestAssured.config;
    }

    public static RestAssuredConfig setDefaultConfig(final RestAssuredConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("RestAssuredConfig can not be null");
        }
        return RestAssured.config = config;
    }

    public static AuthenticationScheme oauth(String consumerKey, String consumerSecret, String accessToken, String secretToken, OAuthSignature signature) {
        return RestAssured.oauth(consumerKey, consumerSecret, accessToken, secretToken, signature);
    }

    public static AuthenticationScheme oauth(String consumerKey, String consumerSecret, String accessToken, String secretToken) {
        return RestAssured.oauth(consumerKey, consumerSecret, accessToken, secretToken);
    }

    public static AuthenticationScheme digest(String userName, String password) {
        return RestAssured.digest(userName, password);
    }

    public static Response options() {
        return given().options();
    }

    public static Response options(URL url) {
        return given().options(url);
    }

    public static Response options(URI uri) {
        return given().options(uri);
    }

    public static Response options(String path, Object... pathParams) {
        return given().options(path, pathParams);
    }

    public static Response options(String path, Map<String, ?> pathParams) {
        return given().options(path, pathParams);
    }

    public static void unregisterParser(String contentType) {
        RestAssured.unregisterParser(contentType);
    }

    public static Response patch(String path, Map<String, ?> pathParams) {
        return given().patch(path, pathParams);
    }

    public static Response patch(URI uri) {
        return given().patch(uri);
    }

    public static Response patch(URL url) {
        return given().patch(url);
    }

    public static Response patch() {
        return given().patch();
    }

    public static Response patch(String path, Object... pathParams) {
        return given().patch(path, pathParams);
    }

    public static Response post(String path, Object... pathParams) {
        return given().post(path, pathParams);
    }

    public static Response post(String path, Map<String, ?> pathParams) {
        return given().post(path, pathParams);
    }

    public static Response post(URL url) {
        return given().post(url);
    }

    public static Response post() {
        return given().post();
    }

    public static Response post(URI uri) {
        return given().post(uri);
    }

    public static Response put(URI uri) {
        return given().put(uri);
    }

    public static Response put(String path, Object... pathParams) {
        return given().put(path, pathParams);
    }

    public static Response put() {
        return given().put();
    }

    public static Response put(URL url) {
        return given().put(url);
    }

    public static Response delete(String path, Map<String, ?> pathParams) {
        return given().delete(path, pathParams);
    }

    public static Response delete(URL url) {
        return given().delete(url);
    }

    public static Response delete(URI uri) {
        return given().delete(uri);
    }

    public static Response delete() {
        return given().delete();
    }

    public static Response delete(String path, Object... pathParams) {
        return given().delete(path, pathParams);
    }

    public static Response get(URI uri) {
        return given().get(uri);
    }

    public static Response get(URL url) {
        return given().get(url);
    }

    public static Response get(String path, Object... pathParams) {
        return given().get(path, pathParams);
    }

    public static Response get(String path, Map<String, ?> pathParams) {
        return given().get(path, pathParams);
    }

    public static Response get() {
        return given().get();
    }
}