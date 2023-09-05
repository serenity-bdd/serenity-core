package net.thucydides.core.healenium;

import com.epam.healenium.SelfHealingDriver;
import net.thucydides.core.webdriver.healenium.HealeniumWrapper;
import org.junit.Assume;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class WhenUsingHealenium {

    WebDriver driver;

    @BeforeEach
    void initMocks() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
    }

    @Nested
    class WithADefinedConfigurationFile {
        Path healeniumConfig;

        @BeforeEach
        void setupHealeniumProperties() throws IOException {
            healeniumConfig = Paths.get("target/test-classes/healenium.properties");
            Files.writeString(healeniumConfig,
                    "recovery-tries = 1\n" +
                            "score-cap = 0.5\n" +
                            "heal-enabled = true\n" +
                            "hlm.server.url = http://localhost:7878\n" +
                            "hlm.imitator.url = http://localhost:8000");
        }

        @AfterEach
        void deleteHeleniumProperties() throws IOException {
            Files.deleteIfExists(healeniumConfig);
        }

        @Test
        void shouldWrapTheDriverWithAHealeniumDelegate() {
            Assumptions.assumeTrue(heleniumIsRunning());
            WebDriver wrappedDriver = HealeniumWrapper.wrapDriver(driver);

            assertThat(wrappedDriver).isInstanceOf(SelfHealingDriver.class);
        }
    }

    private boolean heleniumIsRunning() {
        String urlString = "http://localhost:8000";
        String expectedResponse = "this is an entry point of the selector imitator";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Check if the response code is OK (200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Check if the response contains the expected text
                return response.toString().contains(expectedResponse);
            }

        } catch (Exception e) {
            // If there's any exception, it means Healenium is probably not running or there's a network issue
            return false;
        }

        return false;
    }

    @Test
    @DisplayName("Don't activate Healenium if there is no healenium.properties file available")
    void withNoHealeniumConfigFile() {
        WebDriver wrappedDriver = HealeniumWrapper.wrapDriver(driver);

        assertThat(driver).isEqualTo(wrappedDriver);
    }
}
