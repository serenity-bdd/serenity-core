package net.serenitybdd.plugins.saucelabs;

import io.restassured.RestAssured;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class SauceLabsTestSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(SauceLabsTestSession.class);
    private static final String REST_API_URL = "https://%s:%s@saucelabs.com/rest/v1/%s/jobs/%s";
    private static final String APP_TEST_LINK_TEMPLATE = "https://app.saucelabs.com/tests/%s";
    private final String sauceLabsUsername;
    private final String sauceLabsKey;
    private final String sessionId;

    public SauceLabsTestSession(String sauceLabsUsername, String sauceLabsKey, String sessionId) {
        this.sauceLabsUsername = sauceLabsUsername;
        this.sauceLabsKey = sauceLabsKey;
        this.sessionId = sessionId;
    }

    public void updateTestResultFor(TestOutcome testOutcome) {
        Map<Object, Object> requestBody = new HashMap<Object, Object>() {{
            put("passed", testPassed(testOutcome));
            put("error", testOutcome.getErrorMessage());
        }};

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .put(getRESTAPIUri());
    }

    public String getTestLink() {
        if ((sauceLabsKey != null) && (sessionId != null)) {
            return noLoginLink();
        } else {
            return String.format(APP_TEST_LINK_TEMPLATE, sessionId);
        }
    }

    private boolean testPassed(TestOutcome outcome) {
        return latestResultOf(outcome) == TestResult.SUCCESS;
    }

    private URI getRESTAPIUri() {
        URI uri = null;
        try {
            uri = new URI(String.format(REST_API_URL, sauceLabsUsername, sauceLabsKey, sauceLabsUsername, sessionId));
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

    private String noLoginLink() {
        String authCode = generateHMACFor(sauceLabsUsername + ":" + sauceLabsKey, sessionId);
        return String.format(APP_TEST_LINK_TEMPLATE, sessionId) + "?auth=" + authCode;
    }

    private String generateHMACFor(String secretKey, String message) {
        try {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);
            return new String(Hex.encodeHex(mac.doFinal(message.getBytes())));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not generate HMAC for some reason", e);
        }
    }
}
