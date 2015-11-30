package net.serenitybdd.rest.decorators;

import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.specification.RequestLogSpecification;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 10:41 PM
 */
class RequestLogSpecificationWrapper extends BaseWrapper<RequestLogSpecification>
    implements RequestLogSpecification {

    RequestLogSpecificationWrapper(final RequestLogSpecification log,
                                   final ThreadLocal<RequestSpecification> instrumented,
                                   final RestDecorator decorator) {
        super(log, instrumented, decorator);
    }

    @Override
    public RequestSpecification params() {
        this.core.params();
        return this.specification.get();
    }

    @Override
    public RequestSpecification parameters() {
        this.core.parameters();
        return this.specification.get();
    }

    @Override
    public RequestSpecification path() {
        this.core.path();
        return this.specification.get();
    }

    @Override
    public RequestSpecification method() {
        this.core.method();
        return this.specification.get();
    }

    @Override
    public RequestSpecification body() {
        this.core.body();
        return this.specification.get();
    }

    @Override
    public RequestSpecification body(boolean shouldPrettyPrint) {
        this.core.body(shouldPrettyPrint);
        return this.specification.get();
    }

    @Override
    public RequestSpecification all(boolean shouldPrettyPrint) {
        this.core.all(shouldPrettyPrint);
        return this.specification.get();
    }

    @Override
    public RequestSpecification everything(boolean shouldPrettyPrint) {
        this.core.everything(shouldPrettyPrint);
        return this.specification.get();
    }

    @Override
    public RequestSpecification all() {
        this.core.all();
        return this.specification.get();
    }

    @Override
    public RequestSpecification everything() {
        this.core.everything();
        return this.specification.get();
    }

    @Override
    public RequestSpecification headers() {
        this.core.headers();
        return this.specification.get();
    }

    @Override
    public RequestSpecification cookies() {
        this.core.cookies();
        return this.specification.get();
    }

    @Override
    public RequestSpecification ifValidationFails() {
        this.core.ifValidationFails();
        return this.specification.get();
    }

    @Override
    public RequestSpecification ifValidationFails(final LogDetail logDetail) {
        this.core.ifValidationFails(logDetail);
        return this.specification.get();
    }

    @Override
    public RequestSpecification ifValidationFails(final LogDetail logDetail,
                                                  boolean shouldPrettyPrint) {
        this.core.ifValidationFails(logDetail, shouldPrettyPrint);
        return this.specification.get();
    }
}