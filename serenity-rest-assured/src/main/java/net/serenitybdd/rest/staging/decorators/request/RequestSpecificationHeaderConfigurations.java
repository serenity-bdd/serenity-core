package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jayway.restassured.internal.assertion.AssertParameter.notNull;
import static net.serenitybdd.rest.staging.HeaderNames.*;

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
        return headers(createMapFromObjects(firstHeaderName, firstHeaderValue, headerNameValuePairs));
    }

    @Override
    public RequestSpecification headers(Headers headers) {
        notNull(headers, "headers");
        Map<String, String> converted = new HashMap<>();
        if (headers.exist()) {
            for (Header header : headers.asList()) {
                converted.put(header.getName(), header.getValue());
            }
        }
        return headers(converted);
    }

    @Override
    public RequestSpecification header(String headerName, Object headerValue, Object... additionalHeaderValues) {
        notNull(headerName, "Header name");
        notNull(headerValue, "Header value");

        Map<String, Object> prepared = new HashMap<>();
        prepared.put(headerName, headerValue);
        for (Object value : additionalHeaderValues) {
            prepared.put(headerName, value);
        }
        return headers(prepared);
    }

    @Override
    public RequestSpecification header(Header header) {
        Map<String, String> prepared = new HashMap<>();
        prepared.put(header.getName(), header.getValue());
        return headers(prepared);
    }

    @Override
    public RequestSpecification accept(ContentType contentType) {
        notNull(contentType, "Accept header");
        return accept(contentType.getAcceptHeader());
    }

    @Override
    public RequestSpecification accept(String mediaTypes) {
        notNull(mediaTypes, "Accept header media range");
        return header(ACCEPT.asString(), mediaTypes);
    }

    private Map<String, Object> createMapFromObjects(Object... parameters) {
        if (parameters == null || parameters.length < 2) {
            throw new IllegalArgumentException("You must supply at least one key and one value.");
        } else if (parameters.length % 2 != 0) {
            throw new IllegalArgumentException("You must supply the same number of keys as values.");
        }

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < parameters.length; i += 2) {
            map.put(String.valueOf(parameters[i]), parameters[i + 1]);
        }
        return map;
    }
}