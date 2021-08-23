package net.serenitybdd.lambdatest;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class LambdaTestAuthToken {
    static String usingCredentialsFrom(EnvironmentVariables environmentVariables) {
        String username = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("lambdatest.user").orElse("");
        String accessKey = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("lambdatest.key").orElse("");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            String usernameAndAccessKey = username + ":" + accessKey;
            messageDigest.update(usernameAndAccessKey.getBytes(),0,usernameAndAccessKey.length());
            return new BigInteger(1,messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
