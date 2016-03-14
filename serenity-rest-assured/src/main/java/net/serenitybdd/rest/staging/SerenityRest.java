package net.serenitybdd.rest.staging;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.*;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.specification.*;

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

    public static RestAssuredConfig getDefaultConfig() {
        return RestAssured.config;
    }

    public static RestAssuredConfig setDefaultConfig(final RestAssuredConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("RestAssuredConfig can not be null");
        }
        return RestAssured.config = config;
    }

    public static ProxySpecification getDefaultProxy() {
        return RestAssured.proxy;
    }

    public static ProxySpecification setDefaultProxy(final ProxySpecification proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("ProxySpecification can not be null");
        }
        return RestAssured.proxy = proxy;
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

    public static Parser setDefaultParser(Parser defaultParser) {
        return RestAssured.defaultParser = defaultParser;
    }

    public static ResponseSpecification getDefaultResponseSpecification() {
        return RestAssured.responseSpecification;
    }

    public static ResponseSpecification setDefaultResponseSpecification(ResponseSpecification responseSpecification) {
        return RestAssured.responseSpecification = responseSpecification;
    }

    public static void reset() {
        RestAssured.reset();
    }

    public static RequestSpecification given() {
        return RestAssured.given();
    }
}