package net.serenitybdd.rest.stubs;

import com.jayway.restassured.authentication.CertificateAuthSettings;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.authentication.OAuthSignature;
import com.jayway.restassured.specification.AuthenticationSpecification;
import com.jayway.restassured.specification.PreemptiveAuthSpec;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * Created by john on 23/07/2015.
 */
public class AuthenticationSpecificationStub implements AuthenticationSpecification {
    @Override
    public RequestSpecification basic(String userName, String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification digest(String userName, String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification form(String userName, String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification form(String userName, String password, FormAuthConfig config) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification certificate(String certURL, String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification certificate(String certURL, String password, CertificateAuthSettings certificateAuthSettings) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification certificate(String certURL, String password, String keystoreType, int port) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification oauth2(String accessToken) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification oauth2(String accessToken, OAuthSignature signature) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification oauth(String consumerKey, String consumerSecret, String accessToken, String secretToken) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification oauth(String consumerKey, String consumerSecret, String accessToken, String secretToken, OAuthSignature signature) {
        return new RequestSpecificationStub();
    }

    @Override
    public PreemptiveAuthSpec preemptive() {
        return new PreemptiveAuthSpecStub();
    }

    @Override
    public RequestSpecification none() {
        return new RequestSpecificationStub();
    }
}
