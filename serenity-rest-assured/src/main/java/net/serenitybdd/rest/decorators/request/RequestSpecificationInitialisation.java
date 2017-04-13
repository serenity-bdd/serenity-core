package net.serenitybdd.rest.decorators.request;

import io.restassured.config.RestAssuredConfig;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.internal.ResponseSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import net.serenitybdd.rest.decorators.ResponseDecorated;
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated;
import net.serenitybdd.rest.utils.ReflectionHelper;
import net.serenitybdd.rest.utils.RestReportingHelper;
import net.serenitybdd.rest.utils.RestSpecificationFactory;
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
    protected RestReportingHelper reporting;

    public RequestSpecificationInitialisation(RequestSpecificationImpl core) {
        this.core = core;
        this.helper = new ReflectionHelper<>(core);
        this.reporting = new RestReportingHelper();
    }

    public void setRestReportingHelper(final RestReportingHelper helper) {
        this.reporting = helper;
    }

    @Override
    public ResponseSpecification response() {
        return check(core.response());
    }

    @Override
    public RequestSpecification and() {
        return this;
    }

    @Override
    public RequestSpecification with() {
        return this;
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
        return this;
    }

    @Override
    public RequestSpecification given() {
        return this;
    }

    @Override
    public RequestSpecification that() {
        return this;
    }

    @Override
    public RequestSpecification request() {
        return this;
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
            return RestSpecificationFactory.getInstrumentedResponseSpecification((ResponseSpecificationImpl) specification);
        }
    }

    protected ResponseDecorated decorate(final Response response) {
        if (response instanceof ResponseDecorated) {
            return (ResponseDecorated)response;
        } else {
            return new ResponseDecorated(response);
        }
    }

    /**
     * Method created for using in tests
     */
    public RequestSpecificationImpl getCore() {
        return ((RequestSpecificationImpl) core);
    }

    /**
     * Method created for using ONLY in groovy (rest assured internals)
     */
    protected void setResponseSpecification(final ResponseSpecification specification) {
        getCore().setResponseSpecification(decorate(specification));
    }

    /**
     * Method created for using ONLY in groovy (rest assured internals)
     */
    protected void setresponseSpecification(final ResponseSpecification specification) {
        setResponseSpecification(specification);
    }

    /**
     * Method created for using ONLY in groovy (rest assured internals)
     */
    protected RestAssuredConfig getRestAssuredConfig() {
        return getCore().getConfig();
    }

    /**
     * Method created for using ONLY in groovy (rest assured internals)
     */
    protected RestAssuredConfig getrestAssuredConfig() {
        return getRestAssuredConfig();
    }
}
