package net.serenitybdd.browserstack;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;

public class AfterABrowserStackScenario implements AfterAWebdriverScenario {

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver))) {
            return;
        }

        try {
            String sessionId = RemoteDriver.of(driver).getSessionId().toString();
//            String browserStackUsername = environmentVariables.getProperty("browserstack.user");
            String browserStackUsername = EnvironmentSpecificConfiguration.from(environmentVariables)
                    .getOptionalProperty("browserstack.user")
                    .orElse(null);

//            String browserStackKey = environmentVariables.getProperty("browserstack.key");
            String browserStackKey = EnvironmentSpecificConfiguration.from(environmentVariables)
                    .getOptionalProperty("browserstack.key")
                    .orElse(null);
            
            URI uri = new URI("https://" + browserStackUsername
                              + ":" + browserStackKey + "@api.browserstack.com/automate/sessions/"
                              + sessionId + ".json");
            HttpPut putRequest = new HttpPut(uri);

            String result = "completed";
            if (testOutcome.isSuccess()) {
                result = "passed";
            } else if (testOutcome.isFailure() || testOutcome.isError() || testOutcome.isCompromised()) {
                result = "failed";
            }
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add((new BasicNameValuePair("status", result)));
            nameValuePairs.add((new BasicNameValuePair("reason", testOutcome.getErrorMessage())));
            putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpClientBuilder.create().build().execute(putRequest);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}