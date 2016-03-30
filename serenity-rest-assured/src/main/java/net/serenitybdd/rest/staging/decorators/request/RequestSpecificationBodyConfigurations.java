package net.serenitybdd.rest.staging.decorators.request;

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
abstract class RequestSpecificationBodyConfigurations extends RequestSpecificationHeaderConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationBodyConfigurations.class);

    public RequestSpecificationBodyConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public <T> T getBody() {
        return core.getBody();
    }

    @Override
    public RequestSpecification body(String body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(byte[] body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(File body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(InputStream body) {
        return core.body(body);
    }

    @Override
    public RequestSpecification body(Object object) {
        return core.body(object);
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapper mapper) {
        return core.body(object, mapper);
    }

    @Override
    public RequestSpecification body(Object object, ObjectMapperType mapperType) {
        return core.body(object, mapperType);
    }
}