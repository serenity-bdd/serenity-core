package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * The LambdaTest username and access key will be read from the LT_USERNAME and LT_ACCESS_KEY system properties,
 * or (if not defined there) from the lt.user an lt.key configuration properties
 */
class LambdaTestCredentials {

    private final EnvironmentVariables environmentVariables;

    public LambdaTestCredentials(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static LambdaTestCredentials from(EnvironmentVariables environmentVariables) {
        return new LambdaTestCredentials(environmentVariables);
    }

    public String getUser() {
        String ltUserDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("lt.user").orElse("");
        return Optional.ofNullable(environmentVariables.getValue("LT_USERNAME")).orElse(ltUserDefinedInSerenityConf);
    }

    public String getAccessKey() {
        String ltKeyDefinedInSerenityConf = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("lt.key").orElse("");
        return Optional.ofNullable(environmentVariables.getValue("LT_ACCESS_KEY")).orElse(ltKeyDefinedInSerenityConf);
    }

    public boolean areDefined() {
        return (!getUser().isEmpty() && !getAccessKey().isEmpty());
    }

    public String getAuthToken() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            String usernameAndAccessKey = getUser() + ":" + getAccessKey();
            messageDigest.update(usernameAndAccessKey.getBytes(), 0, usernameAndAccessKey.length());
            return new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
