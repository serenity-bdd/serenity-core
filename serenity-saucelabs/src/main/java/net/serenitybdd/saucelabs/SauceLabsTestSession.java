package net.serenitybdd.saucelabs;

import net.serenitybdd.rest.SerenityRest;
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
    private static final String BROWSER_SESSION_URL = "https://%s:%s@api.%s.saucelabs.com/rest/v1/%s/jobs/%s";
    private static final String TEST_LINK_TEMPLATE = "https://%s.saucelabs.com/tests/%s";
    private final String sauceLabsUsername, sauceLabsKey, sessionId, datacenter;

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

    public String getTestLink() {
        if (sauceLabsKey != null) {
            return noLoginLink();
        } else {
            return String.format(TEST_LINK_TEMPLATE, datacenter, sessionId);
        }
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

    private String noLoginLink() {
        String authCode = generateHMACFor(sauceLabsUsername + ":" + sauceLabsKey, sessionId);
        return String.format(TEST_LINK_TEMPLATE, datacenter, sessionId) + "?auth=" + authCode;
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
