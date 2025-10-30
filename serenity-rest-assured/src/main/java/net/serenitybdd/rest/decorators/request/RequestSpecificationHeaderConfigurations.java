package net.serenitybdd.rest.decorators.request;

import com.google.common.base.Preconditions;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.internal.MapCreator;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static net.serenitybdd.rest.HeaderNames.ACCEPT;

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
    public Headers getHeaders() {
        return core.getHeaders();
    }

    @Override
    public RequestSpecification headers(Map<String, ?> headers) {
        core.headers(headers);
        return this;
    }

    @Override
    public RequestSpecification headers(String firstHeaderName, Object firstHeaderValue, Object... headerNameValuePairs) {
        return headers(MapCreator.createMapFromParams(MapCreator.CollisionStrategy.OVERWRITE, firstHeaderName, firstHeaderValue, headerNameValuePairs));
    }

    @Override
    public RequestSpecification headers(Headers headers) {
        Preconditions.checkNotNull(headers, "headers");
        Map<String, String> converted = new HashMap();
        if (headers.exist()) {
            for (Header header : headers.asList()) {
                converted.put(header.getName(), header.getValue());
            }
        }
        return headers(converted);
    }

    @Override
    public RequestSpecification header(String headerName, Object headerValue, Object... additionalHeaderValues) {
        Preconditions.checkNotNull(headerName, "Header name");
        Preconditions.checkNotNull(headerValue, "Header value");

        Map<String, Object> prepared = new HashMap();
        prepared.put(headerName, headerValue);
        for (Object value : additionalHeaderValues) {
            prepared.put(headerName, value);
        }
        return headers(prepared);
    }

    @Override
    public RequestSpecification header(Header header) {
        Map<String, String> prepared = new HashMap();
        prepared.put(header.getName(), header.getValue());
        return headers(prepared);
    }

    @Override
    public RequestSpecification accept(ContentType contentType) {
        Preconditions.checkNotNull(contentType, "Accept header");
        return accept(contentType.getAcceptHeader());
    }

    @Override
    public RequestSpecification accept(String mediaTypes) {
        Preconditions.checkNotNull(mediaTypes, "Accept header media range");
        return header(ACCEPT.asString(), mediaTypes);
    }
}
