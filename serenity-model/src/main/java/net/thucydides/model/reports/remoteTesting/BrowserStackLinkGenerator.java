package net.thucydides.model.reports.remoteTesting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.net.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Used to generate links to BrowserStack videos when the tests are executed on the BrowserStack servers.
 */
public class BrowserStackLinkGenerator {

    private EnvironmentVariables environmentVariables;
    private String username = null;
    private String accessKey = null;

    //no arg constructor for serialization
    public BrowserStackLinkGenerator() {

    }

    public BrowserStackLinkGenerator(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        getBrowserStackCredentials();
    }

    public String linkFor(TestOutcome testOutcome) {
        if (username != null && accessKey != null) {
            return getVideoLink(testOutcome.getSessionId());
        }
        return null;
    }

    private String getVideoLink(String sessionID) {

        JsonObject browserstackSession = sessionInformation(sessionID);

        if (browserstackSession == null) {
            return null;
        }
        return browserstackSession.get("public_url").getAsString();
    }

    private JsonObject sessionInformation(String sessionID) {
        try {
            URL url = null;
            String respnse = "";
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            url = new URL("https://api.browserstack.com/automate/sessions/" + sessionID + ".json");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Basic " + getBasicAuthenticationEncoding());
            con.setRequestMethod("GET");
            if (200 == con.getResponseCode()) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                JsonObject jsonObject = (JsonObject) JsonParser.parseReader(br);
                return jsonObject.get("automation_session").getAsJsonObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getBasicAuthenticationEncoding() {
        String userPassword = username + ":" + accessKey;
        return new String(Base64.encodeBase64(userPassword.getBytes()));
    }

    private void getBrowserStackCredentials() {
        String url_string = ThucydidesSystemProperty.BROWSERSTACK_URL.from(environmentVariables);

        URL url;
        try {
            url = new URL(url_string);

            String userInfo = url.getUserInfo();
            if (userInfo.split(":").length == 2) {
                username = userInfo.split(":")[0].split("-")[0];
                accessKey = userInfo.split(":")[1];
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
