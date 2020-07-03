package net.serenitybdd.rest.decorators.request;

import io.restassured.authentication.AuthenticationScheme;
import io.restassured.config.RestAssuredConfig;
import io.restassured.internal.AuthenticationSpecificationImpl;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.AuthenticationSpecification;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
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
    public RequestSpecification keyStore(String pathToJks, String password) {
        core.keyStore(pathToJks, password);
        return this;
    }

    @Override
    public RequestSpecification keyStore(File pathToJks, String password) {
        core.keyStore(pathToJks, password);
        return this;
    }

    @Override
    public RequestSpecification trustStore(KeyStore trustStore) {
        core.trustStore(trustStore);
        return this;
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation() {
        String protocol = "SSL";
        try {
            protocol = (String)helper.getValueFrom("SSL");
        } catch(IllegalAccessException | NoSuchFieldException ex) {
            log.error("Error when getting value of SSL field from RequestSpecification");
            protocol = "SSL";
        }
        return relaxedHTTPSValidation(protocol);
    }

    @Override
    public RequestSpecification relaxedHTTPSValidation(String protocol) {
        core.relaxedHTTPSValidation(protocol);
        return this;
    }

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
