package net.serenitybdd.saucelabs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
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

public class SauceLabsTestSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(SauceLabsTestSession.class);
    private static final String BROWSER_SESSION_URL = "https://%s:%s@api.%s/rest/v1/%s/jobs/%s";
    private final String sauceLabsUsername, sauceLabsKey, sessionId, datacenter;
    private final Gson gson = new Gson();

    public SauceLabsTestSession(String datacenter, String sauceLabsUsername, String sauceLabsKey, String sessionId) {
        this.datacenter = datacenter;
        this.sauceLabsUsername = sauceLabsUsername;
        this.sauceLabsKey = sauceLabsKey;
        this.sessionId = sessionId;
    }

    public void updateTestResultFor(TestOutcome testOutcome) {

        try {
            HttpPut putRequest = new HttpPut(getSessionUri());
            // TODO: change to JSON
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("passed", testPassed(testOutcome)));
            putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpClientBuilder.create().build().execute(putRequest);
        } catch (IOException e) {
            LOGGER.error("Failed to update SauceLabs results", e);
        }
    }

    public String getName() {
        return getSessionProperty("name");
    }

    public int getDuration() {
        return Integer.parseInt(getSessionProperty("duration"));
    }

    public String getOS() {
        return getSessionProperty("os");
    }

    public String getOS_Version() {
        return getSessionProperty("os_version");
    }

    public String getBrowserVersion() {
        return getSessionProperty("browser_version");
    }

    public String getBrowser() {
        return getSessionProperty("browser");
    }

    public String getDevice() {
        return getSessionProperty("device");
    }

    public String getStatus() {
        return getSessionProperty("status");
    }

    public String getHashedId() {
        return getSessionProperty("hashed_id");
    }

    public String getReason() {
        return getSessionProperty("reason");
    }

    public String getBuildName() {
        return getSessionProperty("build_name");
    }

    public String getProjectName() {
        return getSessionProperty("project_name");
    }

    public String getTestPriority() {
        return getSessionProperty("test_priority");
    }

    public String getLogs() {
        return getSessionProperty("logs");
    }

    public String getCreatedAt() {
        return getSessionProperty("created_at");
    }

    public String getBrowserUrl() {
        return getSessionProperty("browser_url");
    }

    public String getTestUrl() {
        return String.format("https://%s/tests/%s", datacenter, sessionId);
    }

    public String getAppiumLogsUrl() {
        return getSessionProperty("appium_logs_url");
    }

    public String getVideoUrl() {
        return getSessionProperty("video_url");
    }

    public String getBrowserConsoleLogsUrl() {
        return getSessionProperty("browser_console_logs_url");
    }

    public String getHarLogsUrl() {
        return getSessionProperty("har_logs_url");
    }

    public String getSeleniumLogsUrl() {
        return getSessionProperty("selenium_logs_url");
    }

    private String getSessionProperty(String propertyName) {
        JsonElement sessionElement = null;
        if ((sauceLabsUsername == null) || (sauceLabsKey == null)) {
            return null;
        }

        try {
            HttpGet querySessionInfo = new HttpGet(getSessionUri());
            HttpEntity sessionDetails = HttpClientBuilder.create().build().execute(querySessionInfo).getEntity();
            String sessionBody = EntityUtils.toString(sessionDetails, charsetOf(sessionDetails));
            sessionElement = gson.fromJson(sessionBody, JsonElement.class);
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to connect to SauceLabs API.", e);
        }

        if (sessionElement == null) {
            return null;
        }

        JsonElement automationSession = sessionElement.getAsJsonObject().get("automation_session");
        return automationSession.getAsJsonObject().get(propertyName).getAsString();
    }

    private Charset charsetOf(HttpEntity entity) {
        Header encodingHeader = entity.getContentEncoding();
        return encodingHeader == null ? StandardCharsets.UTF_8 : Charsets.toCharset(encodingHeader.getValue());
    }

    private String testPassed(TestOutcome outcome) {
        return Boolean.toString(latestResultOf(outcome) == TestResult.SUCCESS);
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
