package net.serenitybdd.saucelabs;

import com.google.gson.Gson;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SauceLabsTestSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(SauceLabsTestSession.class);
    private static final String BROWSER_SESSION_URL = "https://%s:%s@api.%s.saucelabs.com/rest/v1/%s/jobs/%s";
    private final String sauceLabsUsername, sauceLabsKey, sessionId, datacenter;
    private final Gson gson = new Gson();

    public SauceLabsTestSession(String datacenter, String sauceLabsUsername, String sauceLabsKey, String sessionId) {
        this.datacenter = datacenter;
        this.sauceLabsUsername = sauceLabsUsername;
        this.sauceLabsKey = sauceLabsKey;
        this.sessionId = sessionId;
    }

    public void updateTestResultFor(TestOutcome testOutcome) {
        Map<Object, Object> requestBody = new HashMap<Object, Object>() {{
            put("passed", testPassed(testOutcome));
            put("error", testOutcome.getErrorMessage());
        }};

        SerenityRest.given()
                .body(requestBody)
                .put(getSessionUri());
    }

    public String getTestUrl() {
        return String.format("https://%s.saucelabs.com/tests/%s", datacenter, sessionId);
    }

    private boolean testPassed(TestOutcome outcome) {
        return latestResultOf(outcome) == TestResult.SUCCESS;
    }

    private URI getSessionUri() {
        URI uri = null;
        try {
            uri = new URI(String.format(BROWSER_SESSION_URL, sauceLabsUsername, sauceLabsKey, datacenter, sauceLabsUsername, sessionId));
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse SauceLabs API url.", e);
        }

        return uri;
    }

    private TestResult latestResultOf(TestOutcome outcome) {
        if (outcome.isDataDriven()) {
            return outcome.getLatestTopLevelTestStep().get().getResult();
        } else {
            return outcome.getResult();
        }
    }
}
