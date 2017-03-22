package net.serenitybdd.rest.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationAdvancedConfiguration extends RequestSpecificationLoggable
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationAdvancedConfiguration.class);

    public RequestSpecificationAdvancedConfiguration(RequestSpecificationImpl core) {
        super(core);
    }

}