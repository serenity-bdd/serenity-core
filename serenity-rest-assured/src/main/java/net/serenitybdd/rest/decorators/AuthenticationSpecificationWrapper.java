package net.serenitybdd.rest.decorators;

import com.jayway.restassured.authentication.CertificateAuthSettings;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.authentication.OAuthSignature;
import com.jayway.restassured.specification.AuthenticationSpecification;
import com.jayway.restassured.specification.PreemptiveAuthSpec;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * User: YamStranger
 * Date: 11/29/15
 * Time: 11:48 PM
 */
class AuthenticationSpecificationWrapper extends BaseWrapper<AuthenticationSpecification>
    implements AuthenticationSpecification {

    public AuthenticationSpecificationWrapper(final AuthenticationSpecification auth,
                                              final ThreadLocal<RequestSpecification> instrumented,
                                              final RestDecorator decorator) {
        super(auth, instrumented, decorator);
    }

    @Override
    public RequestSpecification basic(final String userName,
                                      final String password) {
        this.core.basic(userName, password);
        return this.specification.get();
    }

    @Override
    public RequestSpecification digest(final String userName,
                                       final String password) {
        this.core.digest(userName, password);
        return this.specification.get();

    }

    @Override
    public RequestSpecification certificate(final String certURL,
                                            final String password) {
        this.core.certificate(certURL, password);
        return this.specification.get();

    }

    @Override
    public RequestSpecification certificate(final String certURL,
                                            final String password,
                                            final CertificateAuthSettings settings) {
        this.core.certificate(certURL, password, settings);
        return this.specification.get();
    }

    @Override
    @Deprecated
    public RequestSpecification certificate(final String certURL,
                                            final String password,
                                            final String keystoreType, int port) {
        this.core.certificate(certURL, password, keystoreType, port);
        return this.specification.get();
    }

    @Override
    public RequestSpecification oauth(final String consumerKey,
                                      final String consumerSecret,
                                      final String accessToken,
                                      final String secretToken) {
        this.core.oauth(consumerKey, consumerSecret, accessToken, secretToken);
        return this.specification.get();
    }

    @Override
    public RequestSpecification oauth(final String consumerKey,
                                      final String consumerSecret,
                                      final String accessToken,
                                      final String secretToken,
                                      OAuthSignature signature) {
        this.core.oauth(consumerKey, consumerSecret, accessToken, secretToken,
            signature);
        return this.specification.get();
    }

    @Override
    public RequestSpecification oauth2(final String accessToken) {
        this.core.oauth2(accessToken);
        return this.specification.get();
    }

    @Override
    public RequestSpecification oauth2(final String accessToken,
                                       final OAuthSignature signature) {
        this.core.oauth2(accessToken, signature);
        return this.specification.get();
    }

    @Override
    public RequestSpecification none() {
        this.core.none();
        return this.specification.get();
    }

    @Override
    public PreemptiveAuthSpec preemptive() {
        return this.decorator.decorate(this.core.preemptive());
    }

    @Override
    public RequestSpecification form(final String userName,
                                     final String password) {
        this.core.form(userName, password);
        return this.specification.get();
    }

    @Override
    public RequestSpecification form(final String userName,
                                     final String password,
                                     final FormAuthConfig config) {
        this.core.form(userName, password, config);
        return this.specification.get();
    }
}