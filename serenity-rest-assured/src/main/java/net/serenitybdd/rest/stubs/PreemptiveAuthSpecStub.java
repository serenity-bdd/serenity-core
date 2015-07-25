package net.serenitybdd.rest.stubs;

import com.jayway.restassured.specification.PreemptiveAuthSpec;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * Created by john on 23/07/2015.
 */
public class PreemptiveAuthSpecStub implements PreemptiveAuthSpec {
    @Override
    public RequestSpecification basic(String userName, String password) {
        return new RequestSpecificationStub();
    }
}
