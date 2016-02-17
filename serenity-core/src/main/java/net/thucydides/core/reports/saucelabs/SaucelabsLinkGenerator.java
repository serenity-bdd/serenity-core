package net.thucydides.core.reports.saucelabs;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * Used to generate links to Saucelabs videos when the tests are executed on the Saucelabs servers.
 */
public class SaucelabsLinkGenerator implements LinkGenerator {

    private EnvironmentVariables environmentVariables;

    //no arg constructor for serialization
    public SaucelabsLinkGenerator() {
    	
    }
    
    @Inject
    public SaucelabsLinkGenerator(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String linkFor(TestOutcome testOutcome) {
        if (saucelabsIsNotConfigured()) {
            return null;
        }
        if (noSessionIdIsFoundFor(testOutcome)) {
            return null;
        }
        if (accessKeyAvailable()) {
            return noLoginLinkFor(testOutcome.getSessionId());
        } else {
            return simpleLinkFor(testOutcome.getSessionId());
        }
    }

    private boolean saucelabsIsNotConfigured() {
        return (ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables) == null);
    }

    private boolean noSessionIdIsFoundFor(TestOutcome testOutcome) {
        return testOutcome.getSessionId() == null;
    }

    private String simpleLinkFor(String jobId) {
        return "http://saucelabs.com/jobs/" + jobId;
    }

    private String noLoginLinkFor(String jobId) {
        String accessKey = ThucydidesSystemProperty.SAUCELABS_ACCESS_KEY.from(environmentVariables);
        String username = ThucydidesSystemProperty.SAUCELABS_USER_ID.from(environmentVariables);

        String authCode = generateHMACFor(username + ":" + accessKey, jobId);
        return "https://saucelabs.com/jobs/" + jobId + "?auth=" + authCode;
    }

    private String generateHMACFor(String secretKey, String message) {
        try {
            byte[] keyBytes = secretKey.getBytes();
            Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);
            return new String(Hex.encodeHex(mac.doFinal(message.getBytes())));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not generate HMAC for some reason", e);
        }
    }

    private boolean accessKeyAvailable() {
        return ThucydidesSystemProperty.SAUCELABS_ACCESS_KEY.from(environmentVariables) != null;
    }
}
