package net.serenitybdd.rest;

import io.restassured.authentication.*;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.Argument;

import java.util.List;


/**
 * User: YamStranger
 * Date: 4/5/16
 * Time: 8:27 PM
 * This class directly calls some methods from SerenityRest related only to
 * creating some predefined objects like arguments, configs, auth schemes and so on,
 * can be used for better readability of code.
 * There is no difference what to use this class or SerenityRest.
 */
public class RestUtility {
    public static List<Argument> withArguments(final Object firstArgument, final Object... additionalArguments) {
        return SerenityRest.withArguments(firstArgument, additionalArguments);
    }

    public static List<Argument> withNoArguments() {
        return SerenityRest.withNoArguments();
    }

    public static List<Argument> withArgs(final Object firstArgument, final Object... additionalArguments) {
        return SerenityRest.withArgs(firstArgument, additionalArguments);
    }

    public static List<Argument> withNoArgs() {
        return SerenityRest.withNoArgs();
    }

    public static AuthenticationScheme oauth2(final String accessToken) {
        return SerenityRest.oauth2(accessToken);
    }

    public static AuthenticationScheme certificate(final String certURL, final String password) {
        return SerenityRest.certificate(certURL, password);
    }

    public static AuthenticationScheme certificate(final String certURL, final String password, final CertificateAuthSettings certificateAuthSettings) {
        return SerenityRest.certificate(certURL, password, certificateAuthSettings);
    }

    public static AuthenticationScheme form(final String userName, final String password) {
        return SerenityRest.form(userName, password);
    }

    public static AuthenticationScheme form(final String userName, final String password, final FormAuthConfig config) {
        return SerenityRest.form(userName, password, config);
    }

    public static PreemptiveAuthProvider preemptive() {
        return SerenityRest.preemptive();
    }

    public static AuthenticationScheme oauth2(final String accessToken, final OAuthSignature signature) {
        return SerenityRest.oauth2(accessToken, signature);
    }

    public static AuthenticationScheme basic(final String userName, final String password) {
        return SerenityRest.basic(userName, password);
    }

    public static RestAssuredConfig newConfig() {
        return new RestAssuredConfig();
    }

    public static AuthenticationScheme oauth(final String consumerKey, final String consumerSecret, final String accessToken, final String secretToken, final OAuthSignature signature) {
        return SerenityRest.oauth(consumerKey, consumerSecret, accessToken, secretToken, signature);
    }

    public static AuthenticationScheme oauth(final String consumerKey, final String consumerSecret, final String accessToken, final String secretToken) {
        return SerenityRest.oauth(consumerKey, consumerSecret, accessToken, secretToken);
    }

    public static AuthenticationScheme digest(final String userName, final String password) {
        return SerenityRest.digest(userName, password);
    }
}
