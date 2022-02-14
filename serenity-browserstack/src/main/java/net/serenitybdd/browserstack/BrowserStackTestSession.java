package net.serenitybdd.browserstack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
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

public class BrowserStackTestSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserStackTestSession.class);
    private static final String BROWSER_SESSION_URL = "https://%s:%s@api.browserstack.com/automate/sessions/%s.json";
    private static final String BUILD_INFO_URL = "https://%s:%s@api.browserstack.com/automate/builds.json";
    private final String browserStackUsername;
    private final String browserStackKey;
    private final String sessionId;
    private final Gson gson = new Gson();

    public BrowserStackTestSession(String browserStackUsername, String browserStackKey, String sessionId) {
        this.browserStackUsername = browserStackUsername;
        this.browserStackKey = browserStackKey;
        this.sessionId = sessionId;
    }

    public void updateTestResultFor(TestOutcome testOutcome) {

        try {
            HttpPut putRequest = new HttpPut(getSessionUri());
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("status", browserstackCompatibleResultOf(testOutcome)));
            nameValuePairs.add(new BasicNameValuePair("reason", testOutcome.getErrorMessage()));
            putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpClientBuilder.create().build().execute(putRequest);
        }
        catch (IOException e) {
            LOGGER.error("Failed to update Browserstack results", e);
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

    public String getBrowserstackStatus() {
        return getSessionProperty("browserstack_status");
    }

    public String getCreatedAt() {
        return getSessionProperty("created_at");
    }

    public String getBrowserUrl() {
        return getSessionProperty("browser_url");
    }

    public String getPublicUrl() {
        return getSessionProperty("public_url");
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
        if ((browserStackUsername == null) || (browserStackKey == null)) {
            return null;
        }

        try {
            HttpGet querySessionInfo = new HttpGet(getSessionUri());
            HttpEntity sessionDetails = HttpClientBuilder.create().build().execute(querySessionInfo).getEntity();
            String sessionBody = EntityUtils.toString(sessionDetails, charsetOf(sessionDetails));
            sessionElement = gson.fromJson(sessionBody, JsonElement.class);
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to connect to Browserstack API.", e);
        }

        if (sessionElement == null) {
            return null;
        }

        JsonElement automationSession = sessionElement.getAsJsonObject().get("automation_session");
        JsonElement element = automationSession.getAsJsonObject().get(propertyName);
        return element.isJsonNull() ? null : element.getAsString();
    }


    private Charset charsetOf(HttpEntity entity) {
        Header encodingHeader = entity.getContentEncoding();
        return encodingHeader == null ? StandardCharsets.UTF_8 : Charsets.toCharset(encodingHeader.getValue());
    }

    private String browserstackCompatibleResultOf(TestOutcome outcome) {
        switch (latestResultOf(outcome)) {
            case SUCCESS:
                return "passed";
            case FAILURE:
            case ERROR:
            case COMPROMISED:
                return "failed";
            default:
                return "completed";
        }
    }


    private URI getSessionUri() {
        URI uri = null;
        try {
            uri = new URI(String.format(BROWSER_SESSION_URL, browserStackUsername, browserStackKey, sessionId));
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse Browserstack API url.", e);
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
