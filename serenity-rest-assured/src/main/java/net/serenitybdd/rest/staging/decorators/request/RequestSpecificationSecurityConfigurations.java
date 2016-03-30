package net.serenitybdd.rest.staging.decorators.request;

import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.internal.RequestSpecificationImpl;
import com.jayway.restassured.specification.AuthenticationSpecification;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.KeyStore;

/**
 * User: YamStranger
 * Date: 3/16/16
 * Time: 2:08 PM
 */
abstract class RequestSpecificationSecurityConfigurations extends RequestSpecificationProxyConfigurations
        implements FilterableRequestSpecification {
    private static final Logger log = LoggerFactory.getLogger(RequestSpecificationSecurityConfigurations.class);

    public RequestSpecificationSecurityConfigurations(RequestSpecificationImpl core) {
        super(core);
    }

    @Override
    public RequestSpecification keystore(String pathToJks, String password) {
        return core.keystore(pathToJks, password);
    }

    @Override
    public RequestSpecification keystore(File pathToJks, String password) {
        return core.keystore(pathToJks, password);
    }

    @Override
    public RequestSpecification trustStore(KeyStore trustStore) {
        return core.trustStore(trustStore);
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation() {
        return core.relaxedHTTPSValidation();
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation(String protocol) {
        return core.relaxedHTTPSValidation(protocol);
    }

    @Override
    public AuthenticationSpecification authentication() {
        return core.authentication();
    }

    @Override
    public AuthenticationSpecification auth() {
        return core.auth();
    }

    @Override
    public AuthenticationScheme getAuthenticationScheme() {
        return core.getAuthenticationScheme();
    }
}