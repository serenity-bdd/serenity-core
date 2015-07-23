package net.serenitybdd.rest.stubs;

import com.jayway.restassured.specification.RedirectSpecification;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * Created by john on 23/07/2015.
 */
public class RedirectSpecificationStub implements RedirectSpecification {
    @Override
    public RequestSpecification max(int maxNumberOfRedirect) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification follow(boolean followRedirects) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification allowCircular(boolean allowCircularRedirects) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification rejectRelative(boolean rejectRelativeRedirects) {
        return new RequestSpecificationStub();
    }
}
