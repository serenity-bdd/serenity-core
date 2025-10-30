package net.serenitybdd.rest;

import io.restassured.authentication.AuthenticationScheme;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.mapper.ObjectMapper;
import io.restassured.parsing.Parser;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;
import java.util.List;


/**
 * User: YamStranger
 * Date: 4/5/16
 * Time: 8:27 PM
 *
 * This class directly calls some methods from SerenityRest related only to
 * setting default values and parameters, can be used for better readability of code.
 * There is no difference in behaving between this class or SerenityRest.
 */
public class RestDefaults {
    public static String setDefaultBasePath(final String basePath) {
        return SerenityRest.setDefaultBasePath(basePath);
    }

    public static String getDefaultBasePath() {
        return SerenityRest.getDefaultBasePath();
    }

    public static int getDefaultPort() {
        return SerenityRest.getDefaultPort();
    }

    public static int setDefaultPort(final int port) {
        return SerenityRest.setDefaultPort(port);
    }

    public static boolean isUrlEncodingEnabled() {
        return SerenityRest.isUrlEncodingEnabled();
    }

    public static boolean setUrlEncodingEnabled(final boolean urlEncodingEnabled) {
        return SerenityRest.setUrlEncodingEnabled(urlEncodingEnabled);
    }

    public static String getDefaultRootPath() {
        return SerenityRest.getDefaultRootPath();
    }

    public static String setDefaultRootPath(final String rootPath) {
        return SerenityRest.setDefaultRootPath(rootPath);
    }

    public static String getDefaultSessionId() {
        return SerenityRest.getDefaultSessionId();
    }

    public static String setDefaultSessionId(final String sessionId) {
        return SerenityRest.setDefaultSessionId(sessionId);
    }

    public static AuthenticationScheme getDefaultAuthentication() {
        return SerenityRest.getDefaultAuthentication();
    }

    public static AuthenticationScheme setDefaultAuthentication(final AuthenticationScheme authentication) {
        return SerenityRest.setDefaultAuthentication(authentication);
    }

    public static RequestSpecification getDefaultRequestSpecification() {
        return SerenityRest.getDefaultRequestSpecification();
    }

    public static RequestSpecification setDefaultRequestSpecification(
            final RequestSpecification requestSpecification) {
        return SerenityRest.setDefaultRequestSpecification(requestSpecification);
    }

    public static Parser getDefaultParser() {
        return SerenityRest.getDefaultParser();
    }

    public static Parser setDefaultParser(final Parser defaultParser) {
        return SerenityRest.setDefaultParser(defaultParser);
    }

    public static ResponseSpecification getDefaultResponseSpecification() {
        return SerenityRest.getDefaultResponseSpecification();
    }

    public static ResponseSpecification setDefaultResponseSpecification(
            final ResponseSpecification responseSpecification) {
        return SerenityRest.setDefaultResponseSpecification(responseSpecification);
    }

    public static void reset() {
        SerenityRest.reset();
    }

    public static List<Filter> filters(final List<Filter> filters) {
        return SerenityRest.filters(filters);
    }

    public static List<Filter> filters(final Filter filter, final Filter... additionalFilters) {
        return SerenityRest.filters(filter, additionalFilters);
    }

    public static List<Filter> replaceFiltersWith(final List<Filter> filters) {
        return SerenityRest.replaceFiltersWith(filters);
    }

    public static List<Filter> replaceFiltersWith(final Filter filter, final Filter... additionalFilters) {
        return SerenityRest.replaceFiltersWith(filter, additionalFilters);
    }

    public static List<Filter> filters() {
        return SerenityRest.filters();
    }

    public static ObjectMapper objectMapper(final ObjectMapper objectMapper) {
        return SerenityRest.objectMapper(objectMapper);
    }

    public static SSLConfig useRelaxedHTTPSValidation() {
        return SerenityRest.useRelaxedHTTPSValidation();
    }

    public static SSLConfig useRelaxedHTTPSValidation(final String protocol) {
        return SerenityRest.useRelaxedHTTPSValidation(protocol);
    }

    public static void registerParser(final String contentType, final Parser parser) {
        SerenityRest.registerParser(contentType, parser);
    }

    public static void unregisterParser(final String contentType) {
        SerenityRest.unregisterParser(contentType);
    }

    public static SSLConfig trustStore(KeyStore truststore) {
        return SerenityRest.trustStore(truststore);
    }

    public static LogConfig enableLoggingOfRequestAndResponseIfValidationFails() {
        return SerenityRest.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static LogConfig enableLoggingOfRequestAndResponseIfValidationFails(final LogDetail logDetail) {
        return SerenityRest.enableLoggingOfRequestAndResponseIfValidationFails(logDetail);
    }

    public static ProxySpecification proxy(final String host, final int port, final String scheme) {
        return SerenityRest.proxy(host, port, scheme);
    }

    public static ProxySpecification proxy(final String host, final int port) {
        return SerenityRest.proxy(host, port);
    }

    public static ProxySpecification proxy(final int port) {
        return SerenityRest.proxy(port);
    }

    public static ProxySpecification proxy(final URI uri) {
        return SerenityRest.proxy(uri);
    }

    public static ProxySpecification proxy(final String host) {
        return SerenityRest.proxy(host);
    }

    public static ProxySpecification proxy(final ProxySpecification proxySpecification) {
        return SerenityRest.proxy(proxySpecification);
    }

    public static ProxySpecification setDefaultProxy(final String host, final int port, final String scheme) {
        return SerenityRest.setDefaultProxy(host, port, scheme);
    }

    public static ProxySpecification setDefaultProxy(final ProxySpecification proxy) {
        return SerenityRest.setDefaultProxy(proxy);
    }

    public static ProxySpecification setDefaultProxy(final String host, final int port) {
        return SerenityRest.setDefaultProxy(host, port);
    }

    public static ProxySpecification setDefaultProxy(final int port) {
        return SerenityRest.setDefaultProxy(port);
    }

    public static ProxySpecification setDefaultProxy(final URI uri) {
        return SerenityRest.setDefaultProxy(uri);
    }

    public static ProxySpecification getDefaultProxy() {
        return SerenityRest.getDefaultProxy();
    }

    public static SSLConfig keystore(final File pathToJks, final String password) {
        return SerenityRest.keystore(pathToJks, password);
    }

    public static SSLConfig keystore(final String password) {
        return SerenityRest.keystore(password);
    }

    public static SSLConfig keystore(final String pathToJks, final String password) {
        return SerenityRest.keystore(pathToJks, password);
    }

    public static RestAssuredConfig config() {
        return SerenityRest.config();
    }

    public static RestAssuredConfig getDefaultConfig() {
        return SerenityRest.getDefaultConfig();
    }

    public static RestAssuredConfig setDefaultConfig(final RestAssuredConfig config) {
        return SerenityRest.setDefaultConfig(config);
    }
}
