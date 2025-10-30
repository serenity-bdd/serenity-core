package net.serenitybdd.rest.decorators.request;

import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.FilterableRequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationProxyConfigurations extends RequestSpecificationFiltersConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationProxyConfigurations.class);

    public RequestSpecificationProxyConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

}
