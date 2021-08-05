package net.serenitybdd.rest.decorators.request;

import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationParametersConfigurations extends RequestSpecificationCookieConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationParametersConfigurations.class);

    public RequestSpecificationParametersConfigurations(RequestSpecificationImpl core) {
        super(core);
    }


    @Override
    public RequestSpecification params(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        core.params(firstParameterName, firstParameterValue, parameterNameValuePairs);
        return this;
    }

    @Override
    public RequestSpecification params(Map<String, ?> parametersMap) {
        core.params(parametersMap);
        return this;
    }

    @Override
    public RequestSpecification param(String parameterName, Object... parameterValues) {
        core.param(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification param(String parameterName, Collection<?> parameterValues) {
        core.param(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification queryParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        core.queryParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
        return this;
    }

    @Override
    public RequestSpecification queryParams(Map<String, ?> parametersMap) {
        core.queryParams(parametersMap);
        return this;
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Object... parameterValues) {
        core.queryParam(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Collection<?> parameterValues) {
        core.queryParam(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification formParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        core.formParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
        return this;
    }

    @Override
    public RequestSpecification formParams(Map<String, ?> parametersMap) {
        core.formParams(parametersMap);
        return this;
    }

    @Override
    public RequestSpecification formParam(String parameterName, Object... parameterValues) {
        core.formParam(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification formParam(String parameterName, Collection<?> parameterValues) {
        core.formParam(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification pathParam(String parameterName, Object parameterValue) {
        core.pathParam(parameterName, parameterValue);
        return this;
    }

    @Override
    public RequestSpecification pathParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        core.pathParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
        return this;
    }

    @Override
    public RequestSpecification pathParams(Map<String, ?> parameterNameValuePairs) {
        core.pathParams(parameterNameValuePairs);
        return this;
    }
}
