package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.jayway.restassured.internal.assertion.AssertParameter.notNull;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationConfigurable extends RequestSpecificationInitialisation
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationConfigurable.class);

    public RequestSpecificationConfigurable(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public int getPort() {
        return core.getPort();
    }

    @Override
    public RequestSpecification config(RestAssuredConfig config) {
        return core.config(config);
    }

    @Override
    public RestAssuredConfig getConfig() {
        return core.getConfig();
    }

    @Override
    public RequestSpecification baseUri(String baseUri) {
        return core.baseUri(baseUri);
    }

    @Override
    public String getBaseUri() {
        return core.getBaseUri();
    }

    @Override
    public RequestSpecification basePath(String basePath) {
        return core.basePath(basePath);
    }

    @Override
    public String getBasePath() {
        return core.getBasePath();
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
    public List<Filter> getDefinedFilters() {
        return core.getDefinedFilters();
    }

    @Override
    public <T extends Filter> RequestSpecification noFiltersOfType(Class<T> filterType) {
        return core.noFiltersOfType(filterType);
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
}