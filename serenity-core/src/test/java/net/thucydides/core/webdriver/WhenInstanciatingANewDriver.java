package net.thucydides.core.webdriver;

import com.google.common.collect.Lists;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenInstanciatingANewDriver {

    private WebDriverFactory webDriverFactory;

    @Mock
    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    ChromeDriver chromeDriver;

    @Mock
    SafariDriver safariDriver;

    EnvironmentVariables environmentVariables;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(webdriverInstanceFactory.newChromeDriver(any(Capabilities.class))).thenReturn(chromeDriver);
        when(webdriverInstanceFactory.newSafariDriver(any(Capabilities.class))).thenReturn(safariDriver);

        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);
    }

    @Captor
    ArgumentCaptor<Capabilities> chromeOptionsArgument;

//    @Test
//    public void should_pass_chrome_switches_when_creating_a_chrome_driver() throws Exception {
//        environmentVariables.setProperty("chrome.switches","--no-sandbox,--ignore-certificate-errors,--homepage=about:blank,--no-first-run");
//        webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME);
//
//        verify(webdriverInstanceFactory).newChromeDriver(chromeOptionsArgument.capture());
//        assertThat(argumentsFrom(chromeOptionsArgument), hasItems("--no-sandbox","--ignore-certificate-errors",
//                                                                  "--homepage=about:blank", "--no-first-run"));
//    }

    @Test
    public void should_create_safari_driver_instance() throws Exception {
        webDriverFactory.newInstanceOf(SupportedWebDriver.SAFARI);
        verify(webdriverInstanceFactory).newSafariDriver(any(Capabilities.class));
    }

//    private List<String> argumentsFrom(ArgumentCaptor<Capabilities> chromeOptionsArgument) throws IOException, JSONException {
//        JSONArray argumentsPassed = ((ChromeOptions)chromeOptionsArgument.getValue()
//                                                                         .getCapability("chromeOptions")).toJson().getAsJsonArray();
////                .getJSONArray("args");
//        List<String> arguments = Lists.newArrayList();
//        for(int i = 0; i < argumentsPassed.length(); i++) {
//            arguments.add((String)argumentsPassed.get(i));
//        }
//        return arguments;
//    }

}
