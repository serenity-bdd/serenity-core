package net.serenitybdd.rest.decorators;

import com.jayway.restassured.specification.AuthenticationSpecification;
import com.jayway.restassured.specification.PreemptiveAuthSpec;
import com.jayway.restassured.specification.RequestLogSpecification;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 10:40 PM
 */
public class RestDecorator {
    private final ThreadLocal<RequestSpecification> instrumented;

    public RestDecorator(final ThreadLocal<RequestSpecification> instrumented) {
        this.instrumented = instrumented;
    }

    public AuthenticationSpecification decorate(final AuthenticationSpecification spec) {
        return new AuthenticationSpecificationWrapper(spec, this.instrumented, this);
    }

    public RequestLogSpecification decorate(final RequestLogSpecification spec) {
        return new RequestLogSpecificationWrapper(spec, this.instrumented, this);
    }

    public PreemptiveAuthSpec decorate(final PreemptiveAuthSpec spec) {
        return new PreemptiveAuthSpecWrapper(spec, this.instrumented, this);
    }
}