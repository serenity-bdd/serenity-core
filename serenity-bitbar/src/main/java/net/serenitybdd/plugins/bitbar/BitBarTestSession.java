package net.serenitybdd.plugins.bitbar;

import com.testdroid.api.APIEntity;
import com.testdroid.api.model.APIDeviceSession;
import net.serenitybdd.model.model.TestOutcomeName;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class BitBarTestSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitBarTestSession.class);
    private static final String WD_HUB = "wd/hub";
    private static final String SESSION_PATH = "sessions/%s";
    private final String webdriverRemoteUrl;
    private final String cloudUrl;
    private final String bitbarApiKey;
    private final String sessionId;

    public BitBarTestSession(String cloudUrl, String webdriverRemoteUrl, String bitbarApiKey, String sessionId) {
        this.cloudUrl = cloudUrl;
        this.webdriverRemoteUrl = webdriverRemoteUrl;
        this.bitbarApiKey = bitbarApiKey;
        this.sessionId = sessionId;
    }

    public void updateTestResultFor(TestOutcome testOutcome) {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPut putRequest = new HttpPut(getSessionUri());

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("name", TestOutcomeName.from(testOutcome)));
            nameValuePairs.add(new BasicNameValuePair("state", bitbarCompatibleResultOf(testOutcome)));
            putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));

            byte[] encodedAuth = Base64.encodeBase64((bitbarApiKey + ":").getBytes());
            putRequest.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(encodedAuth)));
            httpClient.execute(putRequest);

        } catch (IOException e) {
            LOGGER.error("Failed to update BitBar results", e);
        }

    }

    public String getPublicUrl() {
        APIDeviceSession deviceSession = getSession();
        // TODO get uiLink from the session, when it's available (API >= 3.7)
        return Optional.ofNullable(deviceSession).map(ds ->
                String.format("%s/#testing/device-session/%s/%s/%s",
                        cloudUrl, deviceSession.getProjectId(), deviceSession.getTestRunId(), deviceSession.getId())
        ).orElse("");
    }

    private APIDeviceSession getSession() {
        if (bitbarApiKey == null) {
            return null;
        }
        try {
            HttpGet querySessionInfo = new HttpGet(getSessionUri());
            HttpEntity sessionDetails = HttpClientBuilder.create().build().execute(querySessionInfo).getEntity();
            String sessionBody = EntityUtils.toString(sessionDetails, charsetOf(sessionDetails));
            return APIEntity.OBJECT_MAPPER.readValue(sessionBody, APIDeviceSession.class);
        } catch (IOException e) {
            LOGGER.error("Failed to connect to BitBar API.", e);
            return null;
        }
    }

    private Charset charsetOf(HttpEntity entity) {
        Header encodingHeader = entity.getContentEncoding();
        return encodingHeader == null ? StandardCharsets.UTF_8 : Charsets.toCharset(encodingHeader.getValue());
    }

    private String bitbarCompatibleResultOf(TestOutcome outcome) {
        switch (latestResultOf(outcome)) {
            case FAILURE:
            case ERROR:
            case COMPROMISED:
                return APIDeviceSession.State.FAILED.name();
            case SUCCESS:
            default:
                return APIDeviceSession.State.SUCCEEDED.name();
        }
    }

    private URI getSessionUri() {
        URI uri = null;
        try {
            uri = new URI(webdriverRemoteUrl.replace(WD_HUB, String.format(SESSION_PATH, sessionId)));
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse BitBar API url.", e);
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
