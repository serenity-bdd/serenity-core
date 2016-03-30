package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationContentConfigurations extends RequestSpecificationParametersConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationContentConfigurations.class);

    public RequestSpecificationContentConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification content(String content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(byte[] content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(File content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(InputStream content) {
        return core.content(content);
    }

    @Override
    public RequestSpecification content(Object object) {
        return core.content(object);
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapperType mapperType) {
        return core.content(object, mapperType);
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapper mapper) {
        return core.content(object, mapper);
    }

    @Override
    public RequestSpecification contentType(ContentType contentType) {
        return core.contentType(contentType);
    }

    @Override
    public RequestSpecification contentType(String contentType) {
        return core.contentType(contentType);
    }

    @Override
    public String getRequestContentType() {
        return core.getRequestContentType();
    }
}