package net.serenitybdd.plugins;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.enhancers.WebDriverManagerEnhancer;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;

public class DefaultWebDriverManagerEnhancer implements WebDriverManagerEnhancer {

    private final EnvironmentVariables environmentVariables;

    public DefaultWebDriverManagerEnhancer() {
        this.environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    private boolean useDocker() {
        return Boolean.parseBoolean(
                EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("webdrivermanager.use.docker").orElse("false")
        );
    }

    @Override
    public WebDriverManager apply(WebDriverManager webDriverManager) {
        if (useDocker()) {
            return webDriverManager.browserInDocker();
        } else {
            return webDriverManager;
        }
    }
}
