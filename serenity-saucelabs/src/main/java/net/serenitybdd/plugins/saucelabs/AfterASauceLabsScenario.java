package net.serenitybdd.plugins.saucelabs;

import com.google.gson.JsonObject;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.serenitybdd.plugins.CapabilityTags;
import net.thucydides.model.domain.ExternalLink;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Base64;

public class AfterASauceLabsScenario implements AfterAWebdriverScenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterASauceLabsScenario.class);

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if (SauceLabsConfiguration.isDriverEnabled(driver) && this.isActivated(environmentVariables)) {
            String sessionId = null;
            if (RemoteDriver.of(driver).getSessionId() != null) {
                sessionId = RemoteDriver.of(driver).getSessionId().toString();
            }
            String userName = SauceLabsCredentials.from(environmentVariables).getUser();
            String key = SauceLabsCredentials.from(environmentVariables).getAccessKey();

            if (userName == null || key == null) {
                LOGGER.warn("Incomplete SauceLabs configuration" + System.lineSeparator()
                        + "SauceLabs integration needs the following system properties to work:" + System.lineSeparator()
                        + "  - saucelabs.username - Your SauceLabs account name" + System.lineSeparator()
                        + "  - saucelabs.accessKey - Your SauceLabs Access Key" + System.lineSeparator()
                        + "You can find both of these here: https://app.saucelabs.com/user-settings"
                );
            } else {
                SauceLabsTestSession sauceLabsTestSession = new SauceLabsTestSession(userName, key, sessionId);

                String publicUrl = sauceLabsTestSession.getTestLink();
                testOutcome.setLink(new ExternalLink(publicUrl, "SauceLabs"));

                String result = (testOutcome.isSuccess()) ? "passed" : "failed";
                String tags = Arrays.toString(CapabilityTags.tagsFrom(testOutcome, environmentVariables));
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    String jobUrl = "https://saucelabs.com/rest/v1/" + userName + "/jobs/" + sessionId;
                    HttpPut putRequest = new HttpPut(jobUrl);
                    putRequest.setHeader("Content-Type", "application/json");
                    putRequest.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((userName + ":" + key).getBytes()));

                    JsonObject json = new JsonObject();
                    json.addProperty("build", BuildName.from(environmentVariables));
                    json.addProperty("tags", tags);
                    json.addProperty("passed", result.equals("passed"));

                    putRequest.setEntity(new StringEntity(json.toString()));

                    httpClient.execute(putRequest);
                } catch (Exception e) {
                    // Handle exception
                }
            }
        }
    }

    @Override
    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return SauceLabsConfiguration.isActiveFor(environmentVariables);
    }
}
