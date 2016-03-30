package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.internal.ResponseSpecificationImpl;
import com.jayway.restassured.response.*;
import com.jayway.restassured.specification.*;
import net.serenitybdd.rest.staging.decorators.ReflectionHelper;
import net.serenitybdd.rest.staging.decorators.ResponseDecorated;
import net.serenitybdd.rest.staging.decorators.ResponseSpecificationDecorated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationInitialisation implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationInitialisation.class);
    protected final RequestSpecificationImpl core;
    protected final ReflectionHelper<RequestSpecificationImpl> helper;

    public RequestSpecificationInitialisation(RequestSpecificationImpl core) {
        this.core = core;
        this.helper = new ReflectionHelper<>(core);
    }

    @Override
    public ResponseSpecification response() {
        return check(core.response());
    }

    @Override
    public RequestSpecification and() {
        return core.and();
    }

    @Override
    public RequestSpecification with() {
        return core.with();
    }

    @Override
    public ResponseSpecification then() {
        return check(core.then());
    }

    @Override
    public ResponseSpecification expect() {
        return check(core.expect());
    }

    @Override
    public RequestSpecification when() {
        return core.when();
    }

    @Override
    public RequestSpecification given() {
        return core.given();
    }

    @Override
    public RequestSpecification that() {
        return core.that();
    }

    @Override
    public RequestSpecification request() {
        return core.request();
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return core.equals(obj);
    }

    protected ResponseSpecification check(final ResponseSpecification specification) {
        if (specification instanceof ResponseSpecificationDecorated) {
            return specification;
        } else {
            log.warn("returned not decorated response, SerenityRest can work incorrectly");
            return specification;
        }
    }

    protected ResponseSpecification decorate(final ResponseSpecification specification) {
        if (specification instanceof ResponseSpecificationDecorated) {
            return specification;
        } else {
            return new ResponseSpecificationDecorated((ResponseSpecificationImpl) specification);
        }
    }

    protected Response decorate(final Response response) {
        if (response instanceof ResponseDecorated) {
            return response;
        } else {
            return new ResponseDecorated(response);
        }
    }

    public RequestSpecificationImpl getCore() {
        return ((RequestSpecificationImpl) core);
    }

    public void setResponseSpecification(final ResponseSpecification specification) {
        getCore().setResponseSpecification(decorate(specification));
    }

    public void setresponseSpecification(final ResponseSpecification specification) {
        setResponseSpecification(specification);
    }
}