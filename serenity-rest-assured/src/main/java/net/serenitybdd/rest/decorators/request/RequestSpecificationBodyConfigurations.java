package net.serenitybdd.rest.decorators.request;

import com.google.common.base.Preconditions;
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

import static net.serenitybdd.rest.HeaderNames.*;


abstract class RequestSpecificationBodyConfigurations extends RequestSpecificationHeaderConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationBodyConfigurations.class);

    public RequestSpecificationBodyConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification body(Object object) {
        core.body(object);
        return this;
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapper mapper) {
        if (object instanceof byte[]) {
            core.body((byte[]) object);
        } else {
            core.body(object, mapper);
        }
        return this;
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapperType mapperType) {
        if (object instanceof byte[]) {
            return body((byte[]) object);
        }

        core.body(object, mapperType);
        return this;
    }

    @Override
    public RequestSpecification body(String body) {
        core.body(body);
        return this;
    }

    @Override
    public RequestSpecification body(byte[] body) {
        core.body(body);
        return this;
    }

    @Override
    public RequestSpecification body(File body) {
        core.body(body);
        return this;
    }

    @Override
    public RequestSpecification body(InputStream body) {
        core.body(body);
        return this;
    }

    @Override
    public RequestSpecification content(String content) {
        core.content(content);
        return this;
    }

    @Override
    public RequestSpecification content(byte[] content) {
        core.content(content);
        return this;
    }

    @Override
    public RequestSpecification content(File content) {
        core.content(content);
        return this;
    }

    @Override
    public RequestSpecification content(InputStream content) {
        core.content(content);
        return this;
    }

    @Override
    public RequestSpecification content(Object object) {
        core.content(object);
        return this;
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapperType mapperType) {
        return body(object, mapperType);
    }

    @Override
    public RequestSpecification content(Object object, ObjectMapper mapper) {
        return body(object, mapper);
    }

    @Override
    public RequestSpecification contentType(ContentType contentType) {
        Preconditions.checkNotNull(contentType, ContentType.class);
        return header(CONTENT_TYPE.asString(), contentType);
    }

    @Override
    public RequestSpecification contentType(String contentType) {
        Preconditions.checkNotNull(contentType, "Content-Type header cannot be null");
        return header(CONTENT_TYPE.asString(), contentType);
    }
}