package net.serenitybdd.browserstack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BrowserStackTestSession {
    private final String browserStackUsername;
    private final String browserStackKey;
    private final String sessionId;

    public BrowserStackTestSession(String browserStackUsername, String browserStackKey, String sessionId) {
        this.browserStackUsername = browserStackUsername;
        this.browserStackKey = browserStackKey;
        this.sessionId = sessionId;
    }

    private static final String BROWSER_SESSION_URL = "https://%s:%s@api.browserstack.com/automate/sessions/%s.json";
    private static final String BUILD_INFO_URL = "https://%s:%s@api.browserstack.com/automate/builds.json";

    private Gson gson = new Gson();

    public void updateTestResultFor(TestOutcome testOutcome) throws URISyntaxException, IOException {
        HttpPut putRequest = new HttpPut(getSessionUri());
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("status", browserstackCompatibleResultOf(testOutcome)));
        nameValuePairs.add(new BasicNameValuePair("reason", testOutcome.getErrorMessage()));
        putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpClientBuilder.create().build().execute(putRequest);
    }

    public String getPublicUrl() throws URISyntaxException, IOException {
        if ((browserStackUsername == null) || (browserStackKey == null)) {
            return "";
        }
        HttpGet querySessionInfo = new HttpGet(getSessionUri());
        HttpEntity sessionDetails = HttpClientBuilder.create().build().execute(querySessionInfo).getEntity();
        String sessionBody = EntityUtils.toString(sessionDetails, charsetOf(sessionDetails));

        JsonElement sessionElement = gson.fromJson(sessionBody, JsonElement.class);
        return sessionElement.getAsJsonObject().get("automation_session")
                             .getAsJsonObject().get("public_url").getAsString();
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


    private URI getSessionUri() throws URISyntaxException {
        return new URI(String.format(BROWSER_SESSION_URL, browserStackUsername, browserStackKey, sessionId));
    }

    private TestResult latestResultOf(TestOutcome outcome) {
        if (outcome.isDataDriven()) {
            return outcome.getLatestTopLevelTestStep().get().getResult();
        } else {
            return outcome.getResult();
        }
    }
}
