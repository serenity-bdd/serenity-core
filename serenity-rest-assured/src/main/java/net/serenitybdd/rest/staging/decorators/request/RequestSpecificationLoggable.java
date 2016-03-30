package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestLogSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationLoggable extends RequestSpecificationRedirectConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationLoggable.class);

    public RequestSpecificationLoggable(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestLogSpecification log() {
        return core.log();
    }
}