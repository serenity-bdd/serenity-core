package net.serenitybdd.rest.staging;

import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.specification.ProxySpecification;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;
import java.util.List;


/**
 * User: YamStranger
 * Date: 4/5/16
 * Time: 8:27 PM
 * <p/>
 * This class directly calls some methods from SerenityRest related only to
 * setting default values and parameters, can be used for better readability of code using
 * method chaining, also known as named parameter idiom.
 * So now it is possible to configure serenity rest using line:
 * new RestDefaultsChained().setDefaultBasePath("some/path").setDefaultProxy(object).setDefaultPort(10)
 * There is no difference in behaving between this class or SerenityRest.
 */


public class RestDefaultsChained {
    public RestDefaultsChained setDefaultBasePath(final String basePath) {
        SerenityRest.setDefaultBasePath(basePath);
        return this;
    }

    public RestDefaultsChained setDefaultPort(final int port) {
        SerenityRest.setDefaultPort(port);
        return this;
    }


    public RestDefaultsChained setUrlEncodingEnabled(final boolean urlEncodingEnabled) {
        SerenityRest.setUrlEncodingEnabled(urlEncodingEnabled);
        return this;
    }

    public RestDefaultsChained setDefaultRootPath(final String rootPath) {
        SerenityRest.setDefaultRootPath(rootPath);
        return this;
    }

    public RestDefaultsChained setDefaultSessionId(final String sessionId) {
        SerenityRest.setDefaultSessionId(sessionId);
        return this;
    }


    public RestDefaultsChained setDefaultAuthentication(final AuthenticationScheme authentication) {
        SerenityRest.setDefaultAuthentication(authentication);
        return this;
    }

    public RestDefaultsChained setDefaultRequestSpecification(
            final RequestSpecification requestSpecification) {
        SerenityRest.setDefaultRequestSpecification(requestSpecification);
        return this;
    }

    public RestDefaultsChained setDefaultParser(final Parser defaultParser) {
        SerenityRest.setDefaultParser(defaultParser);
        return this;
    }


    public RestDefaultsChained setDefaultResponseSpecification(
            final ResponseSpecification responseSpecification) {
        SerenityRest.setDefaultResponseSpecification(responseSpecification);
        return this;
    }

    public RestDefaultsChained reset() {
        SerenityRest.reset();
        return this;
    }

    public RestDefaultsChained filters(final List<Filter> filters) {
        SerenityRest.filters(filters);
        return this;
    }

    public RestDefaultsChained filters(final Filter filter, final Filter... additionalFilters) {
        SerenityRest.filters(filter, additionalFilters);
        return this;
    }

    public RestDefaultsChained RestDefaultsChainedreplaceFiltersWith(final List<Filter> filters) {
        SerenityRest.replaceFiltersWith(filters);
        return this;
    }

    public RestDefaultsChained replaceFiltersWith(final Filter filter, final Filter... additionalFilters) {
        SerenityRest.replaceFiltersWith(filter, additionalFilters);
        return this;
    }

    public RestDefaultsChained objectMapper(final ObjectMapper objectMapper) {
        SerenityRest.objectMapper(objectMapper);
        return this;
    }

    public RestDefaultsChained useRelaxedHTTPSValidation() {
        SerenityRest.useRelaxedHTTPSValidation();
        return this;
    }

    public RestDefaultsChained useRelaxedHTTPSValidation(final String protocol) {
        SerenityRest.useRelaxedHTTPSValidation(protocol);
        return this;
    }

    public RestDefaultsChained registerParser(final String contentType, final Parser parser) {
        SerenityRest.registerParser(contentType, parser);
        return this;
    }

    public RestDefaultsChained unregisterParser(final String contentType) {
        SerenityRest.unregisterParser(contentType);
        return this;
    }

    public RestDefaultsChained trustStore(KeyStore truststore) {
        SerenityRest.trustStore(truststore);
        return this;
    }

    public RestDefaultsChained enableLoggingOfRequestAndResponseIfValidationFails() {
        SerenityRest.enableLoggingOfRequestAndResponseIfValidationFails();
        return this;
    }

    public RestDefaultsChained enableLoggingOfRequestAndResponseIfValidationFails(final LogDetail logDetail) {
        SerenityRest.enableLoggingOfRequestAndResponseIfValidationFails(logDetail);
        return this;
    }

    public RestDefaultsChained proxy(final String host, final int port, final String scheme) {
        SerenityRest.proxy(host, port, scheme);
        return this;
    }

    public RestDefaultsChained proxy(final String host, final int port) {
        SerenityRest.proxy(host, port);
        return this;
    }

    public RestDefaultsChained proxy(final int port) {
        SerenityRest.proxy(port);
        return this;
    }

    public RestDefaultsChained proxy(final URI uri) {
        SerenityRest.proxy(uri);
        return this;
    }

    public RestDefaultsChained proxy(final String host) {
        SerenityRest.proxy(host);
        return this;
    }

    public RestDefaultsChained proxy(final ProxySpecification proxySpecification) {
        SerenityRest.proxy(proxySpecification);
        return this;
    }

    public RestDefaultsChained setDefaultProxy(final String host, final int port, final String scheme) {
        SerenityRest.setDefaultProxy(host, port, scheme);
        return this;
    }

    public RestDefaultsChained setDefaultProxy(final ProxySpecification proxy) {
        SerenityRest.setDefaultProxy(proxy);
        return this;
    }

    public RestDefaultsChained setDefaultProxy(final String host, final int port) {
        SerenityRest.setDefaultProxy(host, port);
        return this;
    }

    public RestDefaultsChained setDefaultProxy(final int port) {
        SerenityRest.setDefaultProxy(port);
        return this;
    }

    public RestDefaultsChained setDefaultProxy(final URI uri) {
        SerenityRest.setDefaultProxy(uri);
        return this;
    }

    public RestDefaultsChained keystore(final File pathToJks, final String password) {
        SerenityRest.keystore(pathToJks, password);
        return this;
    }

    public RestDefaultsChained keystore(final String password) {
        SerenityRest.keystore(password);
        return this;
    }

    public RestDefaultsChained keystore(final String pathToJks, final String password) {
        SerenityRest.keystore(pathToJks, password);
        return this;
    }

    public RestDefaultsChained setDefaultConfig(final RestAssuredConfig config) {
        SerenityRest.setDefaultConfig(config);
        return this;
    }
}
