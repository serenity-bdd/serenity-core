package net.serenitybdd.rest.stubs;

import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.specification.ResponseLogSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.hamcrest.Matcher;

/**
 * Created by john on 23/07/2015.
 */
public class ResponseLogSpecificationStub implements ResponseLogSpecification {
    @Override
    public ResponseSpecification status() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifError() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifStatusCodeIsEqualTo(final int statusCode) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifStatusCodeMatches(final Matcher<Integer> matcher) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification body() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification body(final boolean shouldPrettyPrint) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification all() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification all(final boolean shouldPrettyPrint) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification everything() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification everything(final boolean shouldPrettyPrint) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification headers() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification cookies() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifValidationFails() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifValidationFails(final LogDetail logDetail) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifValidationFails(final LogDetail logDetail, boolean shouldPrettyPrint) {
        return new ResponseSpecificationStub();
    }
}
