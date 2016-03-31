package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.ProxySpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

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

    @Override
    public RequestSpecification proxy(String host, int port) {
        return core.proxy(host, port);
    }

    @Override
    public RequestSpecification proxy(String host) {
        return core.proxy(host);
    }

    @Override
    public RequestSpecification proxy(int port) {
        return core.proxy(port);
    }

    @Override
    public RequestSpecification proxy(URI uri) {
        return core.proxy(uri);
    }

    @Override
    public RequestSpecification proxy(ProxySpecification proxySpecification) {
        return core.proxy(proxySpecification);
    }

    @Override
    public RequestSpecification proxy(String host, int port, String scheme) {
        return core.proxy(host, port, scheme);
    }

    @Override
    public ProxySpecification getProxySpecification() {
        return core.getProxySpecification();
    }
}