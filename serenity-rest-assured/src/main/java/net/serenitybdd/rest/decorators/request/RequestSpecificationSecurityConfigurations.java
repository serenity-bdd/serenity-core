package net.serenitybdd.rest.decorators.request;

import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.internal.AuthenticationSpecificationImpl;
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
        core.keystore(pathToJks, password);
        return this;
    }

    @Override
    public RequestSpecification keystore(File pathToJks, String password) {
        core.keystore(pathToJks, password);
        return this;
    }

    @Override
    public RequestSpecification trustStore(KeyStore trustStore) {
        core.trustStore(trustStore);
        return this;
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation() {
        return relaxedHTTPSValidation(RequestSpecificationImpl.SSL);
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation(String protocol) {
        core.relaxedHTTPSValidation(protocol);
        return this;
    }

    @Override
    public AuthenticationSpecification authentication() {
        return new AuthenticationSpecificationImpl(this);
    }

    @Override
    public AuthenticationSpecification auth() {
        return authentication();
    }

    /**
     * Method created ONLY for using in groovy (rest assured internals)
     */
    protected void setAuthenticationScheme(final AuthenticationScheme scheme) {
        try {
            this.helper.setValueTo("authenticationScheme", scheme);
        } catch (Exception e) {
            throw new IllegalStateException
                    ("Can not set authenticationScheme from request, SerenityRest can work incorrectly");
        }
    }

    /**
     * Method created ONLY for using in groovy (rest assured internals)
     */
    protected RestAssuredConfig setauthenticationScheme() {
        return getRestAssuredConfig();
    }
}