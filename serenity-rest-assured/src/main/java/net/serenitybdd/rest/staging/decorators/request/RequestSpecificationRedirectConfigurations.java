package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RedirectSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationRedirectConfigurations extends RequestSpecificationPathParametersConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationRedirectConfigurations.class);

    public RequestSpecificationRedirectConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RedirectSpecification redirects() {
        return core.redirects();
    }
}