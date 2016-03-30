package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
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
abstract class RequestSpecificationHeaderConfigurations extends RequestSpecificationMultiPartConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationHeaderConfigurations.class);

    public RequestSpecificationHeaderConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification headers(String firstHeaderName, Object firstHeaderValue, Object... headerNameValuePairs) {
        return core.headers(firstHeaderName, firstHeaderValue, headerNameValuePairs);
    }

    @Override
    public RequestSpecification headers(Map<String, ?> headers) {
        return core.headers(headers);
    }

    @Override
    public RequestSpecification headers(Headers headers) {
        return core.headers(headers);
    }

    @Override
    public RequestSpecification header(String headerName, Object headerValue, Object... additionalHeaderValues) {
        return core.header(headerName, headerValue, additionalHeaderValues);
    }

    @Override
    public RequestSpecification header(Header header) {
        return core.header(header);
    }

    @Override
    public RequestSpecification accept(ContentType contentType) {
        return core.accept(contentType);
    }

    @Override
    public RequestSpecification accept(String mediaTypes) {
        return core.accept(mediaTypes);
    }

    @Override
    public Headers getHeaders() {
        return core.getHeaders();
    }
}