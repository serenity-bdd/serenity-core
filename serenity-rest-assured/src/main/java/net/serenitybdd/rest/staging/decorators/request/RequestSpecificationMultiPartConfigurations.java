package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.MultiPartSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationMultiPartConfigurations extends RequestSpecificationParametersConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationMultiPartConfigurations.class);

    public RequestSpecificationMultiPartConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification multiPart(MultiPartSpecification multiPartSpecification) {
        return core.multiPart(multiPartSpecification);
    }

    @Override
    public RequestSpecification multiPart(File file) {
        return core.multiPart(file);
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file) {
        return core.multiPart(controlName, file);
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file, String mimeType) {
        return core.multiPart(controlName, file, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, Object object) {
        return core.multiPart(controlName, object);
    }

    @Override
    public RequestSpecification multiPart(String controlName, Object object, String mimeType) {
        return core.multiPart(controlName, object, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes) {
        return core.multiPart(controlName, fileName, bytes);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes, String mimeType) {
        return core.multiPart(controlName, fileName, bytes, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream) {
        return core.multiPart(controlName, fileName, stream);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream, String mimeType) {
        return core.multiPart(controlName, fileName, stream, mimeType);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody) {
        return core.multiPart(controlName, contentBody);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody, String mimeType) {
        return core.multiPart(controlName, contentBody, mimeType);
    }

    @Override
    public List<MultiPartSpecification> getMultiPartParams() {
        return core.getMultiPartParams();
    }
}