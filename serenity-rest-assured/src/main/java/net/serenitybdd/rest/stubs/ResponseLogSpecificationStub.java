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
    public ResponseSpecification ifStatusCodeIsEqualTo(int statusCode) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifStatusCodeMatches(Matcher<Integer> matcher) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification body() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification body(boolean shouldPrettyPrint) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification all() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification all(boolean shouldPrettyPrint) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification everything() {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification everything(boolean shouldPrettyPrint) {
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
    public ResponseSpecification ifValidationFails(LogDetail logDetail) {
        return new ResponseSpecificationStub();
    }

    @Override
    public ResponseSpecification ifValidationFails(LogDetail logDetail, boolean shouldPrettyPrint) {
        return new ResponseSpecificationStub();
    }
}
