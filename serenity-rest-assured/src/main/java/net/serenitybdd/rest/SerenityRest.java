package net.serenitybdd.rest;

import com.google.common.base.Preconditions;
import io.restassured.RestAssured;
import io.restassured.authentication.AuthenticationScheme;
import io.restassured.authentication.CertificateAuthSettings;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.authentication.OAuthSignature;
import io.restassured.authentication.PreemptiveAuthProvider;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.internal.ResponseSpecificationImpl;
import io.restassured.internal.TestSpecificationImpl;
import io.restassured.internal.UriValidator;
import io.restassured.mapper.ObjectMapper;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.Argument;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.utils.RestDecorationHelper;
import net.serenitybdd.rest.utils.RestSpecificationFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.restassured.specification.ProxySpecification.host;

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 8:51 AM
 */
public class SerenityRest {
    private static ThreadLocal<RequestSpecificationDecorated> currentRequestSpecification = new ThreadLocal<>();

    public static RequestSpecification rest() {
        return given();
    }

    public static ValidatableResponse and() {
        return then();
    }

    public static ValidatableResponse then() {
        return lastResponse().then();
    }

    public static Response lastResponse() {
        Preconditions.checkNotNull(currentRequestSpecification, "request specification should be initialized");
        Response lastResponse = currentRequestSpecification.get().getLastResponse();
        Preconditions.checkNotNull(currentRequestSpecification, "response should be created");
        return lastResponse;
    }

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

    public static RequestSpecification setDefaultRequestSpecification(
            final RequestSpecification requestSpecification) {
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

    public static ResponseSpecification setDefaultResponseSpecification(
            final ResponseSpecification responseSpecification) {
        return RestAssured.responseSpecification = responseSpecification;
    }

    //todo should be implemented reset of configs if they used in serenity reports
    public static void reset() {
        RestAssured.reset();
    }

    public static List<Filter> filters(final List<Filter> filters) {
        RestAssured.filters(filters);
        return filters();
    }

    public static List<Filter> filters(final Filter filter, final Filter... additionalFilters) {
        RestAssured.filters(filter, additionalFilters);
        return filters();
    }

    public static List<Filter> replaceFiltersWith(final List<Filter> filters) {
        RestAssured.replaceFiltersWith(filters);
        return filters();
    }

    public static List<Filter> replaceFiltersWith(final Filter filter, final Filter... additionalFilters) {
        RestAssured.replaceFiltersWith(filter, additionalFilters);
        return filters();
    }

    public static List<Filter> filters() {
        return RestAssured.filters();
    }

    public static ObjectMapper objectMapper(final ObjectMapper objectMapper) {
        RestAssured.config = config().objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                .defaultObjectMapper(objectMapper));
        return config().getObjectMapperConfig().defaultObjectMapper();
    }

    public static ResponseSpecification expect() {
        return given().response();
    }

    public static RequestSpecification with() {
        return given();
    }

    public static List<Argument> withArguments(final Object firstArgument, final Object... additionalArguments) {
        return RestAssured.withArgs(firstArgument, additionalArguments);
    }

    public static List<Argument> withNoArguments() {
        return RestAssured.withNoArgs();
    }

    public static List<Argument> withArgs(final Object firstArgument, final Object... additionalArguments) {
        return withArguments(firstArgument, additionalArguments);
    }

    public static List<Argument> withNoArgs() {
        return withNoArguments();
    }

    /**
     * Create a new RestAssured query sequence
     * @return
     */
    public static RequestSpecification given() {
        final RequestSpecificationImpl generated = (RequestSpecificationImpl) RestAssured.given();
        final RequestSpecification request = RestDecorationHelper.decorate(generated);
        final ResponseSpecificationDecorated response =  RestSpecificationFactory.getInstrumentedResponseSpecification((ResponseSpecificationImpl) generated.response());
        return ((TestSpecificationImpl) given(request, response)).getRequestSpecification();
    }

    /**
     * Add an additional clause to a RestAssured query
     * @return
     */
    public static RequestSender andGiven() {
        if(currentRequestSpecification.get() != null) {
            return currentRequestSpecification.get();
        }
        return given();
    }

    /**
     * Clear the current RestAssured test context, so that the next when() call will create a new RestAssured query
     */
    public static void clear() {
        currentRequestSpecification.remove();
    }

    /**
     * Add an action to a RestAssured query, building on previous given() statements if they were called.
     * @return
     */
    public static RequestSender when() {
        if(currentRequestSpecification.get() != null) {
            return currentRequestSpecification.get();
        }
        return given();
    }

    public static RequestSender given(final RequestSpecification request, final ResponseSpecification response) {
        RequestSpecification requestDecorated = RestDecorationHelper.decorate(request);
        ResponseSpecification responseDecorated = RestDecorationHelper.decorate(response);
        RequestSender created = RestAssured.given(requestDecorated, responseDecorated);
        currentRequestSpecification.set(
                (RequestSpecificationDecorated) ((TestSpecificationImpl) created).getRequestSpecification()
        );
        return created;
    }

