package net.serenitybdd.rest.stubs;

import io.restassured.specification.RedirectSpecification;
import io.restassured.specification.RequestSpecification;

/**
 * Created by john on 23/07/2015.
 */
public class RedirectSpecificationStub implements RedirectSpecification {
    @Override
    public RequestSpecification max(final int maxNumberOfRedirect) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification follow(final boolean followRedirects) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification allowCircular(final boolean allowCircularRedirects) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification rejectRelative(final boolean rejectRelativeRedirects) {
        return new RequestSpecificationStub();
    }
}
