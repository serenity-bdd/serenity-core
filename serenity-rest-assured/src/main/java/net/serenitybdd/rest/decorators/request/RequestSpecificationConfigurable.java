package net.serenitybdd.rest.decorators.request;

import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SessionConfig;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationConfigurable extends RequestSpecificationInitialisation
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationConfigurable.class);

    public RequestSpecificationConfigurable(final RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification config(final RestAssuredConfig config) {
        core.config(config);
        return this;
    }

    @Override
    public RestAssuredConfig getConfig() {
        return core.getConfig();
    }

    @Override
    public RequestSpecification baseUri(final String baseUri) {
        core.baseUri(baseUri);
        return this;
    }

    @Override
    public RequestSpecification basePath(final String basePath) {
        core.basePath(basePath);
        return this;
    }

    @Override
    public RequestSpecification sessionId(final String sessionIdValue) {
        final String sessionIdName;
        if (getConfig() == null) {
            sessionIdName = SessionConfig.DEFAULT_SESSION_ID_NAME;
        } else {
            sessionIdName = getConfig().getSessionConfig().sessionIdName();
        }
        return sessionId(sessionIdName, sessionIdValue);
    }

    @Override
    public RequestSpecification sessionId(final String sessionIdName, final String sessionIdValue) {
        core.sessionId(sessionIdName, sessionIdValue);
        return this;
    }

    @Override
    public RequestSpecification urlEncodingEnabled(final boolean isEnabled) {
        core.urlEncodingEnabled(isEnabled);
        return this;
    }

    @Override
    public RequestSpecification port(final int port) {
        core.port(port);
        return this;
    }

    @Override
    public RequestSpecification spec(final RequestSpecification requestSpecificationToMerge) {
        core.spec(requestSpecificationToMerge);
        return this;
    }

    @Override
    public RequestSpecification specification(final RequestSpecification requestSpecificationToMerge) {
        core.specification(requestSpecificationToMerge);
        return this;
    }
}