package net.thucydides.core.healenium;

import com.epam.healenium.SelfHealingDriver;
import net.thucydides.core.webdriver.healenium.HealeniumWrapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
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
            WebDriver wrappedDriver = HealeniumWrapper.wrapDriver(driver);

            assertThat(wrappedDriver).isInstanceOf(SelfHealingDriver.class);
        }
    }

    @Test
    @DisplayName("Don't activate Healenium if there is no healenium.properties file available")
    void withNoHealeniumConfigFile() {
        WebDriver wrappedDriver = HealeniumWrapper.wrapDriver(driver);

        assertThat(driver).isEqualTo(wrappedDriver);
    }
}
