package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
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
    public Map<String, ?> getQueryParams() {
        return core.getQueryParams();
    }

    @Override
    public Map<String, ?> getRequestParams() {
        return core.getRequestParams();
    }

    @Override
    public Map<String, ?> getFormParams() {
        return core.getFormParams();
    }
}