    public static RequestSpecification given(final RequestSpecification requestSpecification) {
        final RequestSpecificationImpl generated = (RequestSpecificationImpl) RestAssured.given(requestSpecification);
        final RequestSpecification request = RestDecorationHelper.decorate(generated);
        final ResponseSpecificationDecorated response =  RestSpecificationFactory.getInstrumentedResponseSpecification((ResponseSpecificationImpl) generated.response());
        return ((TestSpecificationImpl) given(request, response)).getRequestSpecification();
    }

    public static SSLConfig useRelaxedHTTPSValidation() {
        return SerenityRest.useRelaxedHTTPSValidation("SSL");
    }

    public static SSLConfig useRelaxedHTTPSValidation(final String protocol) {
        RestAssured.useRelaxedHTTPSValidation(protocol);
        return config().getSSLConfig();
    }

    public static void registerParser(final String contentType, final Parser parser) {
        RestAssured.registerParser(contentType, parser);
    }

    public static void unregisterParser(final String contentType) {
        RestAssured.unregisterParser(contentType);
    }

    public static AuthenticationScheme oauth2(final String accessToken) {
        return RestAssured.oauth2(accessToken);
    }

    public static SSLConfig trustStore(final KeyStore truststore) {
        RestAssured.trustStore(truststore);
        return config().getSSLConfig();
    }

    public static AuthenticationScheme certificate(final String certURL, final String password) {
        return RestAssured.certificate(certURL, password);
    }

    public static LogConfig enableLoggingOfRequestAndResponseIfValidationFails() {
        return SerenityRest.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    public static LogConfig enableLoggingOfRequestAndResponseIfValidationFails(final LogDetail logDetail) {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(logDetail);
        return config().getLogConfig();
    }

    public static AuthenticationScheme certificate(final String certURL, final String password,
                                                   final CertificateAuthSettings certificateAuthSettings) {
        return RestAssured.certificate(certURL, password, certificateAuthSettings);
    }

    public static AuthenticationScheme form(final String userName, final String password) {
        return RestAssured.form(userName, password);
    }

    public static PreemptiveAuthProvider preemptive() {
        return RestAssured.preemptive();
    }

    public static AuthenticationScheme form(final String userName, final String password,
                                            final FormAuthConfig config) {
        return RestAssured.form(userName, password, config);
    }

    public static AuthenticationScheme oauth2(final String accessToken, final OAuthSignature signature) {
        return RestAssured.oauth2(accessToken, signature);
    }

    public static AuthenticationScheme basic(final String userName, final String password) {
        return RestAssured.basic(userName, password);
    }

    public static ProxySpecification proxy(final String host, final int port, final String scheme) {
        return setDefaultProxy(host, port, scheme);
    }

    public static ProxySpecification proxy(final String host, final int port) {
        return setDefaultProxy(host, port);
    }

    public static ProxySpecification proxy(final int port) {
        return setDefaultProxy(port);
    }

    public static ProxySpecification proxy(final URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Proxy URI cannot be null");
        }
        return SerenityRest.setDefaultProxy(new ProxySpecification(uri.getHost(), uri.getPort(), uri.getScheme()));
    }

    public static ProxySpecification proxy(final String host) {
        if (UriValidator.isUri(host)) {
            try {
                return setDefaultProxy(new URI(host));
            } catch (URISyntaxException e) {
                throw new RuntimeException("Internal error in REST Assured when constructing URI for Proxy.", e);
            }
        } else {
            return setDefaultProxy(host(host));
        }
    }

    public static ProxySpecification proxy(final ProxySpecification proxySpecification) {
        return SerenityRest.setDefaultProxy(proxySpecification);
    }

    public static ProxySpecification setDefaultProxy(final String host, final int port, final String scheme) {
        return setDefaultProxy(new ProxySpecification(host, port, scheme));
    }

