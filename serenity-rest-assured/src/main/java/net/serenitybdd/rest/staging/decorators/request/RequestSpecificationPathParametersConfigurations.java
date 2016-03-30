package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationPathParametersConfigurations extends RequestSpecificationBodyConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationPathParametersConfigurations.class);

    public RequestSpecificationPathParametersConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification pathParameter(String parameterName, Object parameterValue) {
        return core.pathParameter(parameterName, parameterValue);
    }

    @Override
    public RequestSpecification pathParameters(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.pathParameters(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification pathParameters(Map<String, ?> parameterNameValuePairs) {
        return core.pathParameters(parameterNameValuePairs);
    }

    @Override
    public RequestSpecification pathParam(String parameterName, Object parameterValue) {
        return core.pathParam(parameterName, parameterValue);
    }

    @Override
    public RequestSpecification pathParams(String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
        return core.pathParams(firstParameterName, firstParameterValue, parameterNameValuePairs);
    }

    @Override
    public RequestSpecification pathParams(Map<String, ?> parameterNameValuePairs) {
        return core.pathParams(parameterNameValuePairs);
    }

    @Override
    public Map<String, ?> getPathParams() {
        return core.getPathParams();
    }
}