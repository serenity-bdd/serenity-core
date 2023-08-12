package net.serenitybdd.plugins.lambdatest;

import net.thucydides.model.util.EnvironmentVariables;

public class LambdaTestVideoLink {
    private final String PUBLIC_URL = "https://automation.lambdatest.com/public/video?testID=%s&auth=%s";

    private final EnvironmentVariables environmentVariables;

    private LambdaTestVideoLink(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static LambdaTestVideoLink forEnvironment(EnvironmentVariables environmentVariables) {
        return new LambdaTestVideoLink(environmentVariables);
    }

    public String videoUrlForSession(String sessionId) {
        String authToken = LambdaTestCredentials.from(environmentVariables).getAuthToken();
        return String.format(PUBLIC_URL, sessionId, authToken);
    }

}
