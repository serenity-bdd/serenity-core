package net.serenitybdd.rest.decorators.request;

import com.jayway.restassured.internal.MapCreator;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

import static com.jayway.restassured.internal.assertion.AssertParameter.notNull;

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
        notNull(firstParameterName, "firstParameterName");
        notNull(firstParameterValue, "firstParameterValue");
        return parameters(MapCreator.createMapFromParams(firstParameterName, firstParameterValue, parameterNameValuePairs));
    }

    @Override
    public RequestSpecification parameters(Map<String, ?> parametersMap) {
        core.parameters(parametersMap);
        return this;
    }

    @Override
    public RequestSpecification parameter(String parameterName, Object... parameterValues) {
        core.parameter(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification parameter(String parameterName, Collection<?> parameterValues) {
        core.parameter(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification params(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return parameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification params(Map<String, ?> parametersMap) {
        return parameters(parametersMap);
    }

    @Override
    public RequestSpecification param(String parameterName, Object... parameterValues) {
        return parameters(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification param(String parameterName, Collection<?> parameterValues) {
        return parameters(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification queryParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        notNull(firstParameterName, "firstParameterName");
        notNull(firstParameterValue, "firstParameterValue");
        return queryParameters(MapCreator.createMapFromParams(firstParameterName, firstParameterValue, parameterNameValuePairs));
    }

    @Override
    public RequestSpecification queryParameters(Map<String, ?> parametersMap) {
        core.queryParameters(parametersMap);
        return this;
    }

    @Override
    public RequestSpecification queryParameter(String parameterName, Object... parameterValues) {
        core.queryParameter(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification queryParameter(String parameterName, Collection<?> parameterValues) {
        core.queryParameter(parameterName, parameterValues);
        return this;

    }

    @Override
    public RequestSpecification queryParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return queryParameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification queryParams(Map<String, ?> parametersMap) {
        return queryParameters(parametersMap);
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Object... parameterValues) {
        return queryParameters(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification queryParam(String parameterName, Collection<?> parameterValues) {
        return queryParameters(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification formParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        notNull(firstParameterName, "firstParameterName");
        notNull(firstParameterValue, "firstParameterValue");
        return formParameters(MapCreator.createMapFromParams(firstParameterName, firstParameterValue, parameterNameValuePairs));
    }

    @Override
    public RequestSpecification formParameters(Map<String, ?> parametersMap) {
        core.formParameters(parametersMap);
        return this;
    }

    @Override
    public RequestSpecification formParameter(String parameterName, Object... parameterValues) {
        core.formParameter(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification formParameter(String parameterName, Collection<?> parameterValues) {
        core.formParameter(parameterName, parameterValues);
        return this;
    }

    @Override
    public RequestSpecification formParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return formParameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification formParams(Map<String, ?> parametersMap) {
        return formParameters(parametersMap);
    }

    @Override
    public RequestSpecification formParam(String parameterName, Object... parameterValues) {
        return formParameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification formParam(String parameterName, Collection<?> parameterValues) {
        return formParameter(parameterName, parameterValues);
    }

    @Override
    public RequestSpecification pathParameter(String parameterName, Object parameterValue) {
        core.pathParameter(parameterName, parameterValue);
        return this;
    }

    @Override
    public RequestSpecification pathParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        notNull(firstParameterName, "firstParameterName");
        notNull(firstParameterValue, "firstParameterValue");
        return pathParameters(MapCreator.createMapFromParams(firstParameterName, firstParameterValue, parameterNameValuePairs));
    }

    @Override
    public RequestSpecification pathParameters(Map<String, ?> parameterNameValuePairs) {
        core.pathParameters(parameterNameValuePairs);
        return this;
    }

    @Override
    public RequestSpecification pathParam(String parameterName, Object parameterValue) {
        return pathParameter(parameterName, parameterValue);
    }

    @Override
    public RequestSpecification pathParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return pathParameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification pathParams(Map<String, ?> parameterNameValuePairs) {
        return pathParameters(parameterNameValuePairs);
    }

    @Override
    public Map<String, ?> getPathParams() {
        return core.getPathParams();
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