    public static ProxySpecification setDefaultProxy(final ProxySpecification proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("ProxySpecification can not be null");
        }
        return RestAssured.proxy = proxy;
    }

    public static ProxySpecification setDefaultProxy(final String host, final int port) {
        return setDefaultProxy(ProxySpecification.host(host).withPort(port));
    }

    public static ProxySpecification setDefaultProxy(final int port) {
        return setDefaultProxy(ProxySpecification.port(port));
    }

    public static ProxySpecification setDefaultProxy(final URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Proxy URI cannot be null");
        }
        return setDefaultProxy(new ProxySpecification(uri.getHost(), uri.getPort(), uri.getScheme()));
    }

    public static ProxySpecification getDefaultProxy() {
        return RestAssured.proxy;
    }

    public static SSLConfig keystore(final File pathToJks, final String password) {
        RestAssured.keyStore(pathToJks, password);
        return config().getSSLConfig();
    }

    public static SSLConfig keystore(final String password) {
        RestAssured.keyStore(password);
        return config().getSSLConfig();
    }

    public static SSLConfig keystore(final String pathToJks, final String password) {
        RestAssured.keyStore(pathToJks, password);
        return config().getSSLConfig();
    }

    public static Response head(final URI uri) {
        return given().head(uri);
    }

    public static Response head() {
        return given().head();
    }

    public static Response head(final String path, final Object... pathParams) {
        return given().head(path, pathParams);
    }

    public static Response head(final String path, final Map<String, ?> pathParams) {
        return given().head(path, pathParams);
    }

    public static Response head(final URL url) {
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

    public static AuthenticationScheme oauth(final String consumerKey, final String consumerSecret,
                                             final String accessToken, final String secretToken,
                                             final OAuthSignature signature) {
        return RestAssured.oauth(consumerKey, consumerSecret, accessToken, secretToken, signature);
    }

    public static AuthenticationScheme oauth(final String consumerKey, final String consumerSecret,
                                             final String accessToken, final String secretToken) {
        return RestAssured.oauth(consumerKey, consumerSecret, accessToken, secretToken);
    }

    public static AuthenticationScheme digest(final String userName, final String password) {
        return RestAssured.digest(userName, password);
    }

    public static Response options() {
        return given().options();
    }

    public static Response options(final URL url) {
        return given().options(url);
    }

    public static Response options(final URI uri) {
        return given().options(uri);
    }

    public static Response options(final String path, final Object... pathParams) {
        if (pathParams != null && pathParams.length == 1 && pathParams[0] instanceof Map) {
            return given().options(path, pathParams[0]);
        } else {
            return given().options(path, pathParams);
        }
    }

    public static Response options(final String path, final Map<String, ?> pathParams) {
        return given().options(path, pathParams);
    }

    public static Response patch(final String path, final Map<String, ?> pathParams) {
        return given().patch(path, pathParams);
    }

    public static Response patch(final URI uri) {
        return given().patch(uri);
    }

    public static Response patch(final URL url) {
        return given().patch(url);
    }

    public static Response patch() {
        return given().patch();
    }

    public static Response patch(final String path, final Object... pathParams) {
        if (pathParams != null && pathParams.length == 1 && pathParams[0] instanceof Map) {
            return given().patch(path, pathParams[0]);
        } else {
            return given().patch(path, pathParams);
        }
    }

    public static Response post(final String path, final Object... pathParams) {
        if (pathParams != null && pathParams.length == 1 && pathParams[0] instanceof Map) {
            return given().post(path, pathParams[0]);
        } else {
            return given().post(path, pathParams);
        }
    }

    public static Response post(final String path, final Map<String, ?> pathParams) {
        return given().post(path, pathParams);
    }

    public static Response post(final URL url) {
        return given().post(url);
    }

    public static Response post() {
        return given().post();
    }

    public static Response post(final URI uri) {
        return given().post(uri);
    }

    public static Response put(final URI uri) {
        return given().put(uri);
    }

    public static Response put(final String path, final Object... pathParams) {
        if (pathParams != null && pathParams.length == 1 && pathParams[0] instanceof Map) {
            return given().put(path, pathParams[0]);
        } else {
            return given().put(path, pathParams);
        }
    }

    public static Response put() {
        return given().put();
    }

    public static Response put(final URL url) {
        return given().put(url);
    }

    public static Response delete(final String path, final Map<String, ?> pathParams) {
        return given().delete(path, pathParams);
    }

    public static Response delete(final URL url) {
        return given().delete(url);
    }

    public static Response delete(final URI uri) {
        return given().delete(uri);
    }

    public static Response delete() {
        return given().delete();
    }

    public static Response delete(final String path, final Object... pathParams) {
        if (pathParams != null && pathParams.length == 1 && pathParams[0] instanceof Map) {
            return given().delete(path, pathParams[0]);
        } else {
            return given().delete(path, pathParams);
        }
    }

    public static Response get(final URI uri) {
        return given().get(uri);
    }

    public static Response get(final URL url) {
        return given().get(url);
    }

    public static Response get(final String path, final Object... pathParams) {
        if (pathParams != null && pathParams.length == 1 && pathParams[0] instanceof Map) {
            return given().get(path, pathParams[0]);
        } else {
            return given().get(path, pathParams);
        }
    }

    public static Response get(final String path, final Map<String, ?> pathParams) {
        return given().get(path, pathParams);
    }

    public static Response get() {
        return given().get();
    }

    /**
     * A convenience method that allows you to check the last response received using a lambda expression,
     * e.g. restAssuredThat(lastResponse -> lastResponse.body(equalTo(expectedMessage)));
     */
    static public void restAssuredThat(Consumer<ValidatableResponse> expectedCondition) {
        expectedCondition.accept(SerenityRest.lastResponse().then());
    }
}
