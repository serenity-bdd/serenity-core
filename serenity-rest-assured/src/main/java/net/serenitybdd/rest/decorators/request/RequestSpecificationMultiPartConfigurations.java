package net.serenitybdd.rest.decorators.request;

import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import static io.restassured.http.ContentType.ANY;
import static io.restassured.http.ContentType.JSON;

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
    public List<MultiPartSpecification> getMultiPartParams() {
        return core.getMultiPartParams();
    }

    @Override
    public RequestSpecification multiPart(MultiPartSpecification multiPartSpecification) {
        core.multiPart(multiPartSpecification);
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file, String mimeType) {
        core.multiPart(controlName, file, mimeType);
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, Object object, String mimeType) {
        core.multiPart(controlName, object, mimeType);
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes, String mimeType) {
        core.multiPart(controlName, fileName, bytes, mimeType);
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream, String mimeType) {
        core.multiPart(controlName, fileName, stream, mimeType);
        return this;
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody, String mimeType) {
        core.multiPart(controlName, contentBody, mimeType);
        return this;
    }

    @Override
    public RequestSpecification multiPart(File file) {
        return multiPart("file", file);
    }

    @Override
    public RequestSpecification multiPart(String controlName, File file) {
        return multiPart(controlName, file, (String) null);
    }


    @Override
    public RequestSpecification multiPart(String controlName, Object object) {
        return multiPart(controlName, object,
                ANY.toString().equals(getContentType()) ? JSON.toString() : getContentType());
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, byte[] bytes) {
        return multiPart(controlName, fileName, bytes, null);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String fileName, InputStream stream) {
        return multiPart(controlName, fileName, stream, null);
    }

    @Override
    public RequestSpecification multiPart(String controlName, String contentBody) {
        return multiPart(controlName, contentBody, (String) null);
    }
}
