package net.serenitybdd.rest.stubs;

import io.restassured.authentication.CertificateAuthSettings;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.authentication.OAuthSignature;
import io.restassured.specification.AuthenticationSpecification;
import io.restassured.specification.PreemptiveAuthSpec;
import io.restassured.specification.RequestSpecification;

/**
 * Created by john on 23/07/2015.
 */
public class AuthenticationSpecificationStub implements AuthenticationSpecification {
    @Override
    public RequestSpecification basic(final String userName, final String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification ntlm(String userName, String password, String workstation, String domain) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification digest(final String userName, final String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification form(final String userName, final String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification form(final String userName, final String password, final FormAuthConfig config) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification certificate(final String certURL, final String password) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification certificate(final String certURL, final String password,
                                            final CertificateAuthSettings certificateAuthSettings) {
        return new RequestSpecificationStub();
    }

    /*
    @Override
    public RequestSpecification certificate(final String certURL, final String password, final String keystoreType,
                                            final int port) {
        return new RequestSpecificationStub();
    }
*/

    @Override
    public RequestSpecification oauth2(final String accessToken) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification oauth2(final String accessToken, final OAuthSignature signature) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification oauth(final String consumerKey, final String consumerSecret,
                                      final String accessToken, final String secretToken) {
        return new RequestSpecificationStub();
    }

    @Override
    public RequestSpecification oauth(final String consumerKey, final String consumerSecret,
                                      final String accessToken, final String secretToken,
                                      final OAuthSignature signature) {
